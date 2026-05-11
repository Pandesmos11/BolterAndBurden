/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Represents a Warhammer 40,000 10th edition Detachment — a
 *              faction-specific rule set that confers a named passive rule on
 *              the warband and supplies a hand of Stratagems the player may
 *              spend Command Points to activate during combat.
 *
 *              Nine total detachments are implemented — three per faction.
 *              The player selects one before the campaign begins.
 *
 *                DARK ANGELS
 *                  [1] Inner Circle Task Force  — Sustained Hits 1 on all units
 *                  [2] Unforgiven Task Force     — Lethal Hits on all units
 *                  [3] Deathwing Strike Force    — Feel No Pain 5+ on all units
 *
 *                EMPEROR'S CHILDREN
 *                  [1] Kakophoni                 — Feel No Pain 6+ on all units
 *                  [2] The Chosen of Slaanesh    — Sustained Hits 2 on all units
 *                  [3] Children of Torment       — Feel No Pain 5+ on all units
 *
 *                CHAOS KNIGHTS
 *                  [1] Traitoris Lance           — 5++ invuln on Vehicle/Knight
 *                  [2] Iconoclast Household      — Sustained Hits 1 on Vehicle/Knight
 *                  [3] Infernal Court            — Feel No Pain 5+ on Vehicle/Knight
 *
 *              applyPassiveRule() injects a BattleTrait into each qualifying unit
 *              at the start of a combat, reusing the existing CombatEngine trigger
 *              pipeline. resetForNewCombat() resets all Stratagem used-flags so
 *              the full hand is available at the start of each engagement.
 * Inputs:      Warband (applyPassiveRule), no direct user input
 * Outputs:     Console narrative when passive fires; Stratagem list for CombatEngine
 *
 * Change Log:
 *   2026-04-09  Shane Potts  Initial creation — Phase 4 Detachment/CP system;
 *                             private constructor + 9 static factory methods
 *                             (3 per faction); vehicleOnly flag controls whether
 *                             passive BattleTrait is applied to all units or only
 *                             Vehicle/Knight units; applyPassiveRule() injects
 *                             BattleTrait at combat start; resetForNewCombat()
 *                             resets all Stratagem used-flags between encounters
 *   2026-04-09  Shane Potts  Expanded to three detachments per faction; player
 *                             selects from a menu in Game.selectDetachment()
 *   2026-04-26  Shane Potts  Phase 4 complete - no further changes this phase
 */

package bolterandburden;

import java.util.ArrayList;

public class Detachment {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private final String             detachmentName;
    private final String             passiveRuleName;
    private final String             passiveRuleDesc;
    private final ArrayList<Stratagem> stratagems;

    /** Trigger to use when injecting the passive rule BattleTrait. */
    private final String             passiveTrigger;

    /** Modifier value for the passive BattleTrait (e.g. invuln save threshold). */
    private final int                passiveModifier;

    /**
     * When true, the passive rule applies only to Vehicle/Knight units.
     * When false, it applies to all living units.
     */
    private final boolean            vehicleOnly;

    // -------------------------------------------------------------------------
    // Construction
    // -------------------------------------------------------------------------

    /**
     * Full constructor. Called by the static factory methods below.
     *
     * @param detachmentName  Display name (e.g. "Inner Circle Task Force")
     * @param passiveRuleName Short name for the passive BattleTrait
     * @param passiveRuleDesc Description of the passive
     * @param passiveTrigger  BattleTrait trigger string
     * @param passiveModifier Modifier value for the BattleTrait
     * @param vehicleOnly     true = only applies to Vehicle/Knight units
     * @param stratagems      List of Stratagems available to this detachment
     */
    private Detachment(String detachmentName, String passiveRuleName,
                       String passiveRuleDesc, String passiveTrigger,
                       int passiveModifier, boolean vehicleOnly,
                       ArrayList<Stratagem> stratagems) {
        this.detachmentName  = detachmentName;
        this.passiveRuleName = passiveRuleName;
        this.passiveRuleDesc = passiveRuleDesc;
        this.passiveTrigger  = passiveTrigger;
        this.passiveModifier = passiveModifier;
        this.vehicleOnly     = vehicleOnly;
        this.stratagems      = stratagems;
    }

    // -------------------------------------------------------------------------
    // Passive Rule Application
    // -------------------------------------------------------------------------

    /**
     * Injects the detachment passive rule into every qualifying unit in the
     * given warband. Called once at the start of each combat engagement.
     * The BattleTrait is added even if the unit already has a similar trait —
     * the CombatEngine helper methods stack correctly.
     *
     * @param warband The player's warband to buff
     */
    public void applyPassiveRule(Warband warband) {
        System.out.println(Color.c("  [DETACHMENT] " + detachmentName
                + " — " + passiveRuleName + " applied to warband.",
                Color.BOLD + Color.CYAN));
        for (Unit u : warband.getLivingUnits()) {
            if (vehicleOnly && !(u instanceof Vehicle) && !(u instanceof Knight)) {
                continue;
            }
            u.addAbility(new BattleTrait(
                    "Detachment: " + passiveRuleName,
                    passiveRuleDesc,
                    "toughness", passiveModifier, passiveTrigger));
        }
    }

    // -------------------------------------------------------------------------
    // Stratagem Management
    // -------------------------------------------------------------------------

    /**
     * Resets all Stratagems in this detachment so they are available again
     * for the next combat engagement. Call between encounters.
     */
    public void resetForNewCombat() {
        for (Stratagem s : stratagems) {
            s.resetForNewCombat();
        }
    }

    /**
     * Returns the full list of Stratagems belonging to this detachment.
     *
     * @return Stratagem list (may include already-used entries; check isUsedThisCombat())
     */
    public ArrayList<Stratagem> getStratagems() {
        return stratagems;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /** @return Detachment display name */
    public String getDetachmentName() { return detachmentName; }

    /** @return Passive rule display name */
    public String getPassiveRuleName() { return passiveRuleName; }

    // =========================================================================
    // Static Factory Methods — one per faction
    // =========================================================================

    /**
     * Creates the Dark Angels "Inner Circle Task Force" detachment.
     *
     * <p><b>Passive:</b> Doctrines of the Rock — all units gain Sustained Hits 1
     * (representing re-rolls of hit rolls of 1 in the abstracted pipeline).
     *
     * <p><b>Stratagems:</b>
     * <pre>
     * Transhuman Physiology  1CP  FEEL_NO_PAIN  5+   ANY_FRIENDLY
     * Wisdom of the Ancients 2CP  SUSTAINED_HITS 1   ANY_FRIENDLY
     * Honour the Chapter     1CP  EXTRA_ATTACKS  2   INFANTRY_ONLY
     * Armour of Contempt     1CP  INVULN_BOOST   4   ANY_FRIENDLY
     * Swift Retribution      1CP  FIGHTS_FIRST   0   ANY_FRIENDLY
     * Rites of Battle        2CP  HEAL           3   ANY_FRIENDLY
     * </pre>
     *
     * @return Configured Dark Angels Detachment
     */
    public static Detachment createDarkAngelsDetachment() {
        ArrayList<Stratagem> strats = new ArrayList<>();

        strats.add(new Stratagem(
                "Transhuman Physiology",
                "1CP — This unit gains Feel No Pain 5+ this round",
                1, Stratagem.EffectType.FEEL_NO_PAIN,
                Stratagem.TargetScope.ANY_FRIENDLY, 5));

        strats.add(new Stratagem(
                "Wisdom of the Ancients",
                "2CP — This unit gains Sustained Hits 1 this round",
                2, Stratagem.EffectType.SUSTAINED_HITS,
                Stratagem.TargetScope.ANY_FRIENDLY, 1));

        strats.add(new Stratagem(
                "Honour the Chapter",
                "1CP — This Infantry unit makes 2 extra attacks this round",
                1, Stratagem.EffectType.EXTRA_ATTACKS,
                Stratagem.TargetScope.INFANTRY_ONLY, 2));

        strats.add(new Stratagem(
                "Armour of Contempt",
                "1CP — This unit gains a 4++ invulnerable save this round",
                1, Stratagem.EffectType.INVULN_BOOST,
                Stratagem.TargetScope.ANY_FRIENDLY, 4));

        strats.add(new Stratagem(
                "Swift Retribution",
                "1CP — This unit fights first this round",
                1, Stratagem.EffectType.FIGHTS_FIRST,
                Stratagem.TargetScope.ANY_FRIENDLY, 0));

        strats.add(new Stratagem(
                "Rites of Battle",
                "2CP — Restore 3 wounds to this unit",
                2, Stratagem.EffectType.HEAL,
                Stratagem.TargetScope.ANY_FRIENDLY, 3));

        return new Detachment(
                "Inner Circle Task Force",
                "Doctrines of the Rock",
                "Detachment passive: Sustained Hits 1 on all units",
                "sustained_hits", 1, false, strats);
    }

    /**
     * Creates the Emperor's Children "Kakophoni" detachment.
     *
     * <p><b>Passive:</b> Slaaneshi Blessings — all units gain Feel No Pain 6+
     * (representing the general daemonic resilience of the Children of Slaanesh).
     *
     * <p><b>Stratagems:</b>
     * <pre>
     * Endless Cacophony      1CP  EXTRA_ATTACKS  3   INFANTRY_ONLY
     * Stimulated by Agony    1CP  FEEL_NO_PAIN   5+  ANY_FRIENDLY
     * Excess of Violence     2CP  DEVASTATING    —   INFANTRY_ONLY
     * Slaaneshi Enthrallment 1CP  FIGHTS_FIRST   —   ANY_FRIENDLY
     * Delicious Agonies      1CP  INVULN_BOOST   4   ANY_FRIENDLY
     * Dark Pact              2CP  HEAL           4   ANY_FRIENDLY
     * </pre>
     *
     * @return Configured Emperor's Children Detachment
     */
    public static Detachment createEmperorsChildrenDetachment() {
        ArrayList<Stratagem> strats = new ArrayList<>();

        strats.add(new Stratagem(
                "Endless Cacophony",
                "1CP — This Infantry unit makes 3 extra attacks this round",
                1, Stratagem.EffectType.EXTRA_ATTACKS,
                Stratagem.TargetScope.INFANTRY_ONLY, 3));

        strats.add(new Stratagem(
                "Stimulated by Agony",
                "1CP — This unit gains Feel No Pain 5+ this round",
                1, Stratagem.EffectType.FEEL_NO_PAIN,
                Stratagem.TargetScope.ANY_FRIENDLY, 5));

        strats.add(new Stratagem(
                "Excess of Violence",
                "2CP — This Infantry unit gains Devastating Wounds this round",
                2, Stratagem.EffectType.DEVASTATING,
                Stratagem.TargetScope.INFANTRY_ONLY, 1));

        strats.add(new Stratagem(
                "Slaaneshi Enthrallment",
                "1CP — This unit fights first this round",
                1, Stratagem.EffectType.FIGHTS_FIRST,
                Stratagem.TargetScope.ANY_FRIENDLY, 0));

        strats.add(new Stratagem(
                "Delicious Agonies",
                "1CP — This unit gains a 4++ invulnerable save this round",
                1, Stratagem.EffectType.INVULN_BOOST,
                Stratagem.TargetScope.ANY_FRIENDLY, 4));

        strats.add(new Stratagem(
                "Dark Pact",
                "2CP — Restore 4 wounds to this unit",
                2, Stratagem.EffectType.HEAL,
                Stratagem.TargetScope.ANY_FRIENDLY, 4));

        return new Detachment(
                "Kakophoni",
                "Slaaneshi Blessings",
                "Detachment passive: Feel No Pain 6+ on all units",
                "feel_no_pain", 6, false, strats);
    }

    /**
     * Creates the Chaos Knights "Traitoris Lance" detachment.
     *
     * <p><b>Passive:</b> Traitor Households — all Vehicle and Knight units gain
     * a 5++ invulnerable save (representing daemonic pacts and warp-touched armour).
     *
     * <p><b>Stratagems:</b>
     * <pre>
     * Emergency Repairs      1CP  HEAL           4   VEHICLE_ONLY
     * Rotate Ion Shields     1CP  INVULN_BOOST   4   VEHICLE_ONLY
     * Warp Surge             2CP  DEVASTATING    —   VEHICLE_ONLY
     * Terrifying Rampage     1CP  EXTRA_ATTACKS  3   VEHICLE_ONLY
     * Machine Spirit Rampant 2CP  FIGHTS_FIRST   —   VEHICLE_ONLY
     * Infernal Lash          1CP  SUSTAINED_HITS 2   VEHICLE_ONLY
     * </pre>
     *
     * @return Configured Chaos Knights Detachment
     */
    public static Detachment createChaosKnightsDetachment() {
        ArrayList<Stratagem> strats = new ArrayList<>();

        strats.add(new Stratagem(
                "Emergency Repairs",
                "1CP — Restore 4 wounds to this Vehicle/Knight unit",
                1, Stratagem.EffectType.HEAL,
                Stratagem.TargetScope.VEHICLE_ONLY, 4));

        strats.add(new Stratagem(
                "Rotate Ion Shields",
                "1CP — This Vehicle/Knight gains a 4++ invulnerable save this round",
                1, Stratagem.EffectType.INVULN_BOOST,
                Stratagem.TargetScope.VEHICLE_ONLY, 4));

        strats.add(new Stratagem(
                "Warp Surge",
                "2CP — This Vehicle/Knight gains Devastating Wounds this round",
                2, Stratagem.EffectType.DEVASTATING,
                Stratagem.TargetScope.VEHICLE_ONLY, 1));

        strats.add(new Stratagem(
                "Terrifying Rampage",
                "1CP — This Vehicle/Knight makes 3 extra attacks this round",
                1, Stratagem.EffectType.EXTRA_ATTACKS,
                Stratagem.TargetScope.VEHICLE_ONLY, 3));

        strats.add(new Stratagem(
                "Machine Spirit Rampant",
                "2CP — This Vehicle/Knight fights first this round",
                2, Stratagem.EffectType.FIGHTS_FIRST,
                Stratagem.TargetScope.VEHICLE_ONLY, 0));

        strats.add(new Stratagem(
                "Infernal Lash",
                "1CP — This Vehicle/Knight gains Sustained Hits 2 this round",
                1, Stratagem.EffectType.SUSTAINED_HITS,
                Stratagem.TargetScope.VEHICLE_ONLY, 2));

        return new Detachment(
                "Traitoris Lance",
                "Traitor Households",
                "Detachment passive: 5++ invulnerable save on all Vehicle/Knight units",
                "invuln_save", 5, true, strats);
    }

    // =========================================================================
    // DARK ANGELS — additional detachments [2] and [3]
    // =========================================================================

    /**
     * Creates the Dark Angels "Unforgiven Task Force" detachment.
     *
     * <p><b>Passive:</b> Oaths of the Unforgiven — all units gain Lethal Hits
     * (natural 6s on the hit roll automatically wound, skipping the wound roll).
     * Lethal Hits synergises with the high attack counts on Lion, Azrael, and
     * the Deathwing Knights, guaranteeing some wounds every round.
     *
     * <p><b>Stratagems:</b>
     * <pre>
     * Clenched Fist          1CP  HEAL           3   ANY_FRIENDLY
     * Death Knell            2CP  DEVASTATING    —   INFANTRY_ONLY
     * Relentless Advance     1CP  EXTRA_ATTACKS  2   ANY_FRIENDLY
     * Grim Resolve           1CP  FEEL_NO_PAIN   5+  ANY_FRIENDLY
     * Vengeance of the Fallen 2CP SUSTAINED_HITS 2   ANY_FRIENDLY
     * Iron Resolve           1CP  INVULN_BOOST   4   ANY_FRIENDLY
     * </pre>
     *
     * @return Configured Dark Angels Unforgiven Task Force Detachment
     */
    public static Detachment createDarkAngelsUnforgivenDetachment() {
        ArrayList<Stratagem> strats = new ArrayList<>();

        strats.add(new Stratagem(
                "Clenched Fist",
                "1CP — Restore 3 wounds to this unit",
                1, Stratagem.EffectType.HEAL,
                Stratagem.TargetScope.ANY_FRIENDLY, 3));

        strats.add(new Stratagem(
                "Death Knell",
                "2CP — This Infantry unit gains Devastating Wounds this round",
                2, Stratagem.EffectType.DEVASTATING,
                Stratagem.TargetScope.INFANTRY_ONLY, 1));

        strats.add(new Stratagem(
                "Relentless Advance",
                "1CP — This unit makes 2 extra attacks this round",
                1, Stratagem.EffectType.EXTRA_ATTACKS,
                Stratagem.TargetScope.ANY_FRIENDLY, 2));

        strats.add(new Stratagem(
                "Grim Resolve",
                "1CP — This unit gains Feel No Pain 5+ this round",
                1, Stratagem.EffectType.FEEL_NO_PAIN,
                Stratagem.TargetScope.ANY_FRIENDLY, 5));

        strats.add(new Stratagem(
                "Vengeance of the Fallen",
                "2CP — This unit gains Sustained Hits 2 this round",
                2, Stratagem.EffectType.SUSTAINED_HITS,
                Stratagem.TargetScope.ANY_FRIENDLY, 2));

        strats.add(new Stratagem(
                "Iron Resolve",
                "1CP — This unit gains a 4++ invulnerable save this round",
                1, Stratagem.EffectType.INVULN_BOOST,
                Stratagem.TargetScope.ANY_FRIENDLY, 4));

        return new Detachment(
                "Unforgiven Task Force",
                "Oaths of the Unforgiven",
                "Detachment passive: Lethal Hits on all units (6s to hit auto-wound)",
                "lethal_hits", 1, false, strats);
    }

    /**
     * Creates the Dark Angels "Deathwing Strike Force" detachment.
     *
     * <p><b>Passive:</b> Deathwing Resilience — all units gain Feel No Pain 5+,
     * representing the legendary endurance of First Company veterans and their
     * gene-forged resistance to even the most grievous wounds.
     *
     * <p><b>Stratagems:</b>
     * <pre>
     * Fortress of Resolve    1CP  INVULN_BOOST   4   ANY_FRIENDLY
     * Eternal Warrior        2CP  FEEL_NO_PAIN   4+  ANY_FRIENDLY
     * Deathwing Charge       1CP  FIGHTS_FIRST   —   ANY_FRIENDLY
     * Litanies of Hate       1CP  SUSTAINED_HITS 1   ANY_FRIENDLY
     * Iron Halo              1CP  HEAL           4   ANY_FRIENDLY
     * Unbroken Vow           2CP  EXTRA_ATTACKS  3   INFANTRY_ONLY
     * </pre>
     *
     * @return Configured Dark Angels Deathwing Strike Force Detachment
     */
    public static Detachment createDarkAngelsDeathwingDetachment() {
        ArrayList<Stratagem> strats = new ArrayList<>();

        strats.add(new Stratagem(
                "Fortress of Resolve",
                "1CP — This unit gains a 4++ invulnerable save this round",
                1, Stratagem.EffectType.INVULN_BOOST,
                Stratagem.TargetScope.ANY_FRIENDLY, 4));

        strats.add(new Stratagem(
                "Eternal Warrior",
                "2CP — This unit gains Feel No Pain 4+ this round",
                2, Stratagem.EffectType.FEEL_NO_PAIN,
                Stratagem.TargetScope.ANY_FRIENDLY, 4));

        strats.add(new Stratagem(
                "Deathwing Charge",
                "1CP — This unit fights first this round",
                1, Stratagem.EffectType.FIGHTS_FIRST,
                Stratagem.TargetScope.ANY_FRIENDLY, 0));

        strats.add(new Stratagem(
                "Litanies of Hate",
                "1CP — This unit gains Sustained Hits 1 this round",
                1, Stratagem.EffectType.SUSTAINED_HITS,
                Stratagem.TargetScope.ANY_FRIENDLY, 1));

        strats.add(new Stratagem(
                "Iron Halo",
                "1CP — Restore 4 wounds to this unit",
                1, Stratagem.EffectType.HEAL,
                Stratagem.TargetScope.ANY_FRIENDLY, 4));

        strats.add(new Stratagem(
                "Unbroken Vow",
                "2CP — This Infantry unit makes 3 extra attacks this round",
                2, Stratagem.EffectType.EXTRA_ATTACKS,
                Stratagem.TargetScope.INFANTRY_ONLY, 3));

        return new Detachment(
                "Deathwing Strike Force",
                "Deathwing Resilience",
                "Detachment passive: Feel No Pain 5+ on all units",
                "feel_no_pain", 5, false, strats);
    }

    // =========================================================================
    // EMPEROR'S CHILDREN — additional detachments [2] and [3]
    // =========================================================================

    /**
     * Creates the Emperor's Children "The Chosen of Slaanesh" detachment.
     *
     * <p><b>Passive:</b> Hypersensory Assault — all units gain Sustained Hits 2,
     * double the standard bonus. Every natural 6 on the hit roll generates two
     * additional hits, reflecting the frenzied, sensation-drunk fighting style
     * of Slaanesh's elite warriors at full excess.
     *
     * <p><b>Stratagems:</b>
     * <pre>
     * Graceful Slaughter     1CP  FIGHTS_FIRST   —   ANY_FRIENDLY
     * Torment Incarnate      2CP  DEVASTATING    —   INFANTRY_ONLY
     * Hypnotic Gaze          1CP  FEEL_NO_PAIN   5+  ANY_FRIENDLY
     * Cacophonous Assault    1CP  EXTRA_ATTACKS  3   INFANTRY_ONLY
     * Silver Tongue          1CP  INVULN_BOOST   4   ANY_FRIENDLY
     * Warp-kissed Flesh      2CP  HEAL           5   ANY_FRIENDLY
     * </pre>
     *
     * @return Configured Emperor's Children Chosen of Slaanesh Detachment
     */
    public static Detachment createEmperorsChildrenChosenDetachment() {
        ArrayList<Stratagem> strats = new ArrayList<>();

        strats.add(new Stratagem(
                "Graceful Slaughter",
                "1CP — This unit fights first this round",
                1, Stratagem.EffectType.FIGHTS_FIRST,
                Stratagem.TargetScope.ANY_FRIENDLY, 0));

        strats.add(new Stratagem(
                "Torment Incarnate",
                "2CP — This Infantry unit gains Devastating Wounds this round",
                2, Stratagem.EffectType.DEVASTATING,
                Stratagem.TargetScope.INFANTRY_ONLY, 1));

        strats.add(new Stratagem(
                "Hypnotic Gaze",
                "1CP — This unit gains Feel No Pain 5+ this round",
                1, Stratagem.EffectType.FEEL_NO_PAIN,
                Stratagem.TargetScope.ANY_FRIENDLY, 5));

        strats.add(new Stratagem(
                "Cacophonous Assault",
                "1CP — This Infantry unit makes 3 extra attacks this round",
                1, Stratagem.EffectType.EXTRA_ATTACKS,
                Stratagem.TargetScope.INFANTRY_ONLY, 3));

        strats.add(new Stratagem(
                "Silver Tongue",
                "1CP — This unit gains a 4++ invulnerable save this round",
                1, Stratagem.EffectType.INVULN_BOOST,
                Stratagem.TargetScope.ANY_FRIENDLY, 4));

        strats.add(new Stratagem(
                "Warp-kissed Flesh",
                "2CP — Restore 5 wounds to this unit",
                2, Stratagem.EffectType.HEAL,
                Stratagem.TargetScope.ANY_FRIENDLY, 5));

        return new Detachment(
                "The Chosen of Slaanesh",
                "Hypersensory Assault",
                "Detachment passive: Sustained Hits 2 on all units (2 bonus hits per natural 6)",
                "sustained_hits", 2, false, strats);
    }

    /**
     * Creates the Emperor's Children "Children of Torment" detachment.
     *
     * <p><b>Passive:</b> Embrace the Pain — all units gain Feel No Pain 5+,
     * a full pip better than Kakophoni's 6+. The Children of Torment have
     * trained themselves to convert every wound into ecstasy, making them
     * extraordinarily difficult to put down.
     *
     * <p><b>Stratagems:</b>
     * <pre>
     * Pain is Pleasure       1CP  FEEL_NO_PAIN   4+  ANY_FRIENDLY
     * Revel in Agony         1CP  EXTRA_ATTACKS  2   ANY_FRIENDLY
     * Agonizing Overload     2CP  DEVASTATING    —   ANY_FRIENDLY
     * Ecstatic Rush          1CP  FIGHTS_FIRST   —   ANY_FRIENDLY
     * Silken Strikes         1CP  SUSTAINED_HITS 1   ANY_FRIENDLY
     * Dark Apotheosis        2CP  HEAL           6   ANY_FRIENDLY
     * </pre>
     *
     * @return Configured Emperor's Children Children of Torment Detachment
     */
    public static Detachment createEmperorsChildrenTormentDetachment() {
        ArrayList<Stratagem> strats = new ArrayList<>();

        strats.add(new Stratagem(
                "Pain is Pleasure",
                "1CP — This unit gains Feel No Pain 4+ this round (exceptional resilience)",
                1, Stratagem.EffectType.FEEL_NO_PAIN,
                Stratagem.TargetScope.ANY_FRIENDLY, 4));

        strats.add(new Stratagem(
                "Revel in Agony",
                "1CP — This unit makes 2 extra attacks this round",
                1, Stratagem.EffectType.EXTRA_ATTACKS,
                Stratagem.TargetScope.ANY_FRIENDLY, 2));

        strats.add(new Stratagem(
                "Agonizing Overload",
                "2CP — This unit gains Devastating Wounds this round",
                2, Stratagem.EffectType.DEVASTATING,
                Stratagem.TargetScope.ANY_FRIENDLY, 1));

        strats.add(new Stratagem(
                "Ecstatic Rush",
                "1CP — This unit fights first this round",
                1, Stratagem.EffectType.FIGHTS_FIRST,
                Stratagem.TargetScope.ANY_FRIENDLY, 0));

        strats.add(new Stratagem(
                "Silken Strikes",
                "1CP — This unit gains Sustained Hits 1 this round",
                1, Stratagem.EffectType.SUSTAINED_HITS,
                Stratagem.TargetScope.ANY_FRIENDLY, 1));

        strats.add(new Stratagem(
                "Dark Apotheosis",
                "2CP — Restore 6 wounds to this unit",
                2, Stratagem.EffectType.HEAL,
                Stratagem.TargetScope.ANY_FRIENDLY, 6));

        return new Detachment(
                "Children of Torment",
                "Embrace the Pain",
                "Detachment passive: Feel No Pain 5+ on all units",
                "feel_no_pain", 5, false, strats);
    }

    // =========================================================================
    // CHAOS KNIGHTS — additional detachments [2] and [3]
    // =========================================================================

    /**
     * Creates the Chaos Knights "Iconoclast Household" detachment.
     *
     * <p><b>Passive:</b> Iconoclast Oaths — all Vehicle and Knight units gain
     * Sustained Hits 1, representing the relentless, machine-driven weapon
     * systems of god-machines that never tire, never hesitate, and never miss.
     *
     * <p><b>Stratagems:</b>
     * <pre>
     * Death Grip             1CP  EXTRA_ATTACKS  3   VEHICLE_ONLY
     * Veil of Darkness       1CP  INVULN_BOOST   4   VEHICLE_ONLY
     * Daemonic Fury          2CP  DEVASTATING    —   VEHICLE_ONLY
     * Knight's Fury          1CP  FIGHTS_FIRST   —   VEHICLE_ONLY
     * Armour of Contempt     1CP  FEEL_NO_PAIN   5+  VEHICLE_ONLY
     * Field Repairs          2CP  HEAL           6   VEHICLE_ONLY
     * </pre>
     *
     * @return Configured Chaos Knights Iconoclast Household Detachment
     */
    public static Detachment createChaosKnightsIconoclastDetachment() {
        ArrayList<Stratagem> strats = new ArrayList<>();

        strats.add(new Stratagem(
                "Death Grip",
                "1CP — This Vehicle/Knight makes 3 extra attacks this round",
                1, Stratagem.EffectType.EXTRA_ATTACKS,
                Stratagem.TargetScope.VEHICLE_ONLY, 3));

        strats.add(new Stratagem(
                "Veil of Darkness",
                "1CP — This Vehicle/Knight gains a 4++ invulnerable save this round",
                1, Stratagem.EffectType.INVULN_BOOST,
                Stratagem.TargetScope.VEHICLE_ONLY, 4));

        strats.add(new Stratagem(
                "Daemonic Fury",
                "2CP — This Vehicle/Knight gains Devastating Wounds this round",
                2, Stratagem.EffectType.DEVASTATING,
                Stratagem.TargetScope.VEHICLE_ONLY, 1));

        strats.add(new Stratagem(
                "Knight's Fury",
                "1CP — This Vehicle/Knight fights first this round",
                1, Stratagem.EffectType.FIGHTS_FIRST,
                Stratagem.TargetScope.VEHICLE_ONLY, 0));

        strats.add(new Stratagem(
                "Armour of Contempt",
                "1CP — This Vehicle/Knight gains Feel No Pain 5+ this round",
                1, Stratagem.EffectType.FEEL_NO_PAIN,
                Stratagem.TargetScope.VEHICLE_ONLY, 5));

        strats.add(new Stratagem(
                "Field Repairs",
                "2CP — Restore 6 wounds to this Vehicle/Knight unit",
                2, Stratagem.EffectType.HEAL,
                Stratagem.TargetScope.VEHICLE_ONLY, 6));

        return new Detachment(
                "Iconoclast Household",
                "Iconoclast Oaths",
                "Detachment passive: Sustained Hits 1 on all Vehicle/Knight units",
                "sustained_hits", 1, true, strats);
    }

    /**
     * Creates the Chaos Knights "Infernal Court" detachment.
     *
     * <p><b>Passive:</b> Warp-Forged Armour — all Vehicle and Knight units gain
     * Feel No Pain 5+, representing the daemonic bargains and warp-fused plate
     * of the Infernal Court's god-machines. Wounds that would cripple a loyalist
     * Knight are shrugged off as minor inconveniences.
     *
     * <p><b>Stratagems:</b>
     * <pre>
     * Daemon Engine          1CP  SUSTAINED_HITS 2   VEHICLE_ONLY
     * Warp Rift              2CP  DEVASTATING    —   VEHICLE_ONLY
     * Unholy Vigour          1CP  HEAL           5   VEHICLE_ONLY
     * Hellforged Rampage     1CP  EXTRA_ATTACKS  4   VEHICLE_ONLY
     * Chaos Shield           1CP  INVULN_BOOST   3   VEHICLE_ONLY
     * Screaming Death        2CP  FIGHTS_FIRST   —   VEHICLE_ONLY
     * </pre>
     *
     * @return Configured Chaos Knights Infernal Court Detachment
     */
    public static Detachment createChaosKnightsInfernalCourtDetachment() {
        ArrayList<Stratagem> strats = new ArrayList<>();

        strats.add(new Stratagem(
                "Daemon Engine",
                "1CP — This Vehicle/Knight gains Sustained Hits 2 this round",
                1, Stratagem.EffectType.SUSTAINED_HITS,
                Stratagem.TargetScope.VEHICLE_ONLY, 2));

        strats.add(new Stratagem(
                "Warp Rift",
                "2CP — This Vehicle/Knight gains Devastating Wounds this round",
                2, Stratagem.EffectType.DEVASTATING,
                Stratagem.TargetScope.VEHICLE_ONLY, 1));

        strats.add(new Stratagem(
                "Unholy Vigour",
                "1CP — Restore 5 wounds to this Vehicle/Knight unit",
                1, Stratagem.EffectType.HEAL,
                Stratagem.TargetScope.VEHICLE_ONLY, 5));

        strats.add(new Stratagem(
                "Hellforged Rampage",
                "1CP — This Vehicle/Knight makes 4 extra attacks this round",
                1, Stratagem.EffectType.EXTRA_ATTACKS,
                Stratagem.TargetScope.VEHICLE_ONLY, 4));

        strats.add(new Stratagem(
                "Chaos Shield",
                "1CP — This Vehicle/Knight gains a 3++ invulnerable save this round",
                1, Stratagem.EffectType.INVULN_BOOST,
                Stratagem.TargetScope.VEHICLE_ONLY, 3));

        strats.add(new Stratagem(
                "Screaming Death",
                "2CP — This Vehicle/Knight fights first this round",
                2, Stratagem.EffectType.FIGHTS_FIRST,
                Stratagem.TargetScope.VEHICLE_ONLY, 0));

        return new Detachment(
                "Infernal Court",
                "Warp-Forged Armour",
                "Detachment passive: Feel No Pain 5+ on all Vehicle/Knight units",
                "feel_no_pain", 5, true, strats);
    }
}

/*
 * Change Log:
 *   2026-04-09  Shane Potts  Initial creation — Phase 4 Detachment/CP system;
 *                             private constructor + 9 static factory methods
 *                             (3 per faction); vehicleOnly flag controls whether
 *                             passive BattleTrait is applied to all units or only
 *                             Vehicle/Knight units; applyPassiveRule() injects
 *                             BattleTrait at combat start; resetForNewCombat()
 *                             resets all Stratagem used-flags between encounters
 *   2026-04-09  Shane Potts  Expanded to three detachments per faction; player
 *                             selects from a menu in Game.selectDetachment()
 *   2026-04-26  Shane Potts  Phase 4 complete - no further changes this phase
 */
