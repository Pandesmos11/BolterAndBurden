/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Interactive entry point for the full playable game. Presents a
 *              title screen, prompts the player to choose a faction, then drives
 *              a three-act campaign chain: the player makes real narrative choices
 *              at each branch, fights turn-by-turn skirmishes, and watches their
 *              warband earn XP and rank up across all three acts. Warband state
 *              (wounds, XP, rank, wargear) persists between acts. Units possessed
 *              by daemons accumulate in a shared enemy roster across the whole
 *              campaign. The final screen shows survivors ranked by XP and any
 *              units lost to daemonic possession.
 *              To run the automated test suite instead, run Main.java.
 * Inputs:      Keyboard input via Scanner (faction choice 1-3, branch choices
 *              1-2, target selection during combat, Enter to advance)
 * Outputs:     Full campaign narrative, combat log, progression events, and
 *              final ranked results to System.out
 *
 * Change Log:
 *   2026-03-24  Shane Potts  Initial creation - Phase 3 interactive game mode
 *   2026-04-08  Shane Potts  Expanded to three-act campaign chain with persistent
 *                             warband state, act transition screens, and shared
 *                             possessed warband across acts
 *   2026-04-08  Shane Potts  Added save/load via SaveManager: startup Continue/New
 *                             Game menu, auto-save after each act, save deletion
 *                             on campaign completion
 *   2026-04-09  Shane Potts  Integrated Detachment selection: detachment created
 *                             from faction name after warband creation and passed
 *                             through runCampaignChain → CampaignEngine.setDetachment
 *   2026-04-09  Shane Potts  Expanded to three detachments per faction; player
 *                             prompted to select one after faction choice via
 *                             selectDetachment(), which shows passive rule and full
 *                             stratagem hand for each option before asking for input
 *   2026-04-26  Shane Potts  Phase 3 complete - full interactive campaign playable;
 *                             title screen → save check → faction → detachment →
 *                             three-act chain → final results; auto-save after each
 *                             act; save deleted on campaign completion
 */

package bolterandburden;

import java.util.ArrayList;
import java.util.Scanner;

public class Game {

    private Scanner scanner;

    // -------------------------------------------------------------------------
    // Entry Point
    // -------------------------------------------------------------------------

    /**
     * Launches the interactive game. Run this class to play; run Main to test.
     *
     * @param args Unused
     */
    public static void main(String[] args) {
        new Game().start();
    }

    public Game() {
        scanner = new Scanner(System.in);
    }

    // -------------------------------------------------------------------------
    // Game Loop
    // -------------------------------------------------------------------------

    /**
     * Top-level game flow: title → save-check (Continue/New Game) → faction
     * select (if new) → warband display → three-act campaign chain → final results.
     * Auto-saves after each act; deletes the save file on campaign completion.
     */
    public void start() {
        printTitle();

        Warband    playerWarband;
        Warband    possessedWarband;
        Detachment detachment;
        int        startActIndex = 0;    // 0-based index into the acts array

        if (SaveManager.saveExists()) {
            System.out.println(Color.c("  A saved campaign was found.", Color.CYAN));
            System.out.println("    [1] Continue saved campaign");
            System.out.println("    [2] Start a new game  (saved campaign will be deleted)");
            System.out.println();

            String choice = "";
            while (!choice.equals("1") && !choice.equals("2")) {
                System.out.print("  Enter 1 or 2: ");
                choice = scanner.nextLine().trim();
            }

            if (choice.equals("1")) {
                SaveManager.SaveData data = SaveManager.load();
                if (data == null) {
                    System.out.println("  [LOAD] Save file is corrupt. Starting a new game.");
                    playerWarband    = chooseFaction();
                    possessedWarband = buildFreshPossessedWarband();
                } else {
                    playerWarband    = data.playerWarband;
                    possessedWarband = data.possessedWarband;
                    startActIndex    = data.nextAct - 1;   // nextAct is 1-based
                    System.out.println();
                    System.out.println(Color.c("  Campaign resumed at Act " + data.nextAct + ".", Color.CYAN));
                }
            } else {
                SaveManager.deleteSave();
                playerWarband    = chooseFaction();
                possessedWarband = buildFreshPossessedWarband();
            }
        } else {
            playerWarband    = chooseFaction();
            possessedWarband = buildFreshPossessedWarband();
        }

        // Let the player choose their detachment for the chosen faction
        detachment = selectDetachment(playerWarband);

        System.out.println();
        System.out.println("  Your warband stands ready. Study your warriors:");
        System.out.println();
        playerWarband.displayRoster();

        // Display assigned detachment and its stratagems
        System.out.println();
        System.out.println(Color.c("  ── DETACHMENT: " + detachment.getDetachmentName()
                + " ──", Color.BOLD + Color.CYAN));
        System.out.println(Color.c("  Passive Rule: " + detachment.getPassiveRuleName(),
                Color.CYAN));
        System.out.println(Color.c("  Stratagems available each combat:", Color.CYAN));
        for (Stratagem s : detachment.getStratagems()) {
            System.out.println(Color.c("    [" + s.getCost() + " CP] ", Color.YELLOW)
                    + Color.c(s.getName(), Color.BOLD) + " — " + s.getDescription());
        }
        System.out.println(Color.c("  CP pool:  Act I=4  Act II=6  Act III=9  (+1 per round)",
                Color.CYAN));

        pause("\n  Press ENTER to march to war...");

        runCampaignChain(playerWarband, possessedWarband, detachment, startActIndex);

        printFinalResults(playerWarband, possessedWarband);
        SaveManager.deleteSave();   // Campaign over — clean slate for next run
        scanner.close();
    }

    /**
     * Constructs a fresh empty possessed-warband for a new campaign.
     *
     * @return An empty Warband under the Chaos Daemons faction
     */
    private Warband buildFreshPossessedWarband() {
        Faction daemonFaction = new Faction("Chaos Daemons", true);
        return new Warband("The Possessed", daemonFaction);
    }

    // -------------------------------------------------------------------------
    // Campaign Chain
    // -------------------------------------------------------------------------

    /**
     * Runs acts from startActIndex through the end of the campaign, passing
     * the same warband, possessed roster, and detachment through each. Prints
     * act title screens and transition banners between acts. Auto-saves after
     * each completed act. Breaks early if the player warband is wiped out.
     *
     * @param playerWarband    The player's warband (state persists across acts)
     * @param possessedWarband Shared enemy roster for daemon-possessed units
     * @param detachment       The player's faction Detachment (persists across acts)
     * @param startActIndex    0-based index of the first act to run (0 = Act 1)
     */
    private void runCampaignChain(Warband playerWarband, Warband possessedWarband,
                                  Detachment detachment, int startActIndex) {
        String[] actNames = {
            "ACT I   — The Hunt Begins",
            "ACT II  — The Manufactorum Wars",
            "ACT III — The Traitor's Throne"
        };

        CampaignNode[] acts = {
            CampaignEngine.buildActOneCampaign(),
            CampaignEngine.buildActTwoCampaign(),
            CampaignEngine.buildActThreeCampaign()
        };

        for (int i = startActIndex; i < acts.length; i++) {
            printActTitle(actNames[i]);
            pause("  Press ENTER to begin...");

            CampaignEngine engine = new CampaignEngine(acts[i], playerWarband);
            engine.setPossessedWarband(possessedWarband);
            engine.setScanner(scanner);
            engine.setActNumber(i + 1);   // 1-based: Act I=1, Act II=2, Act III=3
            engine.setDetachment(detachment);
            engine.startCampaign();

            if (playerWarband.isDefeated()) {
                System.out.println();
                System.out.println(Color.c(
                        "  Your warband is annihilated. The darkness claims all.",
                        Color.BOLD + Color.RED));
                break;
            }

            if (i < acts.length - 1) {
                // Auto-save after completing an act (nextAct is 1-based: i+2)
                SaveManager.save(playerWarband, possessedWarband, i + 2);
                printActTransition(i + 1, actNames[i + 1]);
                pause("  Press ENTER to continue...");
            }
        }
    }

    // -------------------------------------------------------------------------
    // Faction Selection
    // -------------------------------------------------------------------------

    /**
     * Displays the three faction options and returns the warband created by the
     * matching FactionFactory method. Loops until a valid choice is entered.
     *
     * @return A fully constructed Warband for the chosen faction
     */
    private Warband chooseFaction() {
        System.out.println(Color.c("  SELECT YOUR FACTION:\n", Color.BOLD));

        System.out.println(Color.c("    [1] DARK ANGELS  [Loyalist]", Color.CYAN));
        System.out.println("        12 units. The Lion and Azrael lead. Three squads of five");
        System.out.println("        Deathwing Knights (Sv2+, W15 each) are each led by a");
        System.out.println("        Terminator Chaplain, Captain, and Librarian. Ten Hellblasters");
        System.out.println("        pour plasma fire from the line. A Judiciar commands the");
        System.out.println("        Inner Circle Companions, while Scouts hold the flanks.");
        System.out.println();

        System.out.println(Color.c("    [2] EMPEROR'S CHILDREN  [Chaos]", Color.MAGENTA));
        System.out.println("        7 units. Fulgrim leads (T7, W9, 4 warp charge). Lucius");
        System.out.println("        the Eternal strikes 9+2 times. The Lord Cacophonist");
        System.out.println("        drives ten Noise Marines into sonic frenzy. Infractors");
        System.out.println("        sprint at speed 7 and Tormentors embrace every wound.");
        System.out.println("        Daemon Prince Xerathul flies at speed 12.");
        System.out.println();

        System.out.println(Color.c("    [3] CHAOS KNIGHTS  [Chaos]", Color.RED));
        System.out.println("        8 god-machines. The Cerastus Lancer charges at speed 14.");
        System.out.println("        The Tyrant of Ruin (W22) holds the centre. Twin-gatling");
        System.out.println("        Despoilers Wrath Eternal and Ruinous Tide fill the air");
        System.out.println("        with shells. Vextrix the Abominant (T12, W18) anchors");
        System.out.println("        the line. Karnivores Bloodfang and Bonecleaver hunt.");
        System.out.println("        War Dog Stalker Darkfire suppresses the rear.");
        System.out.println();

        while (true) {
            System.out.print("  Enter 1, 2, or 3: ");
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1": return FactionFactory.createDarkAngelsWarband();
                case "2": return FactionFactory.createEmperorsChildrenWarband();
                case "3": return FactionFactory.createChaosKnightsWarband();
                default:
                    System.out.println("  Invalid choice - enter 1, 2, or 3.");
            }
        }
    }

    // -------------------------------------------------------------------------
    // Detachment Assignment
    // -------------------------------------------------------------------------

    /**
     * Presents the three detachments available to the chosen faction and returns
     * the one selected by the player. Prints the passive rule name and full
     * stratagem hand (with CP costs) for each option before prompting.
     * Loops until a valid choice (1, 2, or 3) is entered.
     *
     * @param warband The player's warband (faction determines the option set)
     * @return The selected Detachment
     */
    private Detachment selectDetachment(Warband warband) {
        String factionName = warband.getFaction().getFactionName();

        // Build the three options for this faction
        Detachment[] options;
        if (factionName.contains("Dark Angels")) {
            options = new Detachment[] {
                Detachment.createDarkAngelsDetachment(),
                Detachment.createDarkAngelsUnforgivenDetachment(),
                Detachment.createDarkAngelsDeathwingDetachment()
            };
        } else if (factionName.contains("Emperor's Children")) {
            options = new Detachment[] {
                Detachment.createEmperorsChildrenDetachment(),
                Detachment.createEmperorsChildrenChosenDetachment(),
                Detachment.createEmperorsChildrenTormentDetachment()
            };
        } else if (factionName.contains("Chaos Knights")) {
            options = new Detachment[] {
                Detachment.createChaosKnightsDetachment(),
                Detachment.createChaosKnightsIconoclastDetachment(),
                Detachment.createChaosKnightsInfernalCourtDetachment()
            };
        } else {
            return Detachment.createDarkAngelsDetachment(); // fallback
        }

        System.out.println();
        System.out.println(Color.c("  SELECT YOUR DETACHMENT:\n", Color.BOLD));
        System.out.println("  Your detachment determines your passive battlefield rule and");
        System.out.println("  the six stratagems available to spend Command Points on each combat.");
        System.out.println();

        // Print all three options with their passives and stratagem hands
        for (int i = 0; i < options.length; i++) {
            Detachment d = options[i];
            System.out.println(Color.c("    [" + (i + 1) + "] " + d.getDetachmentName().toUpperCase(),
                    Color.BOLD + Color.CYAN));
            System.out.println(Color.c("        Passive: " + d.getPassiveRuleName(), Color.CYAN));
            System.out.println("        Stratagems:");
            for (Stratagem s : d.getStratagems()) {
                System.out.println("          " + Color.c("[" + s.getCost() + " CP]", Color.YELLOW)
                        + " " + s.getName() + " — " + s.getDescription());
            }
            System.out.println();
        }

        // Loop until valid input
        while (true) {
            System.out.print("  Enter 1, 2, or 3: ");
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1": return options[0];
                case "2": return options[1];
                case "3": return options[2];
                default:
                    System.out.println("  Invalid choice — enter 1, 2, or 3.");
            }
        }
    }

    // -------------------------------------------------------------------------
    // Final Results Screen
    // -------------------------------------------------------------------------

    /**
     * Displays the post-campaign summary: any daemon-possessed units that
     * turned against the player across all three acts, then surviving units
     * ranked by total XP earned across the full campaign.
     *
     * @param playerWarband   The player's warband after all acts
     * @param possessedWarband The accumulated possessed enemy roster
     */
    private void printFinalResults(Warband playerWarband, Warband possessedWarband) {
        System.out.println();
        System.out.println(Color.c("  ╔══════════════════════════════════════════════════╗", Color.BOLD + Color.CYAN));
        System.out.println(Color.c("  ║   CAMPAIGN RESULTS                               ║", Color.BOLD + Color.CYAN));
        System.out.println(Color.c("  ╚══════════════════════════════════════════════════╝", Color.BOLD + Color.CYAN));

        if (possessedWarband.getRosterSize() > 0) {
            System.out.println();
            System.out.println(Color.c("  FALLEN TO DARKNESS:", Color.BOLD + Color.RED));
            possessedWarband.displayRoster();
        }

        ArrayList<Unit> survivors = playerWarband.getRosterSnapshot();
        System.out.println();
        if (!survivors.isEmpty()) {
            System.out.println(Color.c("  SURVIVORS - ranked by XP earned:", Color.BOLD + Color.GREEN));
            UnitSorter.sort(survivors, "xp");
            UnitSorter.displaySorted(survivors, "xp");
        } else {
            System.out.println("  No survivors. The darkness claims all.");
        }

        System.out.println();
        System.out.println(Color.c("  Glory and death, warrior. Until next campaign.", Color.YELLOW));
        System.out.println("  " + "=".repeat(50));
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    /**
     * Prints the game title screen.
     */
    private void printTitle() {
        System.out.println();
        System.out.println(Color.c("  " + "=".repeat(52), Color.BOLD + Color.CYAN));
        System.out.println();
        System.out.println(Color.c("      B O L T E R   &   B U R D E N", Color.BOLD + Color.CYAN));
        System.out.println(Color.c("    T H E   T R A I T O R ' S   T H R O N E", Color.BOLD + Color.CYAN));
        System.out.println();
        System.out.println(Color.c("  " + "=".repeat(52), Color.BOLD + Color.CYAN));
        System.out.println();
        System.out.println(Color.c("      A Warhammer-Inspired Text Strategy Game", Color.CYAN));
        System.out.println(Color.c("      CSC 1061  --  FRCC Spring 2026", Color.CYAN));
        System.out.println();
        System.out.println(Color.c("  " + "=".repeat(52), Color.BOLD + Color.CYAN));
        System.out.println();
    }

    /**
     * Prints a between-act title banner for the given act name.
     *
     * @param actName Full act label (e.g. "ACT I   — The Hunt Begins")
     */
    private void printActTitle(String actName) {
        System.out.println();
        System.out.println(Color.c("  ╔══════════════════════════════════════════════════╗", Color.BOLD + Color.YELLOW));
        System.out.println(Color.c("  ║   " + padRight(actName, 47) + "║", Color.BOLD + Color.YELLOW));
        System.out.println(Color.c("  ╚══════════════════════════════════════════════════╝", Color.BOLD + Color.YELLOW));
        System.out.println();
    }

    /**
     * Prints an inter-act transition screen after completing an act.
     *
     * @param completedActNum The 1-based number of the act just completed
     * @param nextActName     Full label of the upcoming act
     */
    private void printActTransition(int completedActNum, String nextActName) {
        System.out.println();
        System.out.println(Color.c("  ── ACT " + completedActNum + " COMPLETE ──────────────────────────────────", Color.CYAN));
        System.out.println();
        System.out.println("  Your warband catches its breath. The fallen are honored.");
        System.out.println("  Wounds, experience, and wargear carry forward.");
        System.out.println("  The enemy has not stopped moving.");
        System.out.println();
        System.out.println(Color.c("  NEXT: " + nextActName, Color.BOLD));
        System.out.println();
    }

    /**
     * Displays a prompt and waits for the player to press Enter.
     *
     * @param prompt Text to show before waiting
     */
    private void pause(String prompt) {
        System.out.print(prompt);
        scanner.nextLine();
    }

    /**
     * Right-pads a string with spaces to the given total length.
     *
     * @param s      String to pad
     * @param length Target total length
     * @return Padded string
     */
    private String padRight(String s, int length) {
        if (s.length() >= length) return s.substring(0, length);
        return s + " ".repeat(length - s.length());
    }
}

/*
 * Change Log:
 *   2026-03-24  Shane Potts  Initial creation - Phase 3 interactive game mode;
 *                             title screen, faction select, single-act campaign chain
 *   2026-04-08  Shane Potts  Expanded to three-act campaign chain with persistent
 *                             warband state, act transition screens, and shared
 *                             possessed warband across acts
 *   2026-04-08  Shane Potts  Added save/load via SaveManager: startup Continue/New
 *                             Game menu, auto-save after each act, save deletion
 *                             on campaign completion
 *   2026-04-09  Shane Potts  Integrated Detachment selection: detachment created
 *                             from faction name after warband creation and passed
 *                             through runCampaignChain → CampaignEngine.setDetachment
 *   2026-04-09  Shane Potts  Expanded to three detachments per faction; player
 *                             prompted to select one after faction choice via
 *                             selectDetachment(), which shows passive rule and full
 *                             stratagem hand for each option before asking for input
 *   2026-04-26  Shane Potts  Phase 3 complete - full interactive campaign playable;
 *                             title screen → save check → faction → detachment →
 *                             three-act chain → final results; auto-save after each
 *                             act; save deleted on campaign completion
 */
