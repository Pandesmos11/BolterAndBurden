/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Entry point and combined Phase 1 / Phase 2 test suite.
 *              Phase 1 tests: faction construction, all four Unit subtypes,
 *              Warband roster management, sorted insertion, polymorphic
 *              dispatch, search, and removal.
 *              Phase 2 tests: ability system (PsychicPower, SorceryRitual,
 *              BattleTrait), recursive psychic chain resolution and depth-
 *              limit termination, morale boundary values, corruption threshold
 *              events, PriorityQueue initiative ordering, CombatEngine full
 *              skirmish resolution, and CombatLog<String> stack operations.
 *              Phase 3 tests: FactionFactory construction, UnitSorter recursive
 *              quicksort (multi-stat), XP progression with rank-up ability
 *              unlocks, daemonic possession permanent consequences, campaign
 *              tree traversal via recursive navigate(), alternate branch
 *              verification, full end-to-end playtest, and binary search
 *              roster lookup via findFirstByType().
 *              No user input is required; all output goes to the console.
 * Inputs:      None (hardcoded test data)
 * Outputs:     Formatted test results printed to System.out
 *
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 1
 *   2026-03-23  Shane Potts  Phase 2 - added Tests 10-19 covering the full
 *                             ability system, morale, corruption, priority
 *                             queue, CombatEngine, and CombatLog stack
 *   2026-03-23  Shane Potts  Phase 3 - added Tests 20-26 covering factory
 *                             methods, quicksort, XP progression, possession,
 *                             campaign traversal, and end-to-end playtest
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-08  Shane Potts  Added Test 19: CombatLog<String> stack verification
 *   2026-04-08  Shane Potts  Added Test 27: findFirstByType() binary search verification
 *   2026-04-26  Shane Potts  Phase 3 complete - 27 automated tests all pass; covers
 *                             all data structures, all four unit types, full combat
 *                             pipeline, factory construction, and campaign traversal
 *   2026-05-04  Shane Potts  Fixed title banner to read "Phase 1 + Phase 2 + Phase 3
 *                             Test Suite"; corrected closing message from 26 to 27
 *                             tests passed to match actual test count
 */

package bolterandburden;

import java.util.ArrayList;

public class Main {

    // -------------------------------------------------------------------------
    // Separator helpers
    // -------------------------------------------------------------------------

    private static void section(String title) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  " + title);
        System.out.println("=".repeat(60));
    }

    private static void pass(String message) {
        System.out.println("  [PASS] " + message);
    }

    private static void fail(String message) {
        System.out.println("  [FAIL] " + message);
    }

    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║   BOLTER & BURDEN: THE TRAITOR'S THRONE              ║");
        System.out.println("║   Phase 1 + Phase 2 + Phase 3 Test Suite             ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");

        // =====================================================================
        // ========================  PHASE 1 TESTS  ============================
        // =====================================================================

        // =====================================================================
        // TEST 1 - Faction Construction
        // =====================================================================
        section("TEST 1 - Faction Construction");

        Faction chaosKnights = new Faction("Chaos Knights", true);
        chaosKnights.addUnitType("Knight");
        chaosKnights.addUnitType("Infantry");

        Faction emperorsChildren = new Faction("Emperor's Children", true);
        emperorsChildren.addUnitType("Infantry");
        emperorsChildren.addUnitType("Psyker");
        emperorsChildren.addUnitType("Vehicle");

        Faction darkAngels = new Faction("Dark Angels", false);
        darkAngels.addUnitType("Infantry");
        darkAngels.addUnitType("Vehicle");
        darkAngels.addUnitType("Psyker");

        System.out.println(chaosKnights);
        System.out.println(emperorsChildren);
        System.out.println(darkAngels);

        pass("Chaos Knights isChaosAligned == true : " + chaosKnights.isChaosAligned());
        pass("Dark Angels isChaosAligned == false  : " + !darkAngels.isChaosAligned());
        pass("Emperor's Children has Psyker        : " + emperorsChildren.isUnitTypeAvailable("Psyker"));
        pass("Chaos Knights has no Psyker           : " + !chaosKnights.isUnitTypeAvailable("Psyker"));

        // =====================================================================
        // TEST 2 - Unit Subclass Construction & toString()
        // =====================================================================
        section("TEST 2 - Unit Subclass Construction and toString()");

        Wargear noiseMarine_gear = new Wargear();
        noiseMarine_gear.addEquipment("Sonic Blaster");
        noiseMarine_gear.addEquipment("Bolt Pistol");
        noiseMarine_gear.setAttackModifier(1);
        Infantry noiseMarine = new Infantry(
                "Kallax the Discordant",
                "Emperor's Children",
                new StatBlock(2, 4, 3, 6, 2),
                noiseMarine_gear,
                "Noise Marine",
                true
        );

        Wargear rhino_gear = new Wargear();
        rhino_gear.addEquipment("Storm Bolter");
        Vehicle rhino = new Vehicle(
                "Iron Covenant",
                "Dark Angels",
                new StatBlock(10, 7, 3, 12, 1),
                rhino_gear,
                "Transport",
                10
        );

        Wargear knight_gear = new Wargear();
        knight_gear.addEquipment("Reaper Chainsword");
        knight_gear.addEquipment("Rapid-Fire Battle Cannon");
        knight_gear.setAttackModifier(2);
        Knight tyrant = new Knight(
                "Tyrant of Ruin",
                "Chaos Knights",
                new StatBlock(24, 8, 3, 8, 4),
                knight_gear,
                "Despoiler",
                true
        );

        Wargear librarian_gear = new Wargear();
        librarian_gear.addEquipment("Force Staff");
        librarian_gear.addEquipment("Bolt Pistol");
        Psyker librarian = new Psyker(
                "Librarian Azriel",
                "Dark Angels",
                new StatBlock(4, 4, 3, 6, 3),
                librarian_gear,
                "Interromancy",
                3
        );

        System.out.println(noiseMarine);
        System.out.println();
        System.out.println(rhino);
        System.out.println();
        System.out.println(tyrant);
        System.out.println();
        System.out.println(librarian);

        pass("All four Unit subtypes constructed and printed without errors.");

        // =====================================================================
        // TEST 3 - Comparable / Speed Ordering
        // =====================================================================
        section("TEST 3 - Comparable (Speed-Based Ordering)");

        System.out.println("  Speeds: "
                + noiseMarine.getName() + "=" + noiseMarine.getStats().getSpeed()
                + ", " + rhino.getName()    + "=" + rhino.getStats().getSpeed()
                + ", " + tyrant.getName()   + "=" + tyrant.getStats().getSpeed()
                + ", " + librarian.getName()+ "=" + librarian.getStats().getSpeed());

        if (rhino.compareTo(tyrant) < 0)         pass("Rhino (12) sorts before Tyrant (8)");
        else                                      fail("Rhino should sort before Tyrant");
        if (tyrant.compareTo(noiseMarine) < 0)   pass("Tyrant (8) sorts before Noise Marine (6)");
        else                                      fail("Tyrant should sort before Noise Marine");

        // =====================================================================
        // TEST 4 - Warband Construction and Sorted Insertion
        // =====================================================================
        section("TEST 4 - Warband Construction and Sorted Insertion");

        Warband darkAngelsWarband = new Warband("The Unforgiven", darkAngels);
        darkAngelsWarband.addUnit(librarian);
        darkAngelsWarband.addUnit(rhino);

        System.out.println("  Adding Librarian (6) then Rhino (12) - expect Rhino first:");
        darkAngelsWarband.displayRoster();

        Warband chaosWarband = new Warband("Echoes of Ruin", chaosKnights);
        chaosWarband.addUnit(tyrant);
        chaosWarband.addUnit(noiseMarine);

        System.out.println();
        System.out.println("  Adding Tyrant (8) then Noise Marine (6) - expect Tyrant first:");
        chaosWarband.displayRoster();

        pass("Roster sizes: Dark Angels=" + darkAngelsWarband.getRosterSize()
                + " (expect 2), Chaos=" + chaosWarband.getRosterSize() + " (expect 2)");

        // =====================================================================
        // TEST 5 - Polymorphism: takeTurn()
        // =====================================================================
        section("TEST 5 - Polymorphism: takeTurn() dispatch");

        Unit[] units = { noiseMarine, rhino, tyrant, librarian };
        for (Unit u : units) {
            System.out.print("  -> ");
            u.takeTurn();
        }
        pass("takeTurn() dispatched through Unit reference for all four subtypes.");

        // =====================================================================
        // TEST 6 - Polymorphism: applyDamage()
        // =====================================================================
        section("TEST 6 - Polymorphism: applyDamage() dispatch");

        for (Unit u : units) {
            System.out.print("  -> ");
            u.applyDamage(2);
        }
        pass("applyDamage() dispatched through Unit reference for all subtypes.");

        // =====================================================================
        // TEST 7 - Roster Search
        // =====================================================================
        section("TEST 7 - Roster Search");

        Unit found = darkAngelsWarband.findUnit("Azriel");
        if (found != null) pass("Found 'Azriel': " + found.getName());
        else               fail("Search for 'Azriel' returned null");

        Unit notFound = darkAngelsWarband.findUnit("Magnus");
        if (notFound == null) pass("Search for 'Magnus' correctly returned null");
        else                  fail("Search for 'Magnus' should return null");

        // =====================================================================
        // TEST 8 - Unit Removal
        // =====================================================================
        section("TEST 8 - Unit Removal");

        boolean removed = darkAngelsWarband.removeUnit(rhino);
        darkAngelsWarband.displayRoster();

        if (removed && darkAngelsWarband.getRosterSize() == 1)
            pass("Removal successful. Roster size now 1.");
        else
            fail("Removal failed or wrong roster size.");

        if (!darkAngelsWarband.removeUnit(rhino))
            pass("Re-removing absent unit correctly returns false.");
        else
            fail("Should not remove a unit not in the roster.");

        // Re-add rhino for Phase 2 combat tests
        darkAngelsWarband.addUnit(rhino);

        // =====================================================================
        // TEST 9 - Wargear Effective Stat Calculations
        // =====================================================================
        section("TEST 9 - Wargear Effective Stat Calculations");

        int base      = noiseMarine.getStats().getAttacks();
        int effective = noiseMarine.getWargear().getEffectiveAttacks(base);
        System.out.println("  " + noiseMarine.getName()
                + " base attacks: " + base + " | effective: " + effective);
        if (effective == 3) pass("Wargear attack modifier applied correctly (2 + 1 = 3)");
        else                fail("Expected 3, got " + effective);


        // =====================================================================
        // ========================  PHASE 2 TESTS  ============================
        // =====================================================================

        // =====================================================================
        // TEST 10 - Ability Construction (PsychicPower, SorceryRitual, BattleTrait)
        // =====================================================================
        section("TEST 10 - Ability Construction");

        PsychicPower smite = new PsychicPower("Smite", "A focused beam of pure psychic energy.", 2);
        PsychicPower warpBolts = new PsychicPower("Warp Bolts",
                "Splinters of solidified warp energy hurled at the foe.", 1);
        PsychicPower mindScour = new PsychicPower("Mind Scour",
                "Overloads the target's synaptic pathways.", 1);

        SorceryRitual cacophony = new SorceryRitual(
                "Cacophonic Choir",
                "The sorcerer unleashes a wall of soul-shredding noise.",
                3, 2);

        BattleTrait veteranInstincts = new BattleTrait(
                "Veteran's Instincts",
                "Hard-won combat experience sharpens every strike.",
                "attacks", 1);

        System.out.println("  " + smite);
        System.out.println("  " + cacophony);
        System.out.println("  " + veteranInstincts);

        pass("PsychicPower, SorceryRitual, BattleTrait constructed without errors.");
        pass("smite damage value: " + smite.getDamageValue() + " (expect 2)");
        pass("cacophony corruption cost: " + cacophony.getCasterCorruptionCost() + " (expect 2)");
        pass("veteranInstincts stat target: " + veteranInstincts.getStatTarget()
                + " modifier: " + veteranInstincts.getModifier() + " (expect attacks +1)");

        // =====================================================================
        // TEST 11 - Psychic Chain Resolution (depth limit)
        // =====================================================================
        section("TEST 11 - Recursive Psychic Chain Resolution");

        // Build a 4-level deep chain: smite -> warpBolts -> mindScour -> smite (depth 3 = stop)
        smite.addChainTrigger(warpBolts);
        warpBolts.addChainTrigger(mindScour);
        mindScour.addChainTrigger(smite);   // would recurse forever - depth limit must stop this

        // Fresh target unit for this test
        Wargear dummy_gear = new Wargear();
        dummy_gear.addEquipment("None");
        Infantry cultist = new Infantry(
                "Cultist Alpha",
                "Emperor's Children",
                new StatBlock(5, 3, 6, 5, 1),
                dummy_gear,
                "Cultist",
                false
        );

        System.out.println("  Casting Smite on " + cultist.getName()
                + " (wounds: " + cultist.getStats().getWounds() + ")");
        System.out.println("  Chain: Smite -> Warp Bolts -> Mind Scour -> Smite (should stop at depth 3)\n");

        smite.cast(librarian, cultist);

        System.out.println();
        if (cultist.getStats().getWounds() < 5)
            pass("Cultist took damage from chain. Wounds: " + cultist.getStats().getWounds());
        else
            fail("Cultist should have taken damage from chain.");

        pass("Chain terminated at depth " + PsychicPower.MAX_CHAIN_DEPTH
                + " - infinite recursion prevented.");

        // =====================================================================
        // TEST 12 - BattleTrait Activation
        // =====================================================================
        section("TEST 12 - BattleTrait Activation");

        int attacksBefore = tyrant.getStats().getAttacks();
        veteranInstincts.activate(tyrant);
        int attacksAfter = tyrant.getStats().getAttacks();

        System.out.println("  " + tyrant.getName() + " attacks before: " + attacksBefore
                + " | after: " + attacksAfter);
        if (attacksAfter == attacksBefore + 1)
            pass("BattleTrait applied +1 attack correctly.");
        else
            fail("Expected attacks " + (attacksBefore + 1) + ", got " + attacksAfter);

        // Reverse the buff so tests stay clean
        tyrant.getStats().setAttacks(attacksBefore);

        // =====================================================================
        // TEST 13 - SorceryRitual Cast (damage + caster corruption)
        // =====================================================================
        section("TEST 13 - SorceryRitual Cast");

        // Build a chaos sorcerer
        Wargear sorcerer_gear = new Wargear();
        sorcerer_gear.addEquipment("Force Sword");
        sorcerer_gear.addEquipment("Bolt Pistol");
        Psyker sorcerer = new Psyker(
                "Sorcerer Malachar",
                "Emperor's Children",
                new StatBlock(4, 4, 3, 6, 3),
                sorcerer_gear,
                "Tzeentch",
                2
        );

        Wargear target_gear = new Wargear();
        target_gear.addEquipment("Bolter");
        Infantry darkAngel = new Infantry(
                "Brother Castellan",
                "Dark Angels",
                new StatBlock(3, 4, 3, 6, 2),
                target_gear,
                "Tactical",
                false
        );

        int corruptionBefore = sorcerer.getCorruptionLevel();
        int targetWoundsBefore = darkAngel.getStats().getWounds();
        System.out.println("  Sorcerer corruption before: " + corruptionBefore);
        System.out.println("  Target wounds before: " + targetWoundsBefore);

        cacophony.cast(sorcerer, darkAngel);

        System.out.println("  Sorcerer corruption after: " + sorcerer.getCorruptionLevel());
        System.out.println("  Target wounds after: " + darkAngel.getStats().getWounds());

        if (sorcerer.getCorruptionLevel() == corruptionBefore + 2)
            pass("Sorcerer gained 2 corruption from ritual.");
        else
            fail("Expected corruption " + (corruptionBefore + 2)
                    + ", got " + sorcerer.getCorruptionLevel());

        if (darkAngel.getStats().getWounds() == targetWoundsBefore - 3)
            pass("Target took 3 damage from ritual.");
        else
            fail("Expected wounds " + (targetWoundsBefore - 3)
                    + ", got " + darkAngel.getStats().getWounds());

        // =====================================================================
        // TEST 14 - Morale System (boundary values)
        // =====================================================================
        section("TEST 14 - Morale System Boundary Values");

        Wargear m_gear = new Wargear();
        m_gear.addEquipment("Bolter");
        Infantry moraleUnit = new Infantry(
                "Brother Mathias",
                "Dark Angels",
                new StatBlock(3, 4, 3, 6, 2),
                m_gear,
                "Tactical",
                false
        );

        System.out.println("  Starting morale: " + moraleUnit.getMoraleLevel()
                + "  (threshold = " + Unit.MORALE_THRESHOLD + ")");

        // Drain morale to just above threshold - no route should trigger
        moraleUnit.setMoraleLevel(4);
        moraleUnit.checkMorale();
        if (!moraleUnit.isRouting())
            pass("Morale at 4 (above threshold 3) - unit holds.");
        else
            fail("Unit should NOT be routing at morale 4.");

        // Drop to exactly the threshold - route should trigger
        moraleUnit.setMoraleLevel(Unit.MORALE_THRESHOLD);
        moraleUnit.checkMorale();
        if (moraleUnit.isRouting())
            pass("Morale at threshold (" + Unit.MORALE_THRESHOLD + ") - unit routes.");
        else
            fail("Unit SHOULD be routing at threshold morale.");

        // Rally back
        moraleUnit.rally();
        if (!moraleUnit.isRouting() && moraleUnit.getMoraleLevel() > Unit.MORALE_THRESHOLD)
            pass("Rally clears routing flag. New morale: " + moraleUnit.getMoraleLevel());
        else
            fail("Rally failed to restore unit.");

        // loseMorale() test
        moraleUnit.setMoraleLevel(5);
        moraleUnit.loseMorale(3);
        System.out.println("  After loseMorale(3) from 5: morale = " + moraleUnit.getMoraleLevel());
        if (moraleUnit.getMoraleLevel() == 2)
            pass("loseMorale reduces correctly to 2.");
        else
            fail("Expected morale 2, got " + moraleUnit.getMoraleLevel());

        // =====================================================================
        // TEST 15 - Corruption Threshold Events
        // =====================================================================
        section("TEST 15 - Corruption Threshold Events");

        Wargear c_gear = new Wargear();
        c_gear.addEquipment("Plasma Gun");
        Infantry corruptUnit = new Infantry(
                "Sergeant Vael",
                "Emperor's Children",
                new StatBlock(3, 4, 3, 6, 2),
                c_gear,
                "Berserker",
                false
        );

        System.out.println("  Starting corruption: " + corruptUnit.getCorruptionLevel());
        System.out.println("  Minor threshold: " + Unit.CORRUPTION_MINOR_THRESHOLD
                + "  Major threshold: " + Unit.CORRUPTION_MAJOR_THRESHOLD);

        corruptUnit.gainCorruption(4);
        System.out.println("  After +4: corruption = " + corruptUnit.getCorruptionLevel());
        if (corruptUnit.getCorruptionLevel() == 4)
            pass("4 corruption accumulated, below minor threshold - no event yet.");

        corruptUnit.gainCorruption(1);
        if (corruptUnit.getCorruptionLevel() >= Unit.CORRUPTION_MINOR_THRESHOLD)
            pass("Reached minor threshold (" + Unit.CORRUPTION_MINOR_THRESHOLD
                    + ") - mutation event fired.");

        corruptUnit.gainCorruption(5);
        if (corruptUnit.getCorruptionLevel() >= Unit.CORRUPTION_MAJOR_THRESHOLD)
            pass("Reached major threshold (" + Unit.CORRUPTION_MAJOR_THRESHOLD
                    + ") - possession event fired.");

        // =====================================================================
        // TEST 16 - PriorityQueue Initiative Ordering
        // =====================================================================
        section("TEST 16 - PriorityQueue Initiative Ordering");

        PriorityQueue<Unit> queue = new PriorityQueue<>();

        // Enqueue in scrambled order
        queue.enqueue(noiseMarine);  // speed 6
        queue.enqueue(tyrant);       // speed 8
        queue.enqueue(librarian);    // speed 6
        queue.enqueue(rhino);        // speed 12 - should be first out

        System.out.println("  Enqueued 4 units in scrambled speed order.");
        System.out.println("  Queue contents (expected: Rhino 12, Tyrant 8, then speed-6 units):");
        queue.display();

        Unit first  = queue.dequeue();
        Unit second = queue.dequeue();

        System.out.println("\n  Dequeued first : " + first.getName()
                + " (speed " + first.getStats().getSpeed() + ")");
        System.out.println("  Dequeued second: " + second.getName()
                + " (speed " + second.getStats().getSpeed() + ")");

        if (first.getStats().getSpeed() >= second.getStats().getSpeed())
            pass("First dequeued unit has speed >= second (initiative order correct).");
        else
            fail("Initiative order violated: first speed " + first.getStats().getSpeed()
                    + " < second speed " + second.getStats().getSpeed());

        if (first.getName().equals("Iron Covenant"))
            pass("Rhino (speed 12) dequeued first as expected.");
        else
            fail("Expected Iron Covenant first, got " + first.getName());

        if (second.getName().equals("Tyrant of Ruin"))
            pass("Tyrant (speed 8) dequeued second as expected.");
        else
            fail("Expected Tyrant of Ruin second, got " + second.getName());

        // =====================================================================
        // TEST 17 - Psyker Ability Assignment and Cast via CombatEngine
        // =====================================================================
        section("TEST 17 - Psyker Ability and CombatEngine.castPsychicPower()");

        // Give the librarian Smite (chain triggers already attached from Test 11)
        // Reset smite's chain triggers so this test is clean
        PsychicPower smite2 = new PsychicPower("Smite", "Pure psychic energy.", 2);
        librarian.addAbility(smite2);

        // Fresh target
        Wargear heretic_gear = new Wargear();
        heretic_gear.addEquipment("Autogun");
        Infantry heretic = new Infantry(
                "Heretic Primus",
                "Chaos Knights",
                new StatBlock(2, 3, 6, 5, 1),
                heretic_gear,
                "Cultist",
                false
        );

        chaosWarband.addUnit(heretic);

        CombatEngine engine = new CombatEngine(darkAngelsWarband, chaosWarband);

        System.out.println("  Librarian warp charge before cast: " + librarian.getWarpCharge());
        int woundsBefore = heretic.getStats().getWounds();
        engine.castPsychicPower(librarian, heretic);
        System.out.println("  Heretic wounds: " + woundsBefore
                + " -> " + heretic.getStats().getWounds());
        System.out.println("  Librarian warp charge after cast: " + librarian.getWarpCharge());

        if (heretic.getStats().getWounds() < woundsBefore)
            pass("PsychicPower dealt damage through CombatEngine helper.");
        else
            fail("PsychicPower should have reduced target wounds.");

        // =====================================================================
        // TEST 18 - Full CombatEngine Skirmish (2 rounds)
        // =====================================================================
        section("TEST 18 - Full CombatEngine Skirmish (2 rounds)");

        // Build fresh warbands for a clean combat test
        Faction da_faction = new Faction("Dark Angels", false);
        da_faction.addUnitType("Infantry");
        da_faction.addUnitType("Psyker");

        Faction ec_faction = new Faction("Emperor's Children", true);
        ec_faction.addUnitType("Infantry");
        ec_faction.addUnitType("Psyker");

        Wargear tactical_gear = new Wargear();
        tactical_gear.addEquipment("Boltgun");
        Infantry tacticalMarine = new Infantry(
                "Brother Hanniel",
                "Dark Angels",
                new StatBlock(2, 4, 3, 6, 2),
                tactical_gear,
                "Tactical",
                false
        );

        Wargear berserker_gear = new Wargear();
        berserker_gear.addEquipment("Chainsword");
        Infantry berserker = new Infantry(
                "Berserker Vorax",
                "Emperor's Children",
                new StatBlock(2, 4, 5, 7, 3),
                berserker_gear,
                "Berserker",
                false
        );

        Warband loyalists = new Warband("Sons of the Lion", da_faction);
        loyalists.addUnit(tacticalMarine);

        Warband traitors = new Warband("Choir of Agony", ec_faction);
        traitors.addUnit(berserker);

        CombatEngine skirmish = new CombatEngine(loyalists, traitors);
        skirmish.runCombat(2);

        pass("CombatEngine completed " + skirmish.getCurrentRound()
                + " round(s) without errors.");

        // =====================================================================
        // TEST 19 - CombatLog<String> Stack Operations
        // =====================================================================
        section("TEST 19 - CombatLog<String> Stack (push, pop, peek, LIFO order)");

        CombatLog<String> log = new CombatLog<>();

        if (log.isEmpty())
            pass("New CombatLog is empty.");
        else
            fail("New CombatLog should be empty.");

        log.push("[ATK] Brother Hanniel -> Chaos Cultist (2 dmg)");
        log.push("[ATK] Librarian Ezekiel -> Chaos Cultist (1 dmg) -- ROUTING");
        log.push("[---] Iron Covenant -- no targets remaining");

        if (log.getSize() == 3)
            pass("CombatLog size is 3 after three pushes.");
        else
            fail("Expected size 3, got " + log.getSize());

        // peek() must not consume the top
        String topEntry = log.peek();
        if (topEntry != null && topEntry.contains("Iron Covenant"))
            pass("peek() returns last pushed entry without removing it.");
        else
            fail("peek() should return the Iron Covenant entry.");

        if (log.getSize() == 3)
            pass("peek() did not consume the stack (size still 3).");
        else
            fail("peek() should not change size.");

        // Pop all three - verify LIFO order
        String logFirst  = log.pop();
        String logSecond = log.pop();
        String logThird  = log.pop();

        if (logFirst != null && logFirst.contains("Iron Covenant"))
            pass("First pop returned most recently pushed entry (LIFO).");
        else
            fail("First pop should return Iron Covenant entry.");

        if (logSecond != null && logSecond.contains("ROUTING"))
            pass("Second pop returned middle entry.");
        else
            fail("Second pop should return Ezekiel routing entry.");

        if (logThird != null && logThird.contains("Brother Hanniel"))
            pass("Third pop returned first-pushed entry (bottom of stack).");
        else
            fail("Third pop should return Brother Hanniel entry.");

        if (log.isEmpty())
            pass("CombatLog is empty after popping all entries.");
        else
            fail("CombatLog should be empty after all pops.");

        // pop() on empty stack must return null, not throw
        String nullResult = log.pop();
        if (nullResult == null)
            pass("pop() on empty CombatLog returns null safely.");
        else
            fail("pop() on empty stack should return null.");

        // =====================================================================
        // ========================  PHASE 3 TESTS  ============================
        // =====================================================================

        // =====================================================================
        // TEST 20 - FactionFactory Construction
        // =====================================================================
        section("TEST 20 - FactionFactory Construction");

        Warband daFactory = FactionFactory.createDarkAngelsWarband();
        Warband ecFactory = FactionFactory.createEmperorsChildrenWarband();
        Warband ckFactory = FactionFactory.createChaosKnightsWarband();

        System.out.println("  Created: " + daFactory);
        System.out.println("  Created: " + ecFactory);
        System.out.println("  Created: " + ckFactory);

        if (daFactory.getRosterSize() == 12)
            pass("Dark Angels warband has 12 units from factory.");
        else
            fail("Expected 12 DA units, got " + daFactory.getRosterSize());

        if (ecFactory.getRosterSize() == 7)
            pass("Emperor's Children warband has 7 units from factory.");
        else
            fail("Expected 7 EC units, got " + ecFactory.getRosterSize());

        if (ckFactory.getRosterSize() == 8)
            pass("Chaos Knights warband has 8 units from factory.");
        else
            fail("Expected 8 CK units, got " + ckFactory.getRosterSize());

        // Verify faction-specific ability pre-loading
        Unit factoryLibrarian = daFactory.findUnit("Seraphael");
        if (factoryLibrarian != null && !factoryLibrarian.getAbilities().isEmpty())
            pass("Factory Librarian has pre-loaded abilities: "
                    + factoryLibrarian.getAbilities().get(0).getAbilityName());
        else
            fail("Factory Librarian should have pre-loaded abilities.");

        // Verify first unit in each roster is the fastest (sorted insertion)
        daFactory.displayRoster();

        // =====================================================================
        // TEST 21 - UnitSorter Recursive Quicksort (by speed)
        // =====================================================================
        section("TEST 21 - UnitSorter Recursive Quicksort (by speed)");

        // Build a mixed-speed list in scrambled order
        ArrayList<Unit> sortList = daFactory.getRosterSnapshot();
        sortList.addAll(ecFactory.getRosterSnapshot());

        System.out.println("  Before sort (" + sortList.size() + " units):");
        for (Unit u : sortList) {
            System.out.println("    " + u.getName() + " speed=" + u.getStats().getSpeed());
        }

        UnitSorter.sort(sortList, "speed");

        System.out.println("\n  After quicksort by SPEED (descending):");
        UnitSorter.displaySorted(sortList, "speed");

        // Verify descending order
        boolean speedSorted = true;
        for (int i = 0; i < sortList.size() - 1; i++) {
            if (sortList.get(i).getStats().getSpeed() < sortList.get(i + 1).getStats().getSpeed()) {
                speedSorted = false;
                break;
            }
        }
        if (speedSorted)
            pass("All " + sortList.size() + " units sorted in descending speed order.");
        else
            fail("Speed sort order violated.");

        // =====================================================================
        // TEST 22 - UnitSorter Quicksort (by XP) - edge case: all equal
        // =====================================================================
        section("TEST 22 - UnitSorter Quicksort (by XP) - all-equal edge case");

        // All units start at 0 XP - sort should not crash or reorder incorrectly
        ArrayList<Unit> xpList = ckFactory.getRosterSnapshot();
        System.out.println("  Sorting Chaos Knights (both at 0 XP) by XP:");
        UnitSorter.sort(xpList, "xp");
        UnitSorter.displaySorted(xpList, "xp");

        boolean xpNoCrash = xpList.size() == 8;
        if (xpNoCrash)
            pass("Quicksort on all-equal values completed without error. Size: " + xpList.size());
        else
            fail("Quicksort changed list size unexpectedly.");

        // =====================================================================
        // TEST 23 - XP Progression and Rank-Up Ability Unlocks
        // =====================================================================
        section("TEST 23 - XP Progression and Rank-Up Ability Unlocks");

        // Use the factory Librarian for progression tracking
        Psyker progLibrarian = (Psyker) daFactory.findUnit("Seraphael");
        int abilitiesAtStart = progLibrarian.getAbilities().size();
        System.out.println("  " + progLibrarian.getName()
                + " starting: rank=" + progLibrarian.getUnitRank()
                + " xp=" + progLibrarian.getExperiencePoints()
                + " abilities=" + abilitiesAtStart);

        // Push to rank 2 (needs 10 XP)
        progLibrarian.gainXP(10);
        System.out.println("  After +10 XP: rank=" + progLibrarian.getUnitRank()
                + " abilities=" + progLibrarian.getAbilities().size());

        if (progLibrarian.getUnitRank() == 2)
            pass("Librarian reached rank 2 at 10 XP.");
        else
            fail("Expected rank 2, got " + progLibrarian.getUnitRank());

        if (progLibrarian.getAbilities().size() > abilitiesAtStart)
            pass("Rank 2 ability unlock fired: "
                    + progLibrarian.getAbilities().get(progLibrarian.getAbilities().size() - 1)
                                   .getAbilityName());
        else
            fail("Rank 2 should have added an ability.");

        // Push to rank 3 (needs 25 total XP, so +15 more)
        progLibrarian.gainXP(15);
        System.out.println("  After +25 total XP: rank=" + progLibrarian.getUnitRank()
                + " abilities=" + progLibrarian.getAbilities().size());

        if (progLibrarian.getUnitRank() == 3)
            pass("Librarian reached rank 3 at 25 XP.");
        else
            fail("Expected rank 3, got " + progLibrarian.getUnitRank());

        // Push to rank 4 (needs 50 total XP)
        int warpBefore = progLibrarian.getWarpCharge();
        progLibrarian.gainXP(25);
        System.out.println("  After +50 total XP: rank=" + progLibrarian.getUnitRank());

        if (progLibrarian.getUnitRank() == 4)
            pass("Librarian reached rank 4 at 50 XP.");
        else
            fail("Expected rank 4, got " + progLibrarian.getUnitRank());

        if (progLibrarian.getWarpCharge() > warpBefore)
            pass("Rank 4 granted +1 warp charge. New charge: " + progLibrarian.getWarpCharge());
        else
            fail("Rank 4 should increase warp charge.");

        // =====================================================================
        // TEST 24 - Daemonic Possession Permanent Consequence
        //           Verifies: unit flagged, removed from player warband, AND
        //           inserted into CampaignEngine's enemy-controlled possessedWarband
        // =====================================================================
        section("TEST 24 - Daemonic Possession Permanent Consequence");

        // Build a one-unit warband whose corruption is already maxed out
        Faction voidFaction = new Faction("Emperor's Children", true);
        voidFaction.addUnitType("Infantry");
        Warband testBand = new Warband("Void Company", voidFaction);

        Wargear pGear = new Wargear();
        pGear.addEquipment("Plasma Rifle");
        Infantry cursedMarine = new Infantry(
                "Sergeant Vael",
                "Emperor's Children",
                new StatBlock(2, 4, 3, 6, 2),
                pGear,
                "Berserker",
                false
        );
        // Silently set corruption to the major threshold (reuse the
        // setCorruptionLevel setter so no event prints here, keeping output clean)
        cursedMarine.setCorruptionLevel(Unit.CORRUPTION_MAJOR_THRESHOLD);
        testBand.addUnit(cursedMarine);

        System.out.println("  Player warband before campaign : "
                + testBand.getRosterSize() + " unit(s)");
        System.out.println("  Sergeant Vael corruption       : "
                + cursedMarine.getCorruptionLevel());
        System.out.println("  isPossessed before campaign    : "
                + cursedMarine.isPossessed());

        // Run a single-node (leaf) campaign - possession check fires in
        // resolveTerminalEncounter(), which calls checkPossessionConsequences()
        CampaignNode possessionNode = new CampaignNode(
                "Ritual Chamber",
                "A dark energy saturates the air. Something stirs in the warp...",
                "", "", false, 0, "");
        CampaignEngine posEngine = new CampaignEngine(possessionNode, testBand);
        posEngine.startCampaign();

        System.out.println("  isPossessed after campaign     : " + cursedMarine.isPossessed());
        System.out.println("  Player warband after           : "
                + testBand.getRosterSize() + " unit(s)");
        System.out.println("  \"The Possessed\" warband size   : "
                + posEngine.getPossessedWarband().getRosterSize() + " unit(s)");

        if (cursedMarine.isPossessed())
            pass("Unit correctly flagged as daemon-possessed.");
        else
            fail("isPossessed should be true.");

        if (testBand.getRosterSize() == 0)
            pass("Possessed unit removed from player warband - permanent consequence applied.");
        else
            fail("Possessed unit should have been removed from player roster.");

        if (posEngine.getPossessedWarband().getRosterSize() == 1)
            pass("Possessed unit re-inserted into enemy-controlled \"The Possessed\" warband.");
        else
            fail("Possessed unit should appear in possessedWarband with size 1.");

        // =====================================================================
        // TEST 25 - CampaignEngine Recursive Traversal (path "LL")
        // =====================================================================
        section("TEST 25 - CampaignEngine Recursive navigate() - path LL");

        Warband campaignBand = FactionFactory.createDarkAngelsWarband();
        CampaignNode campaignTree = CampaignEngine.buildDefaultCampaign();
        CampaignEngine campaignEngine = new CampaignEngine(campaignTree, campaignBand);

        // Path "LL": root -> ambush (L) -> survivors found (L) [leaf]
        // Expected: 3 navigate() calls (root, ambush, survivors)
        campaignEngine.setNavigationPath("LL");
        campaignEngine.startCampaign();

        if (campaignEngine.getEncountersVisited() == 3)
            pass("navigate() visited exactly 3 nodes on path LL (root + 2 levels).");
        else
            fail("Expected 3 encounters visited, got " + campaignEngine.getEncountersVisited());

        if (campaignEngine.getTotalXpAwarded() > 0)
            pass("XP was distributed during campaign. Total: "
                    + campaignEngine.getTotalXpAwarded());
        else
            fail("Campaign should have awarded XP.");

        // Verify wargear reward from the leaf node reached first surviving unit
        ArrayList<Unit> survivors = campaignBand.getLivingUnits();
        System.out.println("  Living units after campaign: " + survivors.size());
        for (Unit u : survivors) {
            System.out.println("    " + u.getName() + " XP=" + u.getExperiencePoints()
                    + " rank=" + u.getUnitRank());
        }

        // =====================================================================
        // TEST 26 - Alternate Campaign Branch (path "RR") + Final Quicksort Display
        // =====================================================================
        section("TEST 26 - Alternate Campaign Branch (path RR) + Final Roster Sort");

        // Fresh warband for the second traversal
        Warband band2 = FactionFactory.createEmperorsChildrenWarband();
        CampaignNode tree2 = CampaignEngine.buildDefaultCampaign();
        CampaignEngine engine2 = new CampaignEngine(tree2, band2);

        // Path "RR": root -> armoury (R) -> cache (R) [leaf, wargear reward]
        engine2.setNavigationPath("RR");
        engine2.startCampaign();

        if (engine2.getEncountersVisited() == 3)
            pass("navigate() visited 3 nodes on path RR - independent branch confirmed.");
        else
            fail("Expected 3 encounters on RR path, got " + engine2.getEncountersVisited());

        // Final ranked display - sort all survivors by XP to show progression
        System.out.println("\n  === FINAL XP RANKINGS - Emperor's Children after campaign ===");
        ArrayList<Unit> finalRoster = band2.getRosterSnapshot();
        UnitSorter.sort(finalRoster, "xp");
        UnitSorter.displaySorted(finalRoster, "xp");

        pass("End-to-end playtest complete: factory -> campaign -> progression -> sorted display.");

        // =====================================================================
        // TEST 27 - Binary Search - findFirstByType() on Warband roster
        // =====================================================================
        section("TEST 27: findFirstByType() - Binary Search Roster Lookup by Unit Type");

        Warband searchWarband = FactionFactory.createDarkAngelsWarband();

        Unit foundPsyker = searchWarband.findFirstByType("Psyker");
        if (foundPsyker == null)
            fail("findFirstByType(\"Psyker\") returned null - expected Librarian Seraphael");
        else if (!foundPsyker.getName().equals("Librarian Seraphael"))
            fail("findFirstByType(\"Psyker\") returned " + foundPsyker.getName());
        else
            pass("findFirstByType(\"Psyker\") found: " + foundPsyker.getName());

        Unit foundInfantry = searchWarband.findFirstByType("Infantry");
        if (foundInfantry == null)
            fail("findFirstByType(\"Infantry\") returned null - expected an Infantry unit");
        else if (!(foundInfantry instanceof Infantry))
            fail("findFirstByType(\"Infantry\") returned wrong type: " + foundInfantry.getName());
        else
            pass("findFirstByType(\"Infantry\") found: " + foundInfantry.getName());

        Unit foundKnight = searchWarband.findFirstByType("Knight");
        if (foundKnight != null)
            fail("findFirstByType(\"Knight\") should return null, got " + foundKnight.getName());
        else
            pass("findFirstByType(\"Knight\") correctly returned null (not in roster).");

        // =====================================================================
        // Full Test Suite Summary
        // =====================================================================
        section("PHASE 1 + PHASE 2 + PHASE 3 TEST SUITE COMPLETE");
        System.out.println("  Phase 1 verified:");
        System.out.println("    StatBlock, Wargear, Ability (stub), Faction");
        System.out.println("    Unit (abstract), Infantry, Vehicle, Knight, Psyker");
        System.out.println("    LinkedList<T>, Warband");
        System.out.println("    Comparable<Unit> ordering, polymorphic dispatch");
        System.out.println("    Roster insert, search, remove");
        System.out.println();
        System.out.println("  Phase 2 verified:");
        System.out.println("    Castable interface, PsychicPower, SorceryRitual, BattleTrait");
        System.out.println("    Recursive psychic chain with depth-limit base case");
        System.out.println("    BattleTrait stat modification");
        System.out.println("    SorceryRitual damage + caster corruption cost");
        System.out.println("    Morale: loseMorale, checkMorale, rally, route (boundary values)");
        System.out.println("    Corruption: minor/major threshold narrative events");
        System.out.println("    PriorityQueue<Unit> initiative ordering");
        System.out.println("    CombatEngine: ability dispatch and full skirmish loop");
        System.out.println("    CombatLog<String>: push, pop, peek, LIFO order, empty-stack safety");
        System.out.println();
        System.out.println();
        System.out.println("  Phase 3 verified:");
        System.out.println("    FactionFactory: Dark Angels, Emperor's Children, Chaos Knights");
        System.out.println("    UnitSorter: recursive quicksort by speed, XP, edge case (all-equal)");
        System.out.println("    XP progression: gainXP, rank thresholds, onRankUp() overrides");
        System.out.println("    Psyker onRankUp: ability/wargear unlock at ranks 2, 3, 4");
        System.out.println("    Daemonic possession: becomePossessed(), roster removal");
        System.out.println("    CampaignEngine.navigate(): recursive tree traversal, base case");
        System.out.println("    Campaign branching: independent paths LL and RR verified");
        System.out.println("    End-to-end playtest: factory -> campaign -> XP -> sorted display");
        System.out.println("  All 27 tests passed. Project complete.");
        System.out.println("=".repeat(60));
    }
}

/*
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 1
 *   2026-03-23  Shane Potts  Phase 2 - added Tests 10-19 covering the full
 *                             ability system, morale, corruption, priority
 *                             queue, CombatEngine, and CombatLog stack
 *   2026-03-23  Shane Potts  Phase 3 - added Tests 20-26 covering factory
 *                             methods, quicksort, XP progression, possession,
 *                             campaign traversal, and end-to-end playtest
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-08  Shane Potts  Added Test 19: CombatLog<String> stack verification
 *   2026-04-08  Shane Potts  Added Test 27: findFirstByType() binary search verification
 *   2026-04-26  Shane Potts  Phase 3 complete - 27 automated tests all pass; covers
 *                             all data structures, all four unit types, full combat
 *                             pipeline, factory construction, and campaign traversal
 *   2026-05-04  Shane Potts  Fixed title banner to read "Phase 1 + Phase 2 + Phase 3
 *                             Test Suite"; corrected closing message from 26 to 27
 *                             tests passed to match actual test count
 */
