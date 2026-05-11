/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Drives the campaign narrative by recursively traversing the
 *              binary CampaignNode tree. The core method navigate(CampaignNode)
 *              is called once with the root node. At each non-leaf node it
 *              prints the encounter, resolves the choice (from a pre-set
 *              navigation path string for automated/test runs), and recurses
 *              into the chosen child. The base case triggers when navigate()
 *              receives a leaf node: the terminal encounter is resolved,
 *              XP and wargear rewards are applied to all surviving units, and
 *              the method returns without further recursion.
 *              Combat nodes hand off to an embedded CombatEngine instance.
 *              Possession events flag the unit, remove it from the player
 *              roster, and re-insert it into a persistent possessedWarband
 *              ("The Possessed") that represents enemy-controlled daemon hosts
 *              for the remainder of the campaign.
 * Inputs:      Root CampaignNode, player Warband; navigation path String
 *              (optional, defaults to always-left)
 * Outputs:     Full encounter narrative to console; XP/wargear distributed;
 *              permanent consequences applied to Warband state; possessed units
 *              transferred to enemy-controlled possessedWarband
 *
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 3
 *   2026-03-24  Shane Potts  Phase 3 fix - added possessedWarband so daemonic
 *                             possession converts the unit to an enemy-controlled
 *                             entry rather than simply removing it from play
 *   2026-03-24  Shane Potts  Interactive mode - added Scanner field; gated
 *                             player-choice prompts, pre-combat ability menu,
 *                             real CombatEngine skirmishes, and Enter-to-continue
 *                             pauses behind (scanner != null) so automated tests
 *                             are completely unaffected
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-08  Shane Potts  Added setPossessedWarband() for cross-act possession persistence
 *   2026-04-08  Shane Potts  Added buildActOneCampaign(), buildActTwoCampaign(),
 *                             buildActThreeCampaign() — deeper 3-4 level trees for
 *                             campaign chaining in Game.java
 *   2026-04-09  Shane Potts  Wired Detachment / CP / Stratagem system into
 *                             resolveCombat(): fresh CommandPoints per engagement,
 *                             detachment passive + stratagem hand passed to
 *                             CombatEngine; Stratagems reset between encounters
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; drives all three
 *                             acts, each with a 3-4 level binary narrative tree;
 *                             possession events persist to possessedWarband across acts
 */

package bolterandburden;

import java.util.ArrayList;
import java.util.Scanner;

public class CampaignEngine {

    private CampaignNode root;
    private Warband playerWarband;

    /**
     * Enemy-controlled warband that receives all daemon-possessed units.
     * Units removed from playerWarband via possession are re-inserted here,
     * making them available as hostile combatants for subsequent encounters.
     */
    private Warband possessedWarband;

    private int encountersVisited;
    private int totalXpAwarded;

    /**
     * Navigation path used in automated / test mode. Each character is 'L' or 'R'.
     * The engine takes one character per branch-point node encountered.
     * Defaults to all-left if the path is exhausted or not set.
     */
    private String navigationPath;
    private int pathIndex;

    /**
     * Current act number (1, 2, or 3). Controls which Genestealer Cults
     * enemy warband is spawned and how many combat rounds are allowed.
     * Set via setActNumber() from Game before startCampaign() is called.
     */
    private int actNumber;

    /**
     * The player's faction Detachment. When non-null, its passive rule is
     * applied at the start of each combat and its Stratagems are offered via
     * the CombatEngine stratagem menu. Null in automated/test mode.
     */
    private Detachment detachment;

    /**
     * When non-null, enables interactive mode: choice prompts, pre-combat
     * ability menus, and Enter-to-continue pauses are shown to the player.
     * Null in automated/test mode - no output or behaviour changes.
     */
    private Scanner scanner;

    // -------------------------------------------------------------------------
    // Construction
    // -------------------------------------------------------------------------

    /**
     * Constructs a CampaignEngine with a campaign tree and the player's warband.
     * Navigation defaults to always taking the left branch until setNavigationPath()
     * is called.
     *
     * @param root          Root node of the campaign binary tree
     * @param playerWarband The warband whose units receive XP/wargear from encounters
     */
    public CampaignEngine(CampaignNode root, Warband playerWarband) {
        this.root            = root;
        this.playerWarband   = playerWarband;
        this.encountersVisited = 0;
        this.totalXpAwarded    = 0;
        this.navigationPath    = "";
        this.pathIndex         = 0;
        this.scanner           = null;
        this.actNumber         = 1;
        this.detachment        = null;

        // Build the enemy-controlled warband that will receive possessed units
        Faction daemonFaction = new Faction("Chaos Daemons", true);
        this.possessedWarband = new Warband("The Possessed", daemonFaction);
    }

    /**
     * Replaces the internal possessed warband with an externally managed one.
     * Call this when chaining multiple campaigns so that units possessed in
     * earlier acts continue to appear in the same enemy roster throughout.
     *
     * @param wb The shared possessed Warband to use for this campaign
     */
    public void setPossessedWarband(Warband wb) {
        this.possessedWarband = wb;
    }

    /**
     * Enables interactive (game) mode. When set, navigate() will prompt the
     * player for branch choices, offer pre-combat ability use, and pause for
     * Enter key presses between encounters.
     * Pass null to revert to automated mode.
     *
     * @param scanner An open Scanner connected to System.in, or null
     */
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Sets the act number for this campaign run (1, 2, or 3). Controls which
     * Genestealer Cults enemy warband is generated for combat encounters and
     * how many combat rounds are allowed per engagement.
     *
     * @param actNumber 1-based act index
     */
    public void setActNumber(int actNumber) {
        this.actNumber = actNumber;
    }

    /**
     * Sets the player's faction Detachment for this campaign act. When set,
     * the detachment passive rule is applied at the start of each combat and
     * the Stratagem hand is offered to the player each round via CombatEngine.
     * Pass null to disable detachment features (automated / test mode).
     *
     * @param detachment The player's Detachment, or null
     */
    public void setDetachment(Detachment detachment) {
        this.detachment = detachment;
    }

    /**
     * Sets the pre-defined navigation path for automated traversal.
     * Each 'L' or 'R' character in the string corresponds to one branching
     * choice, consumed in order. Resets the path index to 0.
     *
     * @param path String of 'L'/'R' characters (e.g., "LRL")
     */
    public void setNavigationPath(String path) {
        this.navigationPath = path.toUpperCase();
        this.pathIndex      = 0;
    }

    // -------------------------------------------------------------------------
    // Campaign Entry Point
    // -------------------------------------------------------------------------

    /**
     * Starts campaign traversal from the root node.
     * Resets the encounter counter and path index before beginning.
     */
    public void startCampaign() {
        encountersVisited = 0;
        totalXpAwarded    = 0;
        pathIndex         = 0;

        System.out.println(Color.c("  ╔══════════════════════════════════════════════════╗", Color.BOLD + Color.CYAN));
        System.out.println(Color.c("  ║   CAMPAIGN BEGINS: " + playerWarband.getWarbandName(), Color.BOLD + Color.CYAN));
        System.out.println(Color.c("  ╚══════════════════════════════════════════════════╝", Color.BOLD + Color.CYAN) + "\n");

        navigate(root);

        System.out.println();
        System.out.println(Color.c("  ╔══════════════════════════════════════════════════╗", Color.CYAN));
        System.out.println(Color.c("  ║   CAMPAIGN COMPLETE", Color.CYAN));
        System.out.println(Color.c("  ║   Encounters visited : " + encountersVisited, Color.CYAN));
        System.out.println(Color.c("  ║   Total XP awarded   : " + totalXpAwarded, Color.CYAN));
        System.out.println(Color.c("  ╚══════════════════════════════════════════════════╝", Color.CYAN));
    }

    // -------------------------------------------------------------------------
    // Recursive Campaign Traversal - the core Phase 3 algorithm
    // -------------------------------------------------------------------------

    /**
     * Recursively traverses the campaign tree from the given node.
     *
     * <p><b>Base case:</b> {@code node} is a leaf (no children). The terminal
     * encounter is resolved: narrative is printed, XP and wargear rewards are
     * distributed, and the method returns without further recursion.
     *
     * <p><b>Recursive case:</b> {@code node} is an internal node. The encounter
     * narrative is printed and a path choice is made. navigate() then calls
     * itself with either {@code node.getLeft()} or {@code node.getRight()},
     * consuming one level of the campaign tree per call.
     *
     * @param node The current campaign node being processed
     */
    public void navigate(CampaignNode node) {
        if (node == null) {
            return;
        }

        encountersVisited++;
        String depth = "  ".repeat(encountersVisited);

        // Print encounter header
        System.out.println(depth + "[ ENCOUNTER " + encountersVisited + " ]  "
                + node.getEncounterTitle()
                + (node.isCombatEncounter() ? "  ⚔" : "")
                + (node.isLeaf()            ? "  [TERMINAL]" : ""));
        System.out.println(depth + node.getEncounterDescription());

        // --- Base Case: terminal leaf node ---
        if (node.isLeaf()) {
            resolveTerminalEncounter(node, depth);
            return;    // No further recursion
        }

        // --- Recursive Case: internal node ---
        // Resolve any immediate effects at this node (e.g., combat)
        if (node.isCombatEncounter()) {
            resolveCombat(node, depth);
        }

        // Award XP for passing through this node
        if (node.getXpReward() > 0) {
            awardXP(node.getXpReward(), depth);
        }

        // In interactive mode show warband health and both labelled choices
        if (scanner != null) {
            printWarbandStatus(depth);
            System.out.println();
            System.out.println(depth + "  What do you do?");
            System.out.println(depth + "  [1] " + node.getLeftChoiceLabel());
            System.out.println(depth + "  [2] " + node.getRightChoiceLabel());
        }

        // Make a navigation choice and recurse into the chosen child
        char direction = nextDirection();
        if (direction == 'L') {
            System.out.println(depth + ">> Decision: [" + node.getLeftChoiceLabel() + "]");
            System.out.println();
            navigate(node.getLeft());        // RECURSIVE CALL - left branch
        } else {
            System.out.println(depth + ">> Decision: [" + node.getRightChoiceLabel() + "]");
            System.out.println();
            navigate(node.getRight());       // RECURSIVE CALL - right branch
        }
    }

    // -------------------------------------------------------------------------
    // Encounter Resolution Helpers
    // -------------------------------------------------------------------------

    /**
     * Resolves a terminal (leaf) encounter. Prints the outcome narrative,
     * distributes XP, awards wargear if present, and checks for possession
     * consequences on any corruption-maxed units.
     *
     * @param node  The terminal CampaignNode being resolved
     * @param depth Indentation prefix for console output
     */
    private void resolveTerminalEncounter(CampaignNode node, String depth) {
        System.out.println(depth + ">>> Terminal encounter resolves.");

        if (node.isCombatEncounter()) {
            resolveCombat(node, depth);
        }

        if (node.getXpReward() > 0) {
            awardXP(node.getXpReward(), depth);
        }

        if (node.hasWargearReward()) {
            System.out.println(Color.c(depth + ">>> Wargear acquired: [" + node.getWargearReward() + "]", Color.YELLOW));
            for (Unit u : playerWarband.getLivingUnits()) {
                u.getWargear().addEquipment(node.getWargearReward());
                System.out.println(depth + "    " + u.getName() + " receives " + node.getWargearReward());
                break; // Award to first surviving unit only
            }
        }

        // Check for possession on any high-corruption unit
        checkPossessionConsequences(depth);

        // In interactive mode show final warband status and wait for player
        if (scanner != null) {
            System.out.println();
            printWarbandStatus(depth);
            pause(depth + "Press ENTER to continue...");
        }
    }

    /**
     * Resolves a combat encounter. In automated mode applies simple attrition.
     * In interactive mode spawns a real enemy warband, offers the player a
     * pre-combat psychic ability, then runs a full CombatEngine skirmish.
     *
     * @param node  The CampaignNode whose combat is being resolved
     * @param depth Indentation prefix for console output
     */
    private void resolveCombat(CampaignNode node, String depth) {
        System.out.println(depth + ">>> Combat erupts! Warband engages the enemy...");

        if (scanner != null) {
            // Interactive mode: real GSC enemy warband, real CombatEngine skirmish
            Warband enemies = createEncounterEnemies();

            // Announce the cult force with act-appropriate flavour
            String[] cultAnnouncements = {
                "A Genestealer Cults vanguard emerges from the shadows — a probing force, but far from harmless.",
                "The infestation rises. A full Genestealer Cults strike force pours from hidden tunnels.",
                "THE GREAT RISING. Every cult asset commits. There is no holding this tide."
            };
            int actIdx = Math.max(0, Math.min(actNumber - 1, 2));
            System.out.println();
            System.out.println(Color.c(depth + "  " + cultAnnouncements[actIdx],
                    Color.BOLD + Color.MAGENTA));
            System.out.println();
            System.out.println(depth + "    Enemy force: "
                    + Color.c(enemies.getWarbandName(), Color.BOLD + Color.MAGENTA));
            enemies.displayRoster();
            System.out.println();

            offerPreCombatAbilities(depth, enemies);

            // Reset Stratagem used-flags so the full hand is available this combat
            if (detachment != null) detachment.resetForNewCombat();

            // CP pool scales with act: 4 / 6 / 9 starting CP
            int[] startingCp = { 4, 6, 9 };
            CommandPoints cp = new CommandPoints(startingCp[Math.max(0, Math.min(actNumber - 1, 2))]);

            // Standard Warhammer 40,000 engagement: always 5 rounds
            int maxRounds = 5;

            CombatEngine combatEngine = new CombatEngine(playerWarband, enemies);
            combatEngine.setScanner(scanner);
            combatEngine.setDetachment(detachment);
            combatEngine.setCommandPoints(cp);
            combatEngine.runCombat(maxRounds);

            if (playerWarband.isDefeated()) {
                System.out.println();
                System.out.println(Color.c(depth
                        + "  !! YOUR WARBAND IS DESTROYED. The darkness claims you. !!",
                        Color.BOLD + Color.RED));
            }
            pause(depth + "Press ENTER to continue...");
        } else {
            // Automated mode: simple attrition (keeps tests deterministic)
            ArrayList<Unit> living = playerWarband.getLivingUnits();
            for (Unit u : living) {
                System.out.print(depth + "    ");
                u.applyDamage(1);
                u.loseMorale(1);
            }
        }
    }

    /**
     * Awards XP to every surviving unit in the player's warband and prints
     * the narrative message. Delegates to Unit.gainXP() so progression checks
     * fire automatically.
     *
     * @param amount Amount of XP to award per unit
     * @param depth  Indentation prefix for console output
     */
    private void awardXP(int amount, String depth) {
        totalXpAwarded += amount * playerWarband.getLivingUnits().size();
        System.out.println(Color.c(depth + ">>> Each survivor earns " + amount + " XP.", Color.YELLOW));
        for (Unit u : playerWarband.getLivingUnits()) {
            u.gainXP(amount);
        }
    }

    /**
     * Checks all warband units for maximum corruption. Any unit whose
     * corruption has reached or exceeded the major threshold triggers
     * daemonic possession: the unit is flagged, removed from the player
     * roster, and inserted into possessedWarband as an enemy-controlled entry.
     *
     * @param depth Indentation prefix for console output
     */
    private void checkPossessionConsequences(String depth) {
        ArrayList<Unit> all = playerWarband.getRosterSnapshot();
        for (Unit u : all) {
            if (u.getCorruptionLevel() >= Unit.CORRUPTION_MAJOR_THRESHOLD && !u.isPossessed()) {
                System.out.println(Color.c(depth + "!!! PERMANENT CONSEQUENCE: " + u.getName()
                        + " succumbs to possession!", Color.BOLD + Color.RED));
                u.becomePossessed();
                playerWarband.removeUnit(u);
                possessedWarband.addUnit(u);
                System.out.println(depth + "    " + u.getName()
                        + " is removed from the warband and now fights for the darkness!"
                        + " [" + possessedWarband.getWarbandName() + " roster: "
                        + possessedWarband.getRosterSize() + "]");
            }
        }
    }

    // -------------------------------------------------------------------------
    // Navigation Helper
    // -------------------------------------------------------------------------

    /**
     * Returns the next navigation direction. In interactive mode, prompts the
     * player to enter 1 (left) or 2 (right). In automated mode, reads the next
     * character from the pre-set navigationPath, defaulting to 'L'.
     *
     * @return 'L' or 'R'
     */
    private char nextDirection() {
        if (scanner != null) {
            System.out.print("  Enter 1 or 2: ");
            String input = scanner.nextLine().trim();
            return input.equals("2") ? 'R' : 'L';
        }
        if (pathIndex < navigationPath.length()) {
            char dir = navigationPath.charAt(pathIndex);
            pathIndex++;
            return (dir == 'R') ? 'R' : 'L';
        }
        return 'L'; // Default: always left
    }

    // -------------------------------------------------------------------------
    // Interactive Mode Helpers (only called when scanner != null)
    // -------------------------------------------------------------------------

    /**
     * Creates the Genestealer Cults enemy warband appropriate for the current act.
     * Act I  (~1000 pts, 6 units)  — Vanguard of the Abyss
     * Act II (~2000 pts, 11 units) — Host of the Abyss
     * Act III (~3000 pts, 15 units) — The Great Rising
     *
     * @return A freshly constructed GSC Warband scaled to the current act
     */
    private Warband createEncounterEnemies() {
        switch (actNumber) {
            case 1:  return GenestealerCultFactory.createActOneWarband();
            case 2:  return GenestealerCultFactory.createActTwoWarband();
            case 3:  return GenestealerCultFactory.createActThreeWarband();
            default: return GenestealerCultFactory.createActOneWarband();
        }
    }

    /**
     * If the player's warband contains a Psyker with warp charge and at least
     * one PsychicPower, offers the player the chance to cast it before the
     * CombatEngine skirmish begins.
     *
     * @param depth   Indentation prefix for console output
     * @param enemies The enemy warband (target of any ability cast)
     */
    private void offerPreCombatAbilities(String depth, Warband enemies) {
        if (scanner == null) return;

        for (Unit u : playerWarband.getLivingUnits()) {
            if (!(u instanceof Psyker)) continue;
            Psyker psyker = (Psyker) u;
            if (psyker.getWarpCharge() <= 0) continue;

            ArrayList<PsychicPower> powers = new ArrayList<>();
            for (Ability a : psyker.getAbilities()) {
                if (a instanceof PsychicPower) powers.add((PsychicPower) a);
            }
            if (powers.isEmpty()) continue;

            System.out.println(depth + ">>> " + psyker.getName()
                    + " can manifest a power before the fight! "
                    + "(warp charge: " + psyker.getWarpCharge() + ")");
            for (int i = 0; i < powers.size(); i++) {
                System.out.println(depth + "    [" + (i + 1) + "] "
                        + powers.get(i).getAbilityName()
                        + "  (deals " + powers.get(i).getDamageValue() + " damage)");
            }
            System.out.println(depth + "    [0] Skip");
            System.out.print(depth + "    Your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= powers.size()) {
                    ArrayList<Unit> targets = enemies.getLivingUnits();
                    if (!targets.isEmpty()) {
                        powers.get(choice - 1).cast(psyker, targets.get(0));
                        psyker.setWarpCharge(psyker.getWarpCharge() - 1);
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println(depth + "    Psychic phase skipped.");
            }
            System.out.println();
            break; // One psyker per combat phase
        }
    }

    /**
     * Prints a compact status line for each living unit in the player's warband.
     *
     * @param depth Indentation prefix for console output
     */
    private void printWarbandStatus(String depth) {
        System.out.println(depth + "  -- WARBAND STATUS --");
        for (Unit u : playerWarband.getLivingUnits()) {
            System.out.println(depth + "    " + u.getName()
                    + "  W:" + u.getStats().getWounds()
                    + "  XP:" + u.getExperiencePoints()
                    + "  Rank:" + u.getUnitRank()
                    + "  Morale:" + u.getMoraleLevel());
        }
        if (playerWarband.getLivingUnits().isEmpty()) {
            System.out.println(depth + "    (no survivors)");
        }
    }

    /**
     * Waits for the player to press Enter. Does nothing in automated mode.
     *
     * @param prompt Text to display before waiting
     */
    private void pause(String prompt) {
        if (scanner == null) return;
        System.out.print(prompt);
        scanner.nextLine();
    }

    // -------------------------------------------------------------------------
    // Campaign Tree Factory
    // -------------------------------------------------------------------------

    /**
     * Builds and returns the default four-node campaign tree used by the
     * Phase 3 test suite.
     *
     * <pre>
     *              [ Ruins of Calth ]         (root, internal)
     *             /                  \
     *     [ The Ambush ]        [ The Armoury ]    (level 1, internal)
     *     /          \           /           \
     * [Survivors]  [Pursue]  [Trap!]       [Cache]   (level 2, leaves)
     * </pre>
     *
     * @return Root node of the campaign tree
     */
    public static CampaignNode buildDefaultCampaign() {
        // --- Level 2: leaf nodes ---
        CampaignNode survivors = new CampaignNode(
                "Survivors Found",
                "Amid the carnage you discover a handful of Imperial survivors clinging to life. "
                        + "They share intel on enemy movements before collapsing.",
                "", "",
                false, 8, "Combat Blade");

        CampaignNode pursue = new CampaignNode(
                "Pursue the Fleeing Foe",
                "The enemy remnants scatter. You give chase through rubble-strewn corridors, "
                        + "cutting them down before they can regroup.",
                "", "",
                true, 12, "");

        CampaignNode trap = new CampaignNode(
                "Trigger the Trap",
                "The armoury vault is booby-trapped. Promethium charges detonate as you enter - "
                        + "your warriors hurl themselves clear at the last instant.",
                "", "",
                true, 10, "");

        CampaignNode cache = new CampaignNode(
                "Claim the Cache",
                "Beneath a collapsed section of floor you find a sealed munitions cache. "
                        + "Blessed bolters, plasma cells, and a rare volkite weapon await.",
                "", "",
                false, 6, "Volkite Charger");

        // --- Level 1: internal nodes ---
        CampaignNode ambush = new CampaignNode(
                "The Ambush",
                "Your scouts return bloodied - a Chaos warband has set killing grounds "
                        + "across every approach. You will have to fight through.",
                "Hold the line and secure survivors",
                "Pursue the fleeing remnants",
                true, 5, "");
        ambush.setLeft(survivors);
        ambush.setRight(pursue);

        CampaignNode armoury = new CampaignNode(
                "The Armoury",
                "Tactical data points to a pre-Heresy armoury vault two sub-levels below. "
                        + "The route is dark, and your auspex is showing anomalous readings.",
                "Force the vault - take what you can",
                "Bypass the vault - safety first",
                false, 3, "");
        armoury.setLeft(trap);
        armoury.setRight(cache);

        // --- Level 0: root ---
        CampaignNode root = new CampaignNode(
                "Ruins of Calth - Deployment Zone",
                "The shattered hab-blocks of Calth stretch in every direction. Somewhere ahead, "
                        + "the Chaos warband consolidates its position. Your warband must advance.",
                "Infiltrate through the ambush site",
                "Bypass to the armoury vault",
                false, 0, "");
        root.setLeft(ambush);
        root.setRight(armoury);

        return root;
    }

    // -------------------------------------------------------------------------
    // Act Campaign Factories (used by Game.java campaign chain)
    // -------------------------------------------------------------------------

    /**
     * Builds the Act I campaign tree: "The Hunt Begins".
     * Three levels deep — urban ruins, ambushes, and reliquary vaults.
     *
     * <pre>
     *              [ Castellan Sector ]          (root)
     *             /                    \
     *   [ Contact with Vanguard ] ⚔   [ Reliquary District ]
     *     /              \               /                  \
     * [Ambush Site]  [Comm Relay]⚔  [Sealed Vault]   [Heretic Ambush]⚔
     * </pre>
     *
     * @return Root node of the Act I campaign tree
     */
    public static CampaignNode buildActOneCampaign() {
        // Leaves
        CampaignNode ambushSite = new CampaignNode(
                "Ambush Site Secured",
                "You spring the kill zone before the enemy can close the trap. Scattered weapons and "
                        + "scattered bodies mark the ground. Your warriors move through the wreckage.",
                "", "", false, 8, "Auspex Scanner");

        CampaignNode commRelay = new CampaignNode(
                "The Comm Relay",
                "A Chaos comm relay crackles with encrypted traffic. Your techs crack the cipher — "
                        + "coordinates of enemy staging grounds flood across the display.",
                "", "", true, 12, "");

        CampaignNode sealedVault = new CampaignNode(
                "The Sealed Vault",
                "Beneath a devotional shrine you find a sealed reliquary. Inside, ancient wargear "
                        + "still gleams — untouched since before the Fall.",
                "", "", false, 6, "Relic Blade");

        CampaignNode hereticAmbush = new CampaignNode(
                "Heretic Ambush",
                "They were waiting. Traitors pour from every shadow. Your warband holds the line "
                        + "through sheer weight of discipline — or fury.",
                "", "", true, 10, "");

        // Level 1
        CampaignNode vanguard = new CampaignNode(
                "Contact with the Vanguard",
                "Your forward scouts return bloodied. A Chaos kill-team has set a kill zone across "
                        + "the primary approach. You cannot go around — you will have to go through.",
                "Spring the ambush early",
                "Flank through the relay station",
                true, 5, "");
        vanguard.setLeft(ambushSite);
        vanguard.setRight(commRelay);

        CampaignNode reliquary = new CampaignNode(
                "The Reliquary District",
                "Tactical data flags a reliquary district to the east. There may be ancient wargear "
                        + "inside — or Chaos scouts who got there first.",
                "Search the sealed vault",
                "Bypass and push forward into the district",
                false, 3, "");
        reliquary.setLeft(sealedVault);
        reliquary.setRight(hereticAmbush);

        // Root
        CampaignNode root = new CampaignNode(
                "Castellan Sector - Deployment",
                "Your warband deploys into the Castellan Sector — a maze of ruined hab-blocks and "
                        + "collapsed spires. The Chaos warband is consolidating somewhere ahead. "
                        + "First contact will set the tone for everything that follows.",
                "Advance through the known contact zone",
                "Probe the reliquary district to the east",
                false, 0, "");
        root.setLeft(vanguard);
        root.setRight(reliquary);

        return root;
    }

    /**
     * Builds the Act II campaign tree: "The Manufactorum Wars".
     * Three levels deep — forge-world ruins, heat and iron, Chaos machinery.
     *
     * <pre>
     *             [ Forge Gate Alpha ]           (root)
     *            /                    \
     *   [ The Smelting Halls ] ⚔    [ The Control Spire ]
     *     /            \               /                \
     * [Cooling Vents] [Servitor Den]⚔ [Override Core]  [The Long Drop]⚔
     * </pre>
     *
     * @return Root node of the Act II campaign tree
     */
    public static CampaignNode buildActTwoCampaign() {
        // Leaves
        CampaignNode coolingVents = new CampaignNode(
                "The Cooling Vents",
                "Vast pipes channel superheated air across the complex. You move through the maze "
                        + "of conduits, emerging behind enemy lines with a clear path forward.",
                "", "", false, 10, "Thermal Charge");

        CampaignNode servitorDen = new CampaignNode(
                "The Servitor Den",
                "Corrupted servitors — half-machine, half-screaming — lurch from their cradles. "
                        + "Your warband dismantles them before the noise draws worse things.",
                "", "", true, 14, "");

        CampaignNode overrideCore = new CampaignNode(
                "The Override Core",
                "The manufactorum's machine-spirit still lives — and it is angry. You feed it "
                        + "purified code, and in gratitude it seals blast doors behind you.",
                "", "", false, 8, "Cogitator Key");

        CampaignNode longDrop = new CampaignNode(
                "The Long Drop",
                "The access bridge has been destroyed. Your warband rappels forty metres into the "
                        + "forge-pit below — straight into a Chaos firing line.",
                "", "", true, 16, "");

        // Level 1
        CampaignNode smeltingHalls = new CampaignNode(
                "The Smelting Halls",
                "The interior of the forge is a hellscape of molten metal and screaming machinery. "
                        + "Chaos cultists have fortified two chokepoints ahead.",
                "Push through the cooling vents",
                "Clear the servitor holding bays",
                true, 6, "");
        smeltingHalls.setLeft(coolingVents);
        smeltingHalls.setRight(servitorDen);

        CampaignNode controlSpire = new CampaignNode(
                "The Control Spire",
                "The manufactorum control spire rises above the smog line. Whoever holds it "
                        + "controls every blast door and machine-weapon in the complex.",
                "Interface with the override core",
                "Rappel to the lower access point",
                false, 4, "");
        controlSpire.setLeft(overrideCore);
        controlSpire.setRight(longDrop);

        // Root
        CampaignNode root = new CampaignNode(
                "Forge Gate Alpha",
                "The forge-world installation sprawls across three sub-levels. Chaos forces have "
                        + "taken the smelting halls and are converting the machinery to dark purpose. "
                        + "Your warband must fight through before the corruption spreads further.",
                "Fight through the smelting halls",
                "Ascend and take the control spire",
                false, 0, "");
        root.setLeft(smeltingHalls);
        root.setRight(controlSpire);

        return root;
    }

    /**
     * Builds the Act III campaign tree: "The Traitor's Throne".
     * The climax. The right-left path descends four levels deep — into the
     * throne room itself.
     *
     * <pre>
     *              [ Siege of the Obsidian Gate ]      (root)
     *             /                               \
     *   [ The Outer Walls ] ⚔         [ The Hidden Passage ]
     *     /          \                   /                  \
     * [The Breach]  [Parley]⚔     [ The Inner Keep ] ⚔    [Dark Ambush]⚔
     *                               /              \
     *                       [Throne Room]⚔    [The Armoury]
     * </pre>
     *
     * @return Root node of the Act III campaign tree
     */
    public static CampaignNode buildActThreeCampaign() {
        // Deepest leaves (level 3 — only reachable via R-L path)
        CampaignNode throneRoom = new CampaignNode(
                "The Traitor's Throne Room",
                "You have breached the inner sanctum. The Chaos warlord waits on a throne of "
                        + "screaming skulls, his daemon bodyguard arrayed before him. This is the end "
                        + "— one way or another. Your warband charges.",
                "", "", true, 25, "Chaos Trophy");

        CampaignNode armouryVault = new CampaignNode(
                "The Armoury Vault",
                "A side chamber holds the warlord's arsenal — racks of cursed weapons and stolen "
                        + "relics. You strip what you can carry before the walls begin to close in.",
                "", "", false, 18, "Cursed Bolter");

        // Level 2 leaves (reachable via L paths and R-R)
        CampaignNode breach = new CampaignNode(
                "The Breach Point",
                "Your warband blows a gap through the outer wall with demo charges. You pour "
                        + "through before the dust settles — the fortress is breached.",
                "", "", false, 12, "Demo Charge");

        CampaignNode parley = new CampaignNode(
                "Under Flag of Parley",
                "A Herald steps forward under a skull-banner. He offers terms. Your warband's "
                        + "answer is written in bolter-fire.",
                "", "", true, 15, "");

        CampaignNode darkAmbush = new CampaignNode(
                "Ambush in the Dark",
                "The passage collapses into darkness. Daemons pour from the walls. Your warband "
                        + "fights by muzzle-flash alone — instinct and discipline against nightmare.",
                "", "", true, 20, "");

        // Level 2 internal node (only one — makes R-L go deeper)
        CampaignNode innerKeep = new CampaignNode(
                "The Inner Keep",
                "You breach the inner keep — the warlord's personal sanctum. Two paths: the throne "
                        + "room where he waits, or the armoury where his power is stored.",
                "Storm the throne room",
                "Raid the armoury vault",
                true, 10, "");
        innerKeep.setLeft(throneRoom);
        innerKeep.setRight(armouryVault);

        // Level 1
        CampaignNode outerWalls = new CampaignNode(
                "The Outer Walls",
                "The Obsidian Gate is defended by fanatics and traitor war-machines. A frontal "
                        + "assault will cost blood. Everything here costs blood.",
                "Blow a breach through the wall",
                "Accept the Herald's parley — then betray it",
                true, 8, "");
        outerWalls.setLeft(breach);
        outerWalls.setRight(parley);

        CampaignNode hiddenPassage = new CampaignNode(
                "The Hidden Passage",
                "Your scouts found a collapsed undercroft — a forgotten route beneath the walls. "
                        + "The passage is narrow and dark. Something is already inside it.",
                "Press on to the inner keep",
                "Fall back and face the ambush",
                false, 5, "");
        hiddenPassage.setLeft(innerKeep);
        hiddenPassage.setRight(darkAmbush);

        // Root
        CampaignNode root = new CampaignNode(
                "Siege of the Obsidian Gate",
                "The Chaos stronghold rises from the ash wastes — black towers wreathed in warp "
                        + "lightning, its walls carved from the bones of a dead god. Somewhere inside, "
                        + "the Traitor's Throne waits. Your warband has come too far to stop now.",
                "Storm the outer walls",
                "Use the hidden passage",
                false, 0, "");
        root.setLeft(outerWalls);
        root.setRight(hiddenPassage);

        return root;
    }

    // --- Getters ---

    public int getEncountersVisited()      { return encountersVisited; }
    public int getTotalXpAwarded()         { return totalXpAwarded; }
    public Warband getPlayerWarband()      { return playerWarband; }
    public Warband getPossessedWarband()   { return possessedWarband; }
}

/*
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 3
 *   2026-03-24  Shane Potts  Phase 3 fix - added possessedWarband so daemonic
 *                             possession converts the unit to an enemy-controlled
 *                             entry rather than simply removing it from play
 *   2026-03-24  Shane Potts  Interactive mode - added Scanner field; gated
 *                             player-choice prompts, pre-combat ability menu,
 *                             real CombatEngine skirmishes, and Enter-to-continue
 *                             pauses behind (scanner != null) so automated tests
 *                             are completely unaffected
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-08  Shane Potts  Added setPossessedWarband() for cross-act possession persistence
 *   2026-04-08  Shane Potts  Added buildActOneCampaign(), buildActTwoCampaign(),
 *                             buildActThreeCampaign() — deeper 3-4 level trees for
 *                             campaign chaining in Game.java
 *   2026-04-09  Shane Potts  Wired Detachment / CP / Stratagem system into
 *                             resolveCombat(): fresh CommandPoints per engagement,
 *                             detachment passive + stratagem hand passed to
 *                             CombatEngine; Stratagems reset between encounters
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; drives all three
 *                             acts, each with a 3-4 level binary narrative tree;
 *                             possession events persist to possessedWarband across acts
 */
