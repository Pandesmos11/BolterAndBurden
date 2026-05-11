/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Orchestrates turn-based skirmish resolution between two Warband
 *              objects. Each round, all living units from both warbands are
 *              loaded into priority queues ordered by speed. Units with the
 *              "fights_first" BattleTrait trigger are processed before all
 *              other units regardless of speed, faithfully representing the
 *              Fights First special rule. Within each tier, units are dequeued
 *              in speed order and take their turn in initiative order.
 *
 *              Attacks resolve via a three-roll D6 sequence with special-rule
 *              overlays applied at each step:
 *                1. Hit rolls (4+) — natural 6s checked for Sustained Hits
 *                   (extra hits) and Lethal Hits (auto-wound, bypass wound roll)
 *                2. Wound rolls (threshold per toughness) — natural 6s checked
 *                   for Devastating Wounds (mortal wounds, bypass saves)
 *                3. Save rolls (armour or invuln, whichever is better)
 *                   Post-save: Inner Circle (-X Damage) then Feel No Pain
 *
 *              In interactive mode (scanner != null), player units display a
 *              numbered target list each turn. Enemy units always auto-target.
 *              A CombatLog<String> stack records key events each round; popping
 *              it at round-end prints the summary in LIFO order.
 * Inputs:      Two fully constructed Warband objects; number of rounds via
 *              runCombat(int maxRounds); optional Scanner via setScanner()
 * Outputs:     Round-by-round narrative to console; per-round event summary
 *              (LIFO order) via CombatLog; victory/defeat message on conclusion
 *
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 2
 *   2026-04-08  Shane Potts  Added CombatLog<String> stack for round event recording
 *   2026-04-08  Shane Potts  Replaced deterministic damage formula with D6 hit/wound/save
 *                             sequence via resolveAttack() and Dice utility class
 *   2026-04-08  Shane Potts  Added scanner field and chooseTarget() for interactive
 *                             player target selection during skirmish turns
 *   2026-04-09  Shane Potts  Wired BattleTrait triggers into combat resolution:
 *                             fights_first (two-tier initiative), sustained_hits,
 *                             lethal_hits, devastating_wounds, invuln_save,
 *                             inner_circle, feel_no_pain — all now fire at the
 *                             correct point in the attack sequence
 *   2026-04-09  Shane Potts  Integrated Detachment passive rules and Stratagem /
 *                             Command Point system: detachment passive applied
 *                             once at combat start; player offered stratagems at
 *                             the top of each round; +1 CP gained per round per
 *                             10th edition Command phase rules
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; full attack
 *                             pipeline: hit rolls → Sustained/Lethal → wound rolls →
 *                             Devastating → armor/invuln → Inner Circle → Feel No Pain;
 *                             two-tier initiative queue (Fights First + standard)
 */

package bolterandburden;

import java.util.ArrayList;
import java.util.Scanner;

public class CombatEngine {

    private Warband attackerWarband;
    private Warband defenderWarband;
    private int currentRound;
    private CombatLog<String> roundLog;

    /**
     * When non-null, enables interactive mode: player units display a numbered
     * target list each turn and wait for the player to choose an attack target.
     * Enemy units always auto-target regardless of this field.
     * Null in automated/test mode — no output or behaviour changes.
     */
    private Scanner scanner;

    /**
     * The player's faction detachment. When non-null, its passive rule is applied
     * once at the start of combat and its Stratagems are offered each round.
     * Null in automated/test mode.
     */
    private Detachment detachment;

    /**
     * The CP pool for this engagement. When non-null, the player gains +1 CP
     * at the start of each round and may spend CP to activate Stratagems.
     * Null in automated/test mode.
     */
    private CommandPoints commandPoints;

    /**
     * Constructs a CombatEngine ready to resolve skirmishes between two warbands.
     *
     * @param attacker The warband that initiates the engagement
     * @param defender The warband being attacked
     */
    public CombatEngine(Warband attacker, Warband defender) {
        this.attackerWarband = attacker;
        this.defenderWarband = defender;
        this.currentRound    = 0;
        this.roundLog        = new CombatLog<>();
        this.scanner         = null;
        this.detachment      = null;
        this.commandPoints   = null;
    }

    /**
     * Enables interactive (game) mode. When set, player units will prompt for
     * a target selection each turn instead of auto-targeting. Pass null to
     * revert to automated mode.
     *
     * @param scanner An open Scanner connected to System.in, or null
     */
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Sets the player's Detachment for this engagement. When set, the passive
     * rule is applied to the attacker warband once at combat start, and the
     * Stratagem hand is offered to the player at the top of each round.
     *
     * @param detachment The faction Detachment, or null for automated mode
     */
    public void setDetachment(Detachment detachment) {
        this.detachment = detachment;
    }

    /**
     * Sets the Command Point pool for this engagement. When set, the player
     * gains +1 CP at the start of each round (Command phase) and may spend
     * CP on Stratagems.
     *
     * @param commandPoints A fresh CommandPoints instance for this combat
     */
    public void setCommandPoints(CommandPoints commandPoints) {
        this.commandPoints = commandPoints;
    }

    // -------------------------------------------------------------------------
    // Combat Entry Points
    // -------------------------------------------------------------------------

    /**
     * Runs up to maxRounds rounds of combat, stopping early if either warband
     * is fully defeated.
     *
     * @param maxRounds Maximum number of rounds before the engagement ends
     */
    public void runCombat(int maxRounds) {
        System.out.println(Color.c("  == ENGAGEMENT BEGINS: "
                + attackerWarband.getWarbandName() + " vs "
                + defenderWarband.getWarbandName() + " ==", Color.BOLD + Color.CYAN) + "\n");

        // Apply detachment passive rule once at combat start (interactive mode only)
        if (detachment != null && scanner != null) {
            detachment.applyPassiveRule(attackerWarband);
            System.out.println();
        }

        // Show starting CP if available
        if (commandPoints != null && scanner != null) {
            System.out.println(Color.c("  [CP] Starting Command Points: "
                    + commandPoints.getPool(), Color.BOLD + Color.CYAN));
            System.out.println();
        }

        while (currentRound < maxRounds
                && !attackerWarband.isDefeated()
                && !defenderWarband.isDefeated()) {
            runRound();
        }

        printCombatResult();
    }

    /**
     * Resolves a single round of combat. Prints the initiative order, then
     * processes all Fights First units before the regular initiative queue.
     * Within each tier units act in descending speed order.
     */
    public void runRound() {
        currentRound++;
        roundLog.clear();
        System.out.println(Color.c("  --- ROUND " + currentRound + " ---", Color.CYAN));

        // ── Command Phase: gain +1 CP per round (10th edition rule) ──────────
        if (commandPoints != null && scanner != null) {
            commandPoints.addRoundGain();
            System.out.println(Color.c("  [CP] +" + 1 + " Command Point gained. "
                    + "Pool: " + commandPoints.getPool() + " CP", Color.CYAN));
            offerStratagems();
            System.out.println();
        }

        // ── Display full initiative order (for information only) ──────────────
        PriorityQueue<Unit> displayQueue = new PriorityQueue<>();
        for (Unit u : attackerWarband.getLivingUnits()) displayQueue.enqueue(u);
        for (Unit u : defenderWarband.getLivingUnits()) displayQueue.enqueue(u);

        System.out.println("  Initiative order (" + displayQueue.getSize() + " units):");
        displayQueue.display();
        System.out.println();

        // ── Build two queues: Fights First units act before all others ────────
        PriorityQueue<Unit> ffQueue  = new PriorityQueue<>();
        PriorityQueue<Unit> stdQueue = new PriorityQueue<>();
        for (Unit u : attackerWarband.getLivingUnits()) {
            if (hasTrigger(u, "fights_first")) ffQueue.enqueue(u);
            else                               stdQueue.enqueue(u);
        }
        for (Unit u : defenderWarband.getLivingUnits()) {
            if (hasTrigger(u, "fights_first")) ffQueue.enqueue(u);
            else                               stdQueue.enqueue(u);
        }

        // ── Process Fights First tier ─────────────────────────────────────────
        if (!ffQueue.isEmpty()) {
            System.out.println(Color.c(
                    "  [FIGHTS FIRST] The following units strike before all others!",
                    Color.BOLD + Color.YELLOW));
        }
        processTurnQueue(ffQueue, true);

        // ── Process standard initiative tier ──────────────────────────────────
        processTurnQueue(stdQueue, false);

        // ── Print round summary (LIFO: most recent event first) ───────────────
        System.out.println("  Round " + currentRound + " Summary (most recent first):");
        while (!roundLog.isEmpty()) {
            System.out.println("    " + roundLog.pop());
        }
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // Ability Execution Helpers
    // -------------------------------------------------------------------------

    /**
     * Commands a Psyker unit to cast a PsychicPower from its ability list
     * against a specified target. Consumes one warp charge. Does nothing if
     * the unit has no PsychicPower abilities or insufficient warp charge.
     *
     * @param psyker The Psyker unit casting the power
     * @param target The unit receiving the power
     */
    public void castPsychicPower(Psyker psyker, Unit target) {
        if (psyker.getWarpCharge() <= 0) {
            System.out.println("  " + psyker.getName() + " has no warp charge remaining!");
            return;
        }
        for (Ability a : psyker.getAbilities()) {
            if (a instanceof PsychicPower) {
                psyker.setWarpCharge(psyker.getWarpCharge() - 1);
                ((PsychicPower) a).cast(psyker, target);
                return;
            }
        }
        System.out.println("  " + psyker.getName() + " has no PsychicPower in their ability list.");
    }

    // -------------------------------------------------------------------------
    // Private Turn Helpers
    // -------------------------------------------------------------------------

    /**
     * Drains a priority queue, resolving each unit's turn in order. Skips
     * units that were destroyed or are routing by the time their turn arrives.
     *
     * @param queue        Queue of units to process in priority order
     * @param isFightsFirst True if this is the Fights First tier (changes label)
     */
    private void processTurnQueue(PriorityQueue<Unit> queue, boolean isFightsFirst) {
        while (!queue.isEmpty()) {
            Unit active = queue.dequeue();

            // Skip if destroyed or routing mid-round from an earlier attacker
            if (active.isDestroyed() || active.isRouting()) continue;

            boolean isPlayerTurn = scanner != null
                    && attackerWarband.findUnit(active.getName()) != null;

            String ffTag = isFightsFirst ? Color.c("[FF] ", Color.YELLOW) : "";

            if (isPlayerTurn) {
                System.out.println("  " + Color.c(">> [YOUR UNIT]", Color.BOLD + Color.CYAN)
                        + " " + ffTag + Color.c(active.getName(), Color.BOLD)
                        + "'s turn (speed " + active.getStats().getSpeed() + "):");
            } else {
                System.out.println("  >> " + ffTag + active.getName()
                        + "'s turn (speed " + active.getStats().getSpeed() + "):");
            }

            active.takeTurn();

            Unit target = chooseTarget(active);
            if (target != null) {
                System.out.println("     " + active.getName()
                        + " attacks " + target.getName() + ":");
                int damage = resolveAttack(active, target);
                String entry;
                if (damage > 0) {
                    target.applyDamage(damage);
                    target.loseMorale(1);
                    System.out.println(Color.c("     --> " + damage + " damage dealt!", Color.RED));
                    entry = "[ATK] " + active.getName() + " -> " + target.getName()
                            + " (" + damage + " dmg)";
                    if (target.isDestroyed()) {
                        entry += Color.c(" -- DESTROYED", Color.BOLD + Color.RED);
                    } else if (target.isRouting()) {
                        entry += Color.c(" -- ROUTING", Color.RED);
                    }
                } else {
                    System.out.println(Color.c("     --> No damage dealt.", Color.GREEN));
                    entry = "[---] " + active.getName() + " -> " + target.getName()
                            + " (no damage)";
                }
                roundLog.push(entry);
            } else {
                System.out.println("     No valid target in range.");
                roundLog.push("[---] " + active.getName() + " -- no targets remaining");
            }
            System.out.println();
        }
    }

    /**
     * Builds a fresh PriorityQueue from all currently living units in both warbands.
     * Used for the display pass in runRound().
     *
     * @return Populated PriorityQueue ready for display
     */
    private PriorityQueue<Unit> buildQueue() {
        PriorityQueue<Unit> queue = new PriorityQueue<>();
        for (Unit u : attackerWarband.getLivingUnits()) queue.enqueue(u);
        for (Unit u : defenderWarband.getLivingUnits()) queue.enqueue(u);
        return queue;
    }

    /**
     * Returns the first living unit in the warband opposing the active unit.
     * Determines which warband the active unit belongs to by name lookup.
     *
     * @param active The unit taking its turn
     * @return First living opponent, or null if the opposing warband is empty
     */
    private Unit findTarget(Unit active) {
        Warband opponent = (attackerWarband.findUnit(active.getName()) != null)
                ? defenderWarband : attackerWarband;
        ArrayList<Unit> targets = opponent.getLivingUnits();
        return targets.isEmpty() ? null : targets.get(0);
    }

    /**
     * Selects an attack target for the active unit. In automated mode (or for
     * enemy units), auto-picks the first living opponent via findTarget(). In
     * interactive mode when the active unit belongs to the player's warband,
     * prints a numbered list of living enemies and waits for player input.
     * Invalid input defaults to the first target.
     *
     * @param active The unit taking its turn
     * @return The chosen target Unit, or null if no opponents remain
     */
    private Unit chooseTarget(Unit active) {
        boolean isPlayerUnit = attackerWarband.findUnit(active.getName()) != null;

        // Auto-select: enemy turns or non-interactive mode
        if (scanner == null || !isPlayerUnit) {
            return findTarget(active);
        }

        // Interactive: show numbered target list for player units
        ArrayList<Unit> targets = defenderWarband.getLivingUnits();
        if (targets.isEmpty()) return null;

        System.out.println("     " + Color.c("Choose your target:", Color.BOLD));
        for (int i = 0; i < targets.size(); i++) {
            Unit t = targets.get(i);
            System.out.println("       " + Color.c("[" + (i + 1) + "]", Color.CYAN)
                    + " " + t.getName()
                    + "  W:" + t.getStats().getWounds()
                    + "  T:" + t.getStats().getToughness()
                    + "  Sv:" + t.getStats().getSave() + "+");
        }
        System.out.print("     Enter number (default 1): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= 1 && choice <= targets.size()) {
                return targets.get(choice - 1);
            }
        } catch (NumberFormatException e) {
            // Fall through to default
        }
        return targets.get(0);
    }

    /**
     * Resolves a full attack sequence from attacker against target.
     *
     * <p>Sequence with special-rule overlays:
     * <ol>
     *   <li>Hit rolls (4+) — natural 6s trigger Sustained Hits (extra hits)
     *       and/or Lethal Hits (those 6s auto-wound, skip wound roll)</li>
     *   <li>Wound rolls (threshold per toughness) — natural 6s trigger
     *       Devastating Wounds (mortal wounds, bypass saves)</li>
     *   <li>Armour/invulnerable saves — best of armour save vs invuln save
     *       (lower threshold wins; invuln_save BattleTrait carries threshold)</li>
     *   <li>Inner Circle damage reduction — subtracts modifier from total damage</li>
     *   <li>Feel No Pain — roll D6 per remaining wound; ignore on threshold+</li>
     * </ol>
     *
     * @param attacker The unit making the attack
     * @param target   The unit being attacked
     * @return Total unsaved wounds after all defensive rolls (damage to apply)
     */
    private int resolveAttack(Unit attacker, Unit target) {
        int attacks = attacker.getWargear().getEffectiveAttacks(attacker.getStats().getAttacks());

        // ── HIT ROLLS ─────────────────────────────────────────────────────────
        int[] hitResult = Dice.rollWithSixes(attacks, 4);
        int hits     = hitResult[0];
        int hitSixes = hitResult[1];

        // Sustained Hits: each natural 6 generates extra hits
        int sustainedBonus = getSustainedHits(attacker);
        if (sustainedBonus > 0 && hitSixes > 0) {
            int extra = hitSixes * sustainedBonus;
            hits += extra;
            System.out.println(Color.c("       [Sustained Hits " + sustainedBonus + "] "
                    + hitSixes + " natural 6(s) → +" + extra + " extra hit(s)",
                    Color.YELLOW));
        }

        // Lethal Hits: natural 6s to hit automatically wound (bypass wound roll)
        int autoWounds = 0;
        if (hasLethalHits(attacker) && hitSixes > 0) {
            autoWounds = hitSixes;
            hits = Math.max(0, hits - hitSixes); // those dice already caused wounds
            System.out.println(Color.c("       [Lethal Hits] " + autoWounds
                    + " natural 6(s) auto-wound — no wound roll needed!", Color.YELLOW));
        }

        System.out.println("       Hit rolls  (" + attacks + " dice, 4+): " + hits + " hit(s)");
        if (hits == 0 && autoWounds == 0) {
            System.out.println(Color.c("       All attacks miss!", Color.GREEN));
            return 0;
        }

        // ── WOUND ROLLS ───────────────────────────────────────────────────────
        int woundOn = woundThreshold(target.getStats().getToughness());
        int[] woundResult = (hits > 0) ? Dice.rollWithSixes(hits, woundOn) : new int[]{0, 0};
        int wounds      = woundResult[0];
        int woundSixes  = woundResult[1];

        // Devastating Wounds: natural 6s to wound become mortal wounds (bypass saves)
        int mortalWounds = autoWounds; // Lethal Hit auto-wounds are also treated as mortal
        if (hasDevastatingWounds(attacker) && woundSixes > 0) {
            mortalWounds += woundSixes;
            wounds = Math.max(0, wounds - woundSixes);
            System.out.println(Color.c("       [Devastating Wounds] " + woundSixes
                    + " natural 6(s) → mortal wounds bypass saves!", Color.YELLOW));
        }

        System.out.println("       Wound rolls (" + hits + " dice, " + woundOn + "+): "
                + wounds + " wound(s)"
                + (mortalWounds > 0 ? " + " + mortalWounds + " mortal wound(s)" : ""));
        if (wounds == 0 && mortalWounds == 0) {
            System.out.println(Color.c("       All hits bounce off!", Color.GREEN));
            return 0;
        }

        // ── ARMOUR / INVULNERABLE SAVES ───────────────────────────────────────
        int armourSave = target.getStats().getSave();
        int invuln     = getInvulnSave(target);
        // Lower threshold = better save; use invuln if it outperforms armour
        int effectiveSave = (invuln > 0 && invuln < armourSave) ? invuln : armourSave;
        if (invuln > 0 && invuln < armourSave) {
            System.out.println(Color.c("       [Invulnerable " + invuln + "++] Using invuln "
                    + "instead of " + armourSave + "+ armour save", Color.YELLOW));
        }
        int saves = (wounds > 0) ? Dice.rollHits(wounds, effectiveSave) : 0;
        System.out.println(Color.c("       Save rolls  (" + wounds + " dice, "
                + effectiveSave + "+): " + saves + " save(s)", Color.GREEN));

        int unsaved = (wounds - saves) + mortalWounds;
        if (unsaved <= 0) {
            System.out.println(Color.c("       All wounds saved!", Color.GREEN));
            return 0;
        }

        // ── INNER CIRCLE / DAMAGE REDUCTION ───────────────────────────────────
        int reduction = getDamageReduction(target);
        if (reduction > 0) {
            int reduced = Math.max(0, unsaved - reduction);
            System.out.println(Color.c("       [Inner Circle] -" + reduction
                    + " damage: " + unsaved + " → " + reduced, Color.YELLOW));
            unsaved = reduced;
        }
        if (unsaved <= 0) return 0;

        // ── FEEL NO PAIN ──────────────────────────────────────────────────────
        int fnpThreshold = getFeelNoPain(target);
        if (fnpThreshold > 0) {
            int fnpSaved = Dice.rollHits(unsaved, fnpThreshold);
            System.out.println(Color.c("       [Feel No Pain " + fnpThreshold + "+] "
                    + fnpSaved + "/" + unsaved + " wound(s) shrugged off",
                    Color.YELLOW));
            unsaved -= fnpSaved;
        }

        return unsaved;
    }

    /**
     * Returns the wound roll threshold (minimum D6) based on target toughness.
     * Higher toughness requires a higher roll to cause a wound.
     *
     * @param toughness Target's toughness stat
     * @return Minimum D6 roll needed to wound (3, 4, 5, or 6)
     */
    private int woundThreshold(int toughness) {
        if (toughness <= 3) return 3;
        if (toughness <= 5) return 4;
        if (toughness <= 7) return 5;
        return 6;
    }

    /**
     * Prints the final combat result after all rounds resolve.
     */
    private void printCombatResult() {
        System.out.println(Color.c("  == ENGAGEMENT ENDS after round " + currentRound + " ==", Color.CYAN));
        if (attackerWarband.isDefeated() && defenderWarband.isDefeated()) {
            System.out.println(Color.c("  RESULT: Mutual annihilation - both warbands destroyed.", Color.RED));
        } else if (defenderWarband.isDefeated()) {
            System.out.println(Color.c("  RESULT: VICTORY for " + attackerWarband.getWarbandName() + "!", Color.BOLD + Color.YELLOW));
        } else if (attackerWarband.isDefeated()) {
            System.out.println(Color.c("  RESULT: VICTORY for " + defenderWarband.getWarbandName() + "!", Color.BOLD + Color.YELLOW));
        } else {
            System.out.println("  RESULT: Round cap reached - engagement inconclusive.");
        }
    }

    // -------------------------------------------------------------------------
    // Special Rule Trigger Helpers
    // -------------------------------------------------------------------------

    /**
     * Returns true if the unit has at least one BattleTrait whose trigger
     * matches the given trigger identifier.
     *
     * @param u       Unit to inspect
     * @param trigger Trigger string to look for (e.g. "fights_first")
     * @return true if a matching BattleTrait trigger is found
     */
    private boolean hasTrigger(Unit u, String trigger) {
        for (Ability a : u.getAbilities()) {
            if (a instanceof BattleTrait
                    && trigger.equals(((BattleTrait) a).getTrigger()))
                return true;
        }
        return false;
    }

    /**
     * Returns the total Sustained Hits bonus from the attacker's BattleTraits.
     * Stacks if multiple sustained_hits traits are present.
     *
     * @param u Attacking unit
     * @return Total extra hits generated per natural 6 (0 if none)
     */
    private int getSustainedHits(Unit u) {
        int total = 0;
        for (Ability a : u.getAbilities()) {
            if (a instanceof BattleTrait
                    && "sustained_hits".equals(((BattleTrait) a).getTrigger()))
                total += ((BattleTrait) a).getModifier();
        }
        return total;
    }

    /**
     * Returns true if the attacker has Lethal Hits (natural 6s to hit
     * automatically wound, skipping the wound roll).
     *
     * @param u Attacking unit
     * @return true if a lethal_hits BattleTrait is present
     */
    private boolean hasLethalHits(Unit u) {
        return hasTrigger(u, "lethal_hits");
    }

    /**
     * Returns true if the attacker has Devastating Wounds (natural 6s to
     * wound cause mortal wounds that bypass saves).
     *
     * @param u Attacking unit
     * @return true if a devastating_wounds BattleTrait is present
     */
    private boolean hasDevastatingWounds(Unit u) {
        return hasTrigger(u, "devastating_wounds");
    }

    /**
     * Returns the best (lowest-threshold) invulnerable save available on the
     * target, or 0 if none. Lower number = better in a "roll X+" system.
     *
     * @param u Target unit
     * @return Best invuln save threshold (e.g. 4 = 4++), or 0
     */
    private int getInvulnSave(Unit u) {
        int best = 0;
        for (Ability a : u.getAbilities()) {
            if (a instanceof BattleTrait
                    && "invuln_save".equals(((BattleTrait) a).getTrigger())) {
                int val = ((BattleTrait) a).getModifier();
                if (best == 0 || val < best) best = val;
            }
        }
        return best;
    }

    /**
     * Returns the total flat damage reduction from inner_circle BattleTraits
     * on the target (minimum damage after reduction is 0).
     *
     * @param u Target unit
     * @return Total damage to subtract from unsaved wounds (0 if none)
     */
    private int getDamageReduction(Unit u) {
        int total = 0;
        for (Ability a : u.getAbilities()) {
            if (a instanceof BattleTrait
                    && "inner_circle".equals(((BattleTrait) a).getTrigger()))
                total += ((BattleTrait) a).getModifier();
        }
        return total;
    }

    /**
     * Returns the best (lowest-threshold) Feel No Pain save from the target's
     * BattleTraits, or 0 if none. Each unsaved wound is rolled against this
     * threshold; successes negate the wound.
     *
     * @param u Target unit
     * @return FNP threshold (e.g. 4 = 4+), or 0 if no FNP trait
     */
    private int getFeelNoPain(Unit u) {
        int best = 0;
        for (Ability a : u.getAbilities()) {
            if (a instanceof BattleTrait
                    && "feel_no_pain".equals(((BattleTrait) a).getTrigger())) {
                int val = ((BattleTrait) a).getModifier();
                if (best == 0 || val < best) best = val;
            }
        }
        return best;
    }

    // -------------------------------------------------------------------------
    // Stratagem Menu (interactive mode only)
    // -------------------------------------------------------------------------

    /**
     * Offers the player the chance to activate one Stratagem per round.
     * Displays all available (not-yet-used, affordable) Stratagems, prompts
     * for a Stratagem and target choice, then applies the effect via
     * Stratagem.applyEffect(). The player may also skip (enter 0).
     *
     * <p>Only called when both {@code scanner} and {@code detachment} are non-null
     * and {@code commandPoints} is set. CP is spent before the effect fires.
     */
    private void offerStratagems() {
        if (scanner == null || detachment == null || commandPoints == null) return;

        ArrayList<Stratagem> available = new ArrayList<>();
        for (Stratagem s : detachment.getStratagems()) {
            if (!s.isUsedThisCombat() && commandPoints.canAfford(s.getCost())) {
                available.add(s);
            }
        }

        if (available.isEmpty()) {
            System.out.println(Color.c("  [STRATAGEM] No affordable stratagems available "
                    + "(CP: " + commandPoints.getPool() + ").", Color.CYAN));
            return;
        }

        System.out.println(Color.c("  [STRATAGEM PHASE] "
                + detachment.getDetachmentName() + " — available stratagems:",
                Color.BOLD + Color.CYAN));
        for (int i = 0; i < available.size(); i++) {
            Stratagem s = available.get(i);
            System.out.println("    " + Color.c("[" + (i + 1) + "]", Color.CYAN)
                    + " " + Color.c(s.getName(), Color.BOLD)
                    + " (" + s.getCost() + " CP) — " + s.getDescription());
        }
        System.out.println("    " + Color.c("[0]", Color.CYAN) + " Skip stratagem phase");
        System.out.print("  Choose stratagem (0 to skip): ");

        int strChoice;
        try {
            strChoice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            strChoice = 0;
        }

        if (strChoice < 1 || strChoice > available.size()) {
            System.out.println(Color.c("  [STRATAGEM] Phase skipped.", Color.CYAN));
            return;
        }

        Stratagem chosen = available.get(strChoice - 1);

        // Build valid target list for this stratagem
        ArrayList<Unit> validTargets = new ArrayList<>();
        for (Unit u : attackerWarband.getLivingUnits()) {
            if (chosen.isValidTarget(u)) validTargets.add(u);
        }

        if (validTargets.isEmpty()) {
            System.out.println(Color.c("  [STRATAGEM] No valid targets for "
                    + chosen.getName() + ".", Color.CYAN));
            return;
        }

        System.out.println("  Choose target for " + Color.c(chosen.getName(), Color.BOLD) + ":");
        for (int i = 0; i < validTargets.size(); i++) {
            Unit t = validTargets.get(i);
            System.out.println("    " + Color.c("[" + (i + 1) + "]", Color.CYAN)
                    + " " + t.getName()
                    + "  W:" + t.getStats().getWounds()
                    + "  T:" + t.getStats().getToughness());
        }
        System.out.print("  Choose target (default 1): ");

        int targetChoice;
        try {
            targetChoice = Integer.parseInt(scanner.nextLine().trim());
            if (targetChoice < 1 || targetChoice > validTargets.size()) targetChoice = 1;
        } catch (NumberFormatException e) {
            targetChoice = 1;
        }

        Unit target = validTargets.get(targetChoice - 1);

        // Spend CP and apply the effect
        commandPoints.spend(chosen.getCost());
        chosen.applyEffect(target);
        System.out.println(Color.c("  [CP] " + chosen.getCost() + " CP spent. "
                + "Remaining: " + commandPoints.getPool() + " CP", Color.CYAN));
    }

    // --- Getters ---

    public int getCurrentRound()        { return currentRound; }
    public Warband getAttackerWarband() { return attackerWarband; }
    public Warband getDefenderWarband() { return defenderWarband; }
}

/*
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 2; round-based skirmish
 *                             engine using two PriorityQueue<Unit> instances per
 *                             round; deterministic damage formula; CombatLog stack
 *                             records key events
 *   2026-04-08  Shane Potts  Added CombatLog<String> stack for round event recording
 *   2026-04-08  Shane Potts  Replaced deterministic damage formula with D6 hit/wound/save
 *                             sequence via resolveAttack() and Dice utility class
 *   2026-04-08  Shane Potts  Added scanner field and chooseTarget() for interactive
 *                             player target selection during skirmish turns
 *   2026-04-09  Shane Potts  Wired BattleTrait triggers into combat resolution:
 *                             fights_first (two-tier initiative), sustained_hits,
 *                             lethal_hits, devastating_wounds, invuln_save,
 *                             inner_circle, feel_no_pain — all now fire at the
 *                             correct point in the attack sequence
 *   2026-04-09  Shane Potts  Integrated Detachment passive rules and Stratagem /
 *                             Command Point system: detachment passive applied
 *                             once at combat start; player offered stratagems at
 *                             the top of each round; +1 CP gained per round per
 *                             10th edition Command phase rules
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; full attack
 *                             pipeline: hit rolls → Sustained/Lethal → wound rolls →
 *                             Devastating → armor/invuln → Inner Circle → Feel No Pain;
 *                             two-tier initiative queue (Fights First + standard)
 */
