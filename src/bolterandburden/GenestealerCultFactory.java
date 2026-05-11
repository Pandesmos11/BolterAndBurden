/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Static factory class that constructs Genestealer Cults enemy
 *              warbands for each act of the campaign, scaled to competitive
 *              10th edition army sizes:
 *
 *                Act I  — "Vanguard of the Abyss"  (~1000 pts, 6 units)
 *                  A probing vanguard: the Patriarch leads a pack of
 *                  Purestrain Genestealers, Acolyte Hybrids with demo
 *                  charges, Neophyte Hybrid gunline, and Aberrant shock
 *                  troops. Fast, ambush-heavy, and immediately threatening.
 *
 *                Act II — "Host of the Abyss"  (~2000 pts, 11 units)
 *                  The infestation rises: a Broodlord joins the Patriarch,
 *                  a second Purestrain pack and Hybrid Metamorphs reinforce
 *                  the assault, Atalan Jackals provide flanking speed, and
 *                  an Achilles Ridgerunner delivers heavy fire support.
 *
 *                Act III — "The Great Rising"  (~3000 pts, 15 units)
 *                  Every cult asset commits: Magus and Primus support the
 *                  twin leaders, a second Aberrant pack and Clamavus stiffen
 *                  the line, a Goliath Rockgrinder crashes through the centre,
 *                  and the full Neophyte blob anchors the rear.
 *
 *              All units are built from Warhammer 40,000 10th edition datasheets.
 *              Special rules are mapped to BattleTrait triggers:
 *                fights_first      — Purestrain Genestealers, Patriarch, Broodlord
 *                lethal_hits       — Purestrain Genestealers (Rending Claws)
 *                invuln_save       — Patriarch (4++), Broodlord (4++),
 *                                    Purestrain Genestealers (4++)
 *                devastating_wounds— Acolyte Hybrids, Atalan Jackals (Demo Charges)
 *                feel_no_pain      — Aberrants (6+ resilience)
 *                sustained_hits    — Achilles Ridgerunner (Heavy Mining Laser)
 *
 *              Private builder helpers are shared across all three acts so
 *              repeated units stay statistically consistent.
 *
 * Change Log:
 *   2026-04-09  Shane Potts  Initial creation — Genestealer Cults enemy faction;
 *                             three public factory methods (createActOneWarband,
 *                             createActTwoWarband, createActThreeWarband) scaled
 *                             to ~1000 / ~2000 / ~3000 points with 6/11/15 units;
 *                             private builder helpers (buildPatriarch, buildBroodlord,
 *                             buildPurestrains, etc.) shared across acts for
 *                             statistical consistency; all special rules mapped to
 *                             BattleTrait triggers: fights_first, lethal_hits,
 *                             invuln_save, devastating_wounds, feel_no_pain,
 *                             sustained_hits; stats sourced from 10th ed datasheets
 *   2026-04-26  Shane Potts  Phase 4 complete - no further changes this phase
 */

package bolterandburden;

public class GenestealerCultFactory {

    private static final String FACTION = "Genestealer Cults";

    private GenestealerCultFactory() {}

    // =========================================================================
    // ACT I — "Vanguard of the Abyss"  ~1000 pts  — 6 units
    // =========================================================================

    /**
     * Creates the Act I Genestealer Cults warband — a probing vanguard
     * approximately equivalent to a 1000-point competitive patrol detachment.
     *
     * <pre>
     * Patriarch Khyrgoss          Psyker   W8   T5  Sv4+  M8   A6  +2
     * Purestrain Genestealers ×10 Infantry W10  T4  Sv5+  M8   A3  +1
     * Acolyte Hybrids ×10         Infantry W10  T3  Sv5+  M6   A2  +1
     * Neophyte Hybrids ×10        Infantry W10  T3  Sv5+  M6   A1  +1
     * Aberrants ×5                Infantry W15  T5  Sv5+  M6   A4  +1
     * Atalan Jackals ×5           Infantry W10  T4  Sv4+  M12  A2  +0
     * </pre>
     */
    public static Warband createActOneWarband() {
        Faction faction = new Faction(FACTION, false);
        faction.addUnitType("Infantry");
        faction.addUnitType("Psyker");

        Warband warband = new Warband("Vanguard of the Abyss", faction);

        warband.addUnit(buildPatriarch());
        warband.addUnit(buildPurestrains("Purestrain Pack Alpha"));
        warband.addUnit(buildAcolytes("Acolyte Brood"));
        warband.addUnit(buildNeophytes("Neophyte Gunline", 10));
        warband.addUnit(buildAberrants("Aberrant Shock Troop"));
        warband.addUnit(buildJackals("Atalan Outriders"));

        return warband;
    }

    // =========================================================================
    // ACT II — "Host of the Abyss"  ~2000 pts  — 11 units
    // =========================================================================

    /**
     * Creates the Act II Genestealer Cults warband — a full strike force
     * approximately equivalent to a 2000-point competitive battalion.
     *
     * <pre>
     * Patriarch Khyrgoss          Psyker   W8   T5  Sv4+  M8   A6  +2
     * Broodlord Vex'rrath         Psyker   W8   T5  Sv4+  M8   A7  +2
     * Purestrain Pack Alpha ×10   Infantry W10  T4  Sv5+  M8   A3  +1
     * Purestrain Pack Beta ×10    Infantry W10  T4  Sv5+  M8   A3  +1
     * Acolyte Brood ×10           Infantry W10  T3  Sv5+  M6   A2  +1
     * Hybrid Metamorphs ×10       Infantry W10  T3  Sv5+  M6   A3  +1
     * Aberrant Shock Troop ×5     Infantry W15  T5  Sv5+  M6   A4  +1
     * Neophyte Gunline ×10        Infantry W10  T3  Sv5+  M6   A1  +1
     * Atalan Outriders ×5         Infantry W10  T4  Sv4+  M12  A2  +0
     * Achilles Ridgerunner        Vehicle  W6   T6  Sv4+  M14  A3  +1
     * Goliath Truck               Vehicle  W11  T7  Sv4+  M12  A2  +0
     * </pre>
     */
    public static Warband createActTwoWarband() {
        Faction faction = new Faction(FACTION, false);
        faction.addUnitType("Infantry");
        faction.addUnitType("Psyker");
        faction.addUnitType("Vehicle");

        Warband warband = new Warband("Host of the Abyss", faction);

        warband.addUnit(buildPatriarch());
        warband.addUnit(buildBroodlord());
        warband.addUnit(buildPurestrains("Purestrain Pack Alpha"));
        warband.addUnit(buildPurestrains("Purestrain Pack Beta"));
        warband.addUnit(buildAcolytes("Acolyte Brood"));
        warband.addUnit(buildMetamorphs("Hybrid Metamorphs"));
        warband.addUnit(buildAberrants("Aberrant Shock Troop"));
        warband.addUnit(buildNeophytes("Neophyte Gunline", 10));
        warband.addUnit(buildJackals("Atalan Outriders"));
        warband.addUnit(buildRidgerunner("Achilles Ridgerunner"));
        warband.addUnit(buildGoliathTruck("Goliath Truck"));

        return warband;
    }

    // =========================================================================
    // ACT III — "The Great Rising"  ~3000 pts  — 15 units
    // =========================================================================

    /**
     * Creates the Act III Genestealer Cults warband — the full cult committed
     * to the Great Rising, approximately equivalent to a 3000-point army.
     *
     * <pre>
     * Patriarch Khyrgoss          Psyker   W8   T5  Sv4+  M8   A6  +2
     * Broodlord Vex'rrath         Psyker   W8   T5  Sv4+  M8   A7  +2
     * Magus Sera Keel             Psyker   W4   T3  Sv4+  M6   A3  +0
     * Primus Torvan               Infantry W5   T3  Sv4+  M6   A5  +1
     * Clamavus Drevon             Infantry W4   T3  Sv4+  M6   A4  +0
     * Purestrain Pack Alpha ×10   Infantry W10  T4  Sv5+  M8   A3  +1
     * Purestrain Pack Beta ×10    Infantry W10  T4  Sv5+  M8   A3  +1
     * Acolyte Brood ×10           Infantry W10  T3  Sv5+  M6   A2  +1
     * Hybrid Metamorphs ×10       Infantry W10  T3  Sv5+  M6   A3  +1
     * Aberrant Shock Troop ×5     Infantry W15  T5  Sv5+  M6   A4  +1
     * Aberrant Vanguard ×5        Infantry W15  T5  Sv5+  M6   A4  +1
     * Neophyte Horde ×20          Infantry W20  T3  Sv5+  M6   A1  +2
     * Atalan Outriders ×5         Infantry W10  T4  Sv4+  M12  A2  +0
     * Achilles Ridgerunner        Vehicle  W6   T6  Sv4+  M14  A3  +1
     * Goliath Rockgrinder         Vehicle  W11  T7  Sv4+  M9   A6  +2
     * </pre>
     */
    public static Warband createActThreeWarband() {
        Faction faction = new Faction(FACTION, false);
        faction.addUnitType("Infantry");
        faction.addUnitType("Psyker");
        faction.addUnitType("Vehicle");

        Warband warband = new Warband("The Great Rising", faction);

        warband.addUnit(buildPatriarch());
        warband.addUnit(buildBroodlord());
        warband.addUnit(buildMagus());
        warband.addUnit(buildPrimus());
        warband.addUnit(buildClamavus());
        warband.addUnit(buildPurestrains("Purestrain Pack Alpha"));
        warband.addUnit(buildPurestrains("Purestrain Pack Beta"));
        warband.addUnit(buildAcolytes("Acolyte Brood"));
        warband.addUnit(buildMetamorphs("Hybrid Metamorphs"));
        warband.addUnit(buildAberrants("Aberrant Shock Troop"));
        warband.addUnit(buildAberrants("Aberrant Vanguard"));
        warband.addUnit(buildNeophytes("Neophyte Horde", 20));
        warband.addUnit(buildJackals("Atalan Outriders"));
        warband.addUnit(buildRidgerunner("Achilles Ridgerunner"));
        warband.addUnit(buildRockgrinder("Goliath Rockgrinder"));

        return warband;
    }

    // =========================================================================
    // Unit Builders — shared helpers used across all three acts
    // =========================================================================

    /**
     * Patriarch Khyrgoss — the cult's monstrous psychic mastermind.
     * 10th ed: M8 T5 Sv4+ W8 A6. 4+ invulnerable save (Shadow in the Warp).
     * Fights First. Psyker (Might from Beyond, Brood Mind Link).
     */
    private static Psyker buildPatriarch() {
        Wargear gear = new Wargear();
        gear.addEquipment("Patriarch's Claws");
        gear.addEquipment("Lash of Submission");
        gear.setAttackModifier(2);

        Psyker patriarch = new Psyker("Patriarch Khyrgoss", FACTION,
                new StatBlock(8, 5, 4, 8, 6), gear, "Broodmind", 3);

        // PSYCHIC: Might from Beyond — swell of psychic force through the
        //          brood link; nearby Cultists strike with inhuman strength.
        patriarch.addAbility(new PsychicPower("Might from Beyond",
                "A swell of psychic force flows through the brood link. Allies strike with inhuman strength, overwhelming the foe.", 3));

        // SORCERY: Brood Mind Link — daemonic echo of the Hive Mind;
        //          inflicts corruption alongside psychic damage.
        SorceryRitual broodLink = new SorceryRitual("Brood Mind Link",
                "The Patriarch taps a daemonic echo of the Great Devourer. A howl of alien hunger rips through nearby minds.", 2, 2);
        patriarch.addAbility(broodLink);

        // RULE: Shadow in the Warp — the alien psychic gestalt confers
        //       a 4+ invulnerable save against all attacks.
        patriarch.addAbility(new BattleTrait("Shadow in the Warp",
                "The alien gestalt of the brood mind wraps the Patriarch in a psychic shroud — 4+ invulnerable save against all attacks.",
                "toughness", 4, "invuln_save"));

        // RULE: Ravenous Appetite — the Patriarch tears into enemies with
        //       supernatural ferocity; re-roll melee hits and wounds.
        patriarch.addAbility(new BattleTrait("Ravenous Appetite",
                "The Patriarch tears into foes with alien hunger, re-rolling all failed melee hit and wound rolls.",
                "attacks", 1));

        // RULE: Patriarch Fights First — monstrous speed and psychic
        //       anticipation allow the Patriarch to always strike before enemies.
        patriarch.addAbility(new BattleTrait("Alien Speed",
                "The Patriarch's alien reflexes are beyond human comprehension. It always acts before non-Fights-First units.",
                "speed", 2, "fights_first"));

        return patriarch;
    }

    /**
     * Broodlord Vex'rrath — a monstrous melee killer that leads the stealer packs.
     * 10th ed: M8 T5 Sv4+ W8 A7. 4+ invulnerable save. Psyker (Telepathic Summons).
     * Fights First (Ferocious Charge). Lethal Hits on natural 6s.
     */
    private static Psyker buildBroodlord() {
        Wargear gear = new Wargear();
        gear.addEquipment("Broodlord's Claws");
        gear.setAttackModifier(2);

        Psyker broodlord = new Psyker("Broodlord Vex'rrath", FACTION,
                new StatBlock(8, 5, 4, 8, 7), gear, "Broodmind", 1);

        // PSYCHIC: Telepathic Summons — the Broodlord calls stealers from
        //          the shadows; represents psychic leadership pressure.
        broodlord.addAbility(new PsychicPower("Telepathic Summons",
                "The Broodlord's call echoes through the brood link, driving stealers forward from the darkness with single-minded hunger.", 2));

        // RULE: 4+ Invulnerable Save — alien resilience and psychic warding.
        broodlord.addAbility(new BattleTrait("Alien Resilience",
                "The Broodlord's alien biology and psychic shroud grant a 4+ invulnerable save against all attacks.",
                "toughness", 4, "invuln_save"));

        // RULE: Fights First — the Broodlord's preternatural speed means it
        //       always strikes before non-Fights-First enemies.
        broodlord.addAbility(new BattleTrait("Ferocious Charge",
                "The Broodlord crashes into enemies with terrifying speed, always striking before non-Fights-First opponents.",
                "speed", 2, "fights_first"));

        // RULE: Lethal Hits — every unmodified 6 to hit with the Broodlord's
        //       claws automatically wounds, bypassing the wound roll entirely.
        broodlord.addAbility(new BattleTrait("Murderous Claws",
                "Each unmodified 6 to hit with the Broodlord's claws automatically wounds — no wound roll required.",
                "attacks", 1, "lethal_hits"));

        return broodlord;
    }

    /**
     * Magus Sera Keel — the cult's psychic channeller and tactical coordinator.
     * 10th ed: M6 T3 Sv4+ W4 A3. Psyker (Hypnotic Gaze, Psionic Blast).
     */
    private static Psyker buildMagus() {
        Wargear gear = new Wargear();
        gear.addEquipment("Force Stave");
        gear.addEquipment("Autopistol");

        Psyker magus = new Psyker("Magus Sera Keel", FACTION,
                new StatBlock(4, 3, 4, 6, 3), gear, "Broodmind", 2);

        // PSYCHIC: Hypnotic Gaze — the Magus locks eyes with a target unit
        //          commander, wresting control and forcing catastrophic errors.
        PsychicPower hypnoticGaze = new PsychicPower("Hypnotic Gaze",
                "The Magus fixes its alien eyes on an enemy leader. The victim's mind shatters, triggering reckless, catastrophic decisions.", 2);

        // PSYCHIC: Psionic Blast — raw psychic force discharged as a bolt
        //          of warp energy directly into the target's nervous system.
        PsychicPower psionicBlast = new PsychicPower("Psionic Blast",
                "The Magus discharges raw psychic force in a bolt of warp energy that overloads the target's nervous system entirely.", 1);

        hypnoticGaze.addChainTrigger(psionicBlast);
        magus.addAbility(hypnoticGaze);
        magus.addAbility(psionicBlast);

        // RULE: Broodmind Coordinator — while the Magus lives, nearby cult
        //       units re-roll failed hit rolls of 1 (aura; represented as attacks+1).
        magus.addAbility(new BattleTrait("Broodmind Coordinator",
                "The Magus projects tactical commands through the brood link. Nearby cult units re-roll hit rolls of 1.",
                "attacks", 1));

        return magus;
    }

    /**
     * Primus Torvan — the cult's close-combat duelist and squad leader.
     * 10th ed: M6 T3 Sv4+ W5 A5. Precision (targets Characters in units).
     * Leader ability: +1 to wound rolls while leading a unit.
     */
    private static Infantry buildPrimus() {
        Wargear gear = new Wargear();
        gear.addEquipment("Needle Pistol");
        gear.addEquipment("Bonesword");
        gear.addEquipment("Toxin Injector Claw");
        gear.setAttackModifier(1);

        Infantry primus = new Infantry("Primus Torvan", FACTION,
                new StatBlock(5, 3, 4, 6, 5), gear, "Primus", true);

        // RULE: Precision — Bonesword attacks can be allocated to CHARACTER
        //       models embedded in enemy units.
        primus.addAbility(new BattleTrait("Precision",
                "The Primus's Bonesword has Precision: wounds can be allocated to CHARACTER models in enemy units.",
                "attacks", 1));

        // RULE: Meticulous Planner (Leader) — while leading a unit, that unit
        //       adds +1 to all wound rolls.
        primus.addAbility(new BattleTrait("Meticulous Planner",
                "Leader: while Primus Torvan leads a unit, all attacks in that unit add +1 to their wound rolls.",
                "attacks", 1));

        // RULE: Devoted Fanatic — the Primus drives cult members to ecstatic
        //       self-sacrifice; once per battle he may absorb a wound aimed at
        //       a nearby unit (represented as toughness +1).
        primus.addAbility(new BattleTrait("Devoted Fanatic",
                "The Primus hurls himself between a fellow cultist and a killing blow, absorbing the wound with fanatic devotion.",
                "toughness", 1));

        return primus;
    }

    /**
     * Clamavus Drevon — the cult's vox-screamer and morale anchor.
     * 10th ed: M6 T3 Sv4+ W4 A4. Aura: nearby cultists ignore morale.
     */
    private static Infantry buildClamavus() {
        Wargear gear = new Wargear();
        gear.addEquipment("Rending Claw");
        gear.addEquipment("Cult Vox-Screamer");

        Infantry clamavus = new Infantry("Clamavus Drevon", FACTION,
                new StatBlock(4, 3, 4, 6, 4), gear, "Clamavus", true);

        // RULE: Insidious Voice — the Clamavus's vox-screamer broadcasts
        //       mind-fracturing transmissions; enemy units subtract 1 from
        //       Leadership and Battle-shock tests within 6".
        clamavus.addAbility(new BattleTrait("Insidious Voice",
                "The Clamavus broadcasts vox-transmissions that fracture enemy minds. Nearby foes subtract 1 from all Leadership and Battle-shock tests.",
                "toughness", 1));

        // RULE: Cult Demagogue — friendly Genestealer Cults units within 6"
        //       are immune to morale and may re-roll failed Battleshock tests.
        clamavus.addAbility(new BattleTrait("Cult Demagogue",
                "The Clamavus's frenzied oratory drives nearby cultists to fearless devotion — they cannot be forced to flee.",
                "speed", 1));

        return clamavus;
    }

    /**
     * Purestrain Genestealers ×10 — the cult's apex ambush predators.
     * 10th ed per model: M8 T4 Sv5+ (4++) W1 A3. Fights First. Lethal Hits.
     * Squad W = 10×1 = 10. +1 attack modifier for Rending Claw volume.
     */
    private static Infantry buildPurestrains(String name) {
        Wargear gear = new Wargear();
        gear.addEquipment("Rending Claws");
        gear.setAttackModifier(1);

        Infantry purestrains = new Infantry(name, FACTION,
                new StatBlock(10, 4, 5, 8, 3), gear, "Purestrain Genestealers", false);

        // RULE: Fights First — Purestrain Genestealers always act before
        //       non-Fights-First units in the Fight phase. This is their
        //       defining rule and makes them lethal ambush predators.
        purestrains.addAbility(new BattleTrait("Fights First",
                "Purestrain Genestealers always act before non-Fights-First units — they fall upon the enemy before any response is possible.",
                "speed", 2, "fights_first"));

        // RULE: Lethal Hits — every unmodified 6 to hit with Rending Claws
        //       automatically wounds the target with no wound roll required.
        purestrains.addAbility(new BattleTrait("Rending Claws",
                "Each unmodified 6 to hit automatically wounds — the razor-edged claws find gaps in any armour.",
                "attacks", 1, "lethal_hits"));

        // RULE: 4+ Invulnerable Save — the Genestealers' alien agility and
        //       chitinous armour deflect blows that would kill lesser creatures.
        purestrains.addAbility(new BattleTrait("Chitinous Resilience",
                "The Genestealers' alien agility and layered chitin grant a 4+ invulnerable save against all attacks.",
                "toughness", 4, "invuln_save"));

        // RULE: Cult Ambush — Purestrain Genestealers emerge from underground
        //       tunnels or hidden lairs, arriving mid-battle from any direction.
        purestrains.addAbility(new BattleTrait("Cult Ambush",
                "The Genestealers burst from concealed tunnels mid-battle, appearing exactly where the enemy least expects them.",
                "speed", 3));

        return purestrains;
    }

    /**
     * Acolyte Hybrids ×10 — close-assault cultists armed with demo charges.
     * 10th ed per model: M6 T3 Sv5+ W1 A2. Cult Ambush. Demo Charges (Devastating Wounds).
     * Squad W = 10×1 = 10. +1 attack modifier for pistol/claw volume.
     */
    private static Infantry buildAcolytes(String name) {
        Wargear gear = new Wargear();
        gear.addEquipment("Autopistols");
        gear.addEquipment("Rending Claws");
        gear.addEquipment("Demolition Charges");
        gear.setAttackModifier(1);

        Infantry acolytes = new Infantry(name, FACTION,
                new StatBlock(10, 3, 5, 6, 2), gear, "Acolyte Hybrids", false);

        // RULE: Demolition Charges — Devastating Wounds: unmodified 6s to
        //       wound bypass armour saves, becoming mortal wounds. The demo
        //       charge detonates directly against armour, melting through it.
        acolytes.addAbility(new BattleTrait("Demolition Charges",
                "Demo charges detonate directly against armour plating. Unmodified 6s to wound bypass saves — pure mortal wound damage.",
                "attacks", 2, "devastating_wounds"));

        // RULE: Cult Ambush — Acolyte Hybrids deploy from hidden underground
        //       passages, flanking the enemy and detonating charges at close range.
        acolytes.addAbility(new BattleTrait("Cult Ambush",
                "Acolyte Hybrids burst from underground tunnels mid-engagement, closing to demo-charge range before the enemy can react.",
                "speed", 3));

        // RULE: Ferocious Assault — the Acolytes hurl themselves at the
        //       enemy in a wave of bodies, overwhelming by sheer numbers.
        acolytes.addAbility(new BattleTrait("Ferocious Assault",
                "The Acolytes charge in a frothing mob, attacking with every weapon — pistols, claws, and explosive charges all at once.",
                "attacks", 1));

        return acolytes;
    }

    /**
     * Neophyte Hybrids ×N — the cult's most numerous ranged cultists.
     * 10th ed per model: M6 T3 Sv5+ W1 A1. Autoguns. Mining Laser support.
     * Squad W = N×1. +1 attack modifier per 10 models (ranged volume).
     *
     * @param name   Squad display name
     * @param models Number of models in the squad (10 or 20)
     */
    private static Infantry buildNeophytes(String name, int models) {
        Wargear gear = new Wargear();
        gear.addEquipment("Autoguns");
        gear.addEquipment("Mining Lasers");
        gear.setAttackModifier(models / 10); // +1 per 10 models

        Infantry neophytes = new Infantry(name, FACTION,
                new StatBlock(models, 3, 5, 6, 1), gear, "Neophyte Hybrids", false);

        // RULE: Mining Laser — the squad's Mining Laser teams provide
        //       long-range anti-armour fire (Sustained Hits 1 on a 6).
        neophytes.addAbility(new BattleTrait("Mining Laser Volley",
                "Neophyte Mining Laser teams add firepower — unmodified 6s to hit score an extra hit against armoured targets.",
                "attacks", 1, "sustained_hits"));

        // RULE: Cult Ambush — some Neophytes emerge from hidden positions
        //       to reinforce with flanking fire.
        neophytes.addAbility(new BattleTrait("Cult Ambush",
                "Neophyte flankers emerge from hidden positions mid-battle, adding their autoguns to the crossfire.",
                "speed", 1));

        return neophytes;
    }

    /**
     * Hybrid Metamorphs ×10 — elite shock assault troops with Metamorph Talons.
     * 10th ed per model: M6 T3 Sv5+ W1 A3. Cult Ambush. High attack volume.
     * Squad W = 10×1 = 10. +1 attack modifier for Whip/Talon combination.
     */
    private static Infantry buildMetamorphs(String name) {
        Wargear gear = new Wargear();
        gear.addEquipment("Metamorph Talons");
        gear.addEquipment("Metamorph Whips");
        gear.setAttackModifier(1);

        Infantry metamorphs = new Infantry(name, FACTION,
                new StatBlock(10, 3, 5, 6, 3), gear, "Hybrid Metamorphs", false);

        // RULE: Cult Ambush — Metamorphs burst from concealment mid-battle,
        //       their hideous forms revealed only when it is too late to react.
        metamorphs.addAbility(new BattleTrait("Cult Ambush",
                "Metamorphs reveal their hideous true forms at the last possible moment, striking from concealment before the enemy recovers.",
                "speed", 3));

        // RULE: Metamorph Whips — models with whips attack before the enemy
        //       in melee, harassing and disrupting defensive formations.
        metamorphs.addAbility(new BattleTrait("Metamorph Whips",
                "The Metamorph Whips lash out before any counter-attack, disrupting the enemy's defensive stance before the talons land.",
                "attacks", 1));

        // RULE: Freakish Mutations — the Metamorphs' unstable genetics grant
        //       them resilience to pain that ordinary humans cannot match.
        metamorphs.addAbility(new BattleTrait("Freakish Mutations",
                "Unstable genetics grant the Metamorphs an alien resilience. Wounds that would stop humans barely slow them.",
                "toughness", 1));

        return metamorphs;
    }

    /**
     * Aberrants ×5 — massive brute-mutants armed with industrial hammers.
     * 10th ed per model: M6 T5 Sv5+ W3 A4. Feel No Pain 6+. High strength melee.
     * Squad W = 5×3 = 15. +1 attack modifier for Pickaxe additional attacks.
     */
    private static Infantry buildAberrants(String name) {
        Wargear gear = new Wargear();
        gear.addEquipment("Power Hammers");
        gear.addEquipment("Pickaxes");
        gear.setAttackModifier(1);

        Infantry aberrants = new Infantry(name, FACTION,
                new StatBlock(15, 5, 5, 6, 4), gear, "Aberrants", false);

        // RULE: Might is Right — Aberrant power hammers ignore armour
        //       modifiers; represented as Sustained Hits on 6s.
        aberrants.addAbility(new BattleTrait("Might is Right",
                "The Aberrants' power hammers smash through armour with brute force. Unmodified 6s to hit score an extra crushing blow.",
                "attacks", 1, "sustained_hits"));

        // RULE: Neophyte Resilience (6+) — the Aberrants' massive bodies
        //       absorb damage that would destroy ordinary cultists.
        aberrants.addAbility(new BattleTrait("Monstrous Resilience",
                "The Aberrants' oversized bodies absorb wounds that would kill anyone else — roll a D6 for each wound; on a 6, ignore it.",
                "toughness", 6, "feel_no_pain"));

        // RULE: Instinctive Aggression — Aberrants charge toward the nearest
        //       enemy regardless of tactical considerations, hammer raised.
        aberrants.addAbility(new BattleTrait("Instinctive Aggression",
                "Aberrants charge toward the nearest enemy with single-minded fury. No order or threat can redirect their hammer blows.",
                "speed", 1));

        return aberrants;
    }

    /**
     * Atalan Jackals ×5 — fast-moving bike raiders armed with demo charges.
     * 10th ed per model: M12 T4 Sv4+ W2 A2. Scout. Demolition Charges (Dev Wounds).
     * Squad W = 5×2 = 10. Attack modifier 0 (pistol/demo only).
     */
    private static Infantry buildJackals(String name) {
        Wargear gear = new Wargear();
        gear.addEquipment("Autopistols");
        gear.addEquipment("Demolition Charges");
        gear.addEquipment("Shotguns");

        Infantry jackals = new Infantry(name, FACTION,
                new StatBlock(10, 4, 4, 12, 2), gear, "Atalan Jackals", false);

        // RULE: Demolition Charges — like Acolyte Hybrids, natural 6s to
        //       wound are mortal wounds that bypass armour saves.
        jackals.addAbility(new BattleTrait("Demolition Charges",
                "The Jackals drop live charges as they ride past. Natural 6s to wound become mortal wounds, bypassing armour entirely.",
                "attacks", 1, "devastating_wounds"));

        // RULE: Scout 9" — Atalan Jackals can move 9" before the first
        //       battle round begins, seizing forward objectives.
        jackals.addAbility(new BattleTrait("Scout 9\"",
                "Before the first battle round, the Jackals roar forward 9\" on their bikes, claiming position before any enemy can react.",
                "speed", 2));

        // RULE: Outflanking — the Jackals circle the enemy formation,
        //       threatening rear and flank positions.
        jackals.addAbility(new BattleTrait("Outflanking",
                "The Jackals circle the enemy at speed, threatening flanks and forcing a response that opens gaps for the cult.",
                "speed", 1));

        return jackals;
    }

    /**
     * Achilles Ridgerunner — a fast, lightly armoured vehicle with a heavy
     * mining laser for long-range anti-armour fire.
     * 10th ed: M14 T6 Sv4+ W6 A3. Sustained Hits 1 (Mining Laser).
     *
     * @param name Display name for the vehicle
     */
    private static Vehicle buildRidgerunner(String name) {
        Wargear gear = new Wargear();
        gear.addEquipment("Heavy Mining Laser");
        gear.addEquipment("Heavy Stubber");
        gear.setAttackModifier(1);

        Vehicle ridgerunner = new Vehicle(name, FACTION,
                new StatBlock(6, 6, 4, 14, 3), gear, "Achilles Ridgerunner", 6);

        // RULE: Sustained Hits 1 — Heavy Mining Laser: each unmodified 6 to
        //       hit generates 1 additional hit, drilling through armour plating.
        ridgerunner.addAbility(new BattleTrait("Heavy Mining Laser",
                "The Mining Laser bores through armour plating. Each unmodified 6 to hit scores 1 extra penetrating hit.",
                "attacks", 1, "sustained_hits"));

        // RULE: Flare Launchers — at the end of any phase in which it was
        //       targeted, the Ridgerunner can fire a flare to cause -1 to hit.
        ridgerunner.addAbility(new BattleTrait("Flare Launchers",
                "The Ridgerunner fires blinding flares when targeted, making it harder to track and hit in the next volley.",
                "toughness", 1));

        return ridgerunner;
    }

    /**
     * Goliath Truck — a rugged transport vehicle used to rush cult troops
     * into assault range. Armed with twin autocannons.
     * 10th ed: M12 T7 Sv4+ W11 A2. Transport. Heavy fire support.
     *
     * @param name Display name for the vehicle
     */
    private static Vehicle buildGoliathTruck(String name) {
        Wargear gear = new Wargear();
        gear.addEquipment("Twin Autocannon");
        gear.addEquipment("Demolition Charges");

        Vehicle truck = new Vehicle(name, FACTION,
                new StatBlock(11, 7, 4, 12, 2), gear, "Goliath Truck", 11);

        // RULE: Cult Ambush — the Goliath Truck can deploy from a hidden
        //       underground tunnel entrance, arriving mid-battle.
        truck.addAbility(new BattleTrait("Cult Ambush",
                "The Goliath Truck erupts from an underground tunnel ramp, arriving exactly where the enemy's line is thinnest.",
                "speed", 2));

        // RULE: Ram — the Truck's reinforced ram can strike in the Fight
        //       phase like a weapon (represented as attacks+1).
        truck.addAbility(new BattleTrait("Reinforced Ram",
                "The Goliath Truck's reinforced ram strikes anything that gets within reach in the Fight phase.",
                "attacks", 1));

        return truck;
    }

    /**
     * Goliath Rockgrinder — a heavily armed siege vehicle that grinds through
     * anything in its path. Act III only.
     * 10th ed: M9 T7 Sv4+ W11 A6. Drilldozer Blade. Devastating Wounds on charge.
     *
     * @param name Display name for the vehicle
     */
    private static Vehicle buildRockgrinder(String name) {
        Wargear gear = new Wargear();
        gear.addEquipment("Drilldozer Blade");
        gear.addEquipment("Heavy Seismic Cannon");
        gear.setAttackModifier(2);

        Vehicle rockgrinder = new Vehicle(name, FACTION,
                new StatBlock(11, 7, 4, 9, 6), gear, "Goliath Rockgrinder", 11);

        // RULE: Drilldozer Blade — natural 6s to wound with the blade
        //       punch straight through armour (Devastating Wounds).
        rockgrinder.addAbility(new BattleTrait("Drilldozer Blade",
                "The rotating drilldozer blade bores through armour as easily as rock. Natural 6s to wound bypass saves entirely.",
                "attacks", 2, "devastating_wounds"));

        // RULE: Mobile Fortress — the Rockgrinder's reinforced hull grants
        //       it additional resilience on the charge.
        rockgrinder.addAbility(new BattleTrait("Mobile Fortress",
                "The Rockgrinder is a rolling fortress of armoured plate and grinding machinery — incredibly difficult to stop.",
                "toughness", 1));

        // RULE: Seismic Cannon — the Heavy Seismic Cannon fires
        //       concussive blasts that crack armour plating (Sustained Hits 1).
        rockgrinder.addAbility(new BattleTrait("Seismic Cannon Barrage",
                "The Heavy Seismic Cannon fires concussive blasts that crack armour and shatter morale. Sustained Hits 1 on 6s.",
                "attacks", 1, "sustained_hits"));

        return rockgrinder;
    }
}

/*
 * Change Log:
 *   2026-04-09  Shane Potts  Initial creation — Genestealer Cults enemy faction;
 *                             three public factory methods (createActOneWarband,
 *                             createActTwoWarband, createActThreeWarband) scaled
 *                             to ~1000 / ~2000 / ~3000 points with 6/11/15 units;
 *                             private builder helpers (buildPatriarch, buildBroodlord,
 *                             buildPurestrains, etc.) shared across acts for
 *                             statistical consistency; all special rules mapped to
 *                             BattleTrait triggers: fights_first, lethal_hits,
 *                             invuln_save, devastating_wounds, feel_no_pain,
 *                             sustained_hits; stats sourced from 10th ed datasheets
 *   2026-04-26  Shane Potts  Phase 4 complete - no further changes this phase
 */
