/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Static factory class that constructs fully initialized Warband
 *              objects for each of the three main factions. Unit stats are
 *              derived from Warhammer 40,000 10th edition datasheets. Each
 *              unit carries abilities that faithfully represent their actual
 *              datasheet special rules, mapped to the three Ability subtypes:
 *                BattleTrait  — passive/activated rules (Fights First, Feel No
 *                               Pain, Sustained Hits, invulnerable saves, etc.)
 *                PsychicPower — damage-dealing psychic abilities (Smite, etc.)
 *                SorceryRitual— corruption-fuelled Chaos powers
 *              Squad units receive a combined wound pool (models × W/model).
 *
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 3; three static factory
 *                             methods (createDarkAngelsWarband, createEmperorsChildrenWarband,
 *                             createChaosKnightsWarband); each builds a named Warband
 *                             with all units pre-populated with stats and abilities
 *   2026-04-08  Shane Potts  Expanded to full 10th ed datasheets with named
 *                             characters; fleshed out abilities with accurate
 *                             special rules from each unit's datasheet; all
 *                             special rules mapped to BattleTrait trigger strings
 *                             consumed by CombatEngine attack resolution pipeline
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes this phase
 */

package bolterandburden;

public class FactionFactory {

    private FactionFactory() {}

    // =========================================================================
    // DARK ANGELS  "The Unforgiven"  — 12 units
    // =========================================================================

    /**
     * Creates the Dark Angels warband "The Unforgiven".
     *
     * <pre>
     * Lion El'Jonson         Infantry  W8   T6  Sv2+  M6   A6  +2
     * Azrael                 Infantry  W5   T4  Sv2+  M6   A7  +1
     * Hellblasters ×10       Infantry  W20  T4  Sv3+  M6   A4  +3
     * Chaplain Mordechai     Infantry  W4   T5  Sv2+  M5   A4  +1
     * DW Knights "Iron Shroud" ×5      Infantry  W15  T5  Sv2+  M5   A4  +1
     * Captain Azmael         Infantry  W6   T5  Sv2+  M5   A5  +1
     * DW Knights "Wrath Unbound" ×5    Infantry  W15  T5  Sv2+  M5   A4  +1
     * Librarian Seraphael    Psyker    W4   T4  Sv2+  M5   A4   0
     * DW Knights "Dark Harbingers" ×5  Infantry  W15  T5  Sv2+  M5   A4  +1
     * Judiciar Vael          Infantry  W4   T4  Sv3+  M6   A4  +1
     * Inner Circle Companions ×6       Infantry  W18  T4  Sv3+  M6   A4  +1
     * Scouts ×5              Infantry  W10  T4  Sv4+  M6   A2   0
     * </pre>
     */
    public static Warband createDarkAngelsWarband() {
        Faction faction = new Faction("Dark Angels", false);
        faction.addUnitType("Infantry");
        faction.addUnitType("Psyker");

        Warband warband = new Warband("The Unforgiven", faction);

        // ── Lion El'Jonson ────────────────────────────────────────────────────
        // 10th ed: M6  T6  Sv2+  W8
        // The Lion's Sword 6A S12 AP-3 D3  |  Fealty 4A S8 AP-1 D2
        Wargear lionGear = new Wargear();
        lionGear.addEquipment("The Lion's Sword");
        lionGear.addEquipment("Fealty");
        lionGear.setAttackModifier(2);
        Infantry lion = new Infantry("Lion El'Jonson", "Dark Angels",
                new StatBlock(8, 6, 2, 6, 6), lionGear, "Primarch", true);
        // RULE: Martial Exemplar — Lion has Fights First in the Fight phase.
        lion.addAbility(new BattleTrait("Martial Exemplar",
                "The Primarch always Fights First in the Fight phase, striking before all enemies regardless of order.",
                "attacks", 1, "fights_first"));
        // RULE: Primarch of the First Legion — Aura: friendly Dark Angels CORE/CHARACTER
        //       within 6\" re-roll hit and wound rolls of 1.
        lion.addAbility(new BattleTrait("Primarch of the First Legion",
                "Aura: friendly Dark Angels Core and Character units within 6\" re-roll hit and wound rolls of 1.",
                "attacks", 1));
        // RULE: The Emperor's Shield — 4+ invulnerable save; unmodified melee save
        //       of 6 reflects 1 mortal wound back at the attacker.
        lion.addAbility(new BattleTrait("The Emperor's Shield",
                "Lion has a 4+ invulnerable save. Unmodified melee saving throws of 6 inflict 1 mortal wound on the attacker.",
                "toughness", 4, "invuln_save"));
        warband.addUnit(lion);

        // ── Azrael ────────────────────────────────────────────────────────────
        // 10th ed: M6  T4  Sv2+  W5
        // Sword of Secrets 7A S7 AP-3 D2  |  Lion's Wrath 3A S4 AP0 D1
        Wargear azraelGear = new Wargear();
        azraelGear.addEquipment("Sword of Secrets");
        azraelGear.addEquipment("Lion's Wrath");
        azraelGear.setAttackModifier(1);
        Infantry azrael = new Infantry("Azrael", "Dark Angels",
                new StatBlock(5, 4, 2, 6, 7), azraelGear, "Supreme Grand Master", true);
        // RULE: Lion Helm — Azrael and any unit he leads benefit from a 4+ invulnerable save.
        azrael.addAbility(new BattleTrait("Lion Helm",
                "Azrael and the unit he leads gain a 4+ invulnerable save from the Hood of Redemption.",
                "toughness", 4, "invuln_save"));
        // RULE: Supreme Grand Master (Leader) — while leading, the unit's weapons
        //       gain Sustained Hits 1 (unmodified 6 to hit scores 1 extra hit).
        azrael.addAbility(new BattleTrait("Supreme Grand Master",
                "Leader: while Azrael leads a unit, all weapons in that unit gain Sustained Hits 1.",
                "attacks", 1, "sustained_hits"));
        // RULE: Masterful Tactician — gain 1CP at the start of each Command phase
        //       while Azrael is on the battlefield.
        azrael.addAbility(new BattleTrait("Masterful Tactician",
                "Azrael generates 1 Command Point at the start of each Command phase, fuelling additional stratagems.",
                "speed", 1));
        warband.addUnit(azrael);

        // ── Hellblasters ×10 ──────────────────────────────────────────────────
        // 10th ed per model: M6  T4  Sv3+  W2  A2
        // Plasma Incinerator 2A S8 AP-4 D2 (standard); supercharge S9 AP-4 D3 Hazardous
        // Squad W = 10×2 = 20. +3 attack mod for concentrated plasma volley.
        Wargear hbGear = new Wargear();
        hbGear.addEquipment("Plasma Incinerators");
        hbGear.setAttackModifier(3);
        Infantry hellblasters = new Infantry("Hellblasters", "Dark Angels",
                new StatBlock(20, 4, 3, 6, 4), hbGear, "Hellblaster Squad", true);
        // RULE: For the Chapter! — when a model in this unit is destroyed, roll 1D6;
        //       on a 3+ it can shoot before being removed as a casualty.
        hellblasters.addAbility(new BattleTrait("For the Chapter!",
                "When a Hellblaster is destroyed, roll 1D6: on a 3+ it fires its weapon one last time before removal.",
                "attacks", 1));
        // RULE: Oath of Moment — Dark Angels army rule: re-roll all hit rolls against
        //       the army's designated target for the battle round.
        hellblasters.addAbility(new BattleTrait("Oath of Moment",
                "Dark Angels army rule: re-roll all hit rolls against the designated target unit each battle round.",
                "attacks", 1));
        // RULE: Supercharge — plasma incinerators can fire a supercharged profile
        //       (S9 AP-4 D3) but become Hazardous (roll 1 on any attack die = mortal wound to wielder).
        hellblasters.addAbility(new BattleTrait("Supercharge",
                "Plasma incinerators may supercharge to S9 AP-4 D3 at the risk of a Hazardous roll of 1 killing the firer.",
                "attacks", 2));
        warband.addUnit(hellblasters);

        // ── Chaplain Mordechai (Terminator Armour) ────────────────────────────
        // 10th ed: M5  T5  Sv2+  W4
        // Crozius Arcanum 4A S+2 AP-1 D2  |  Combi-weapon 2A S4 AP0 D1
        Wargear chaplainGear = new Wargear();
        chaplainGear.addEquipment("Crozius Arcanum");
        chaplainGear.addEquipment("Combi-weapon");
        chaplainGear.setAttackModifier(1);
        Infantry chaplain = new Infantry("Chaplain Mordechai", "Dark Angels",
                new StatBlock(4, 5, 2, 5, 4), chaplainGear, "Chaplain", true);
        // RULE: Litany of Hate (Leader) — while leading, melee attacks in the unit
        //       add +1 to their Wound rolls.
        chaplain.addAbility(new BattleTrait("Litany of Hate",
                "Leader: while Chaplain Mordechai leads a unit, all melee attacks in that unit add +1 to Wound rolls.",
                "attacks", 1));
        // RULE: Recitation of Faith (Leader) — while leading, the unit has
        //       Feel No Pain 4+ against mortal wounds.
        chaplain.addAbility(new BattleTrait("Recitation of Faith",
                "Leader: the Chaplain's litanies grant the led unit Feel No Pain 4+ against all mortal wounds.",
                "toughness", 4, "feel_no_pain"));
        warband.addUnit(chaplain);

        // ── Deathwing Knights "Iron Shroud" ×5 (led by Chaplain) ─────────────
        // 10th ed per model: M5  T5  Sv2+  W3  A4
        // Mace of Absolution 4A S+2 AP-3 D2  |  Storm Shield (4++ invuln)
        // Squad W = 5×3 = 15.
        Wargear dkAlphaGear = new Wargear();
        dkAlphaGear.addEquipment("Mace of Absolution");
        dkAlphaGear.addEquipment("Storm Shield");
        dkAlphaGear.setAttackModifier(1);
        Infantry dkAlpha = new Infantry(
                "Deathwing Knights \"Iron Shroud\"", "Dark Angels",
                new StatBlock(15, 5, 2, 5, 4), dkAlphaGear, "Deathwing Knights", true);
        // RULE: Inner Circle — subtract 1 from the Damage characteristic of all
        //       attacks allocated to models in this unit.
        dkAlpha.addAbility(new BattleTrait("Inner Circle",
                "Subtract 1 from the Damage of all attacks allocated to Deathwing Knights — their Terminator plate and conditioning absorb the worst blows.",
                "toughness", 1, "inner_circle"));
        // RULE: Storm Shield — models in this unit have a 4+ invulnerable save.
        dkAlpha.addAbility(new BattleTrait("Storm Shield",
                "Storm shields grant a 4+ invulnerable save, deflecting blows that pierce even Terminator plate.",
                "toughness", 4, "invuln_save"));
        // RULE: Deathwing Assault — this unit has the Deep Strike ability, teleporting
        //       from orbit and deploying anywhere on the battlefield.
        dkAlpha.addAbility(new BattleTrait("Deathwing Assault",
                "Deep Strike: the Deathwing teleport from orbit, materialising anywhere on the battlefield mid-battle.",
                "speed", 3));
        warband.addUnit(dkAlpha);

        // ── Captain Azmael (Terminator Armour) ────────────────────────────────
        // 10th ed: M5  T5  Sv2+  W6
        // Master-crafted Power Sword 5A S5 AP-3 D2  |  Combi-weapon 2A S4 AP0 D1
        Wargear captainGear = new Wargear();
        captainGear.addEquipment("Master-crafted Power Sword");
        captainGear.addEquipment("Combi-weapon");
        captainGear.setAttackModifier(1);
        Infantry captain = new Infantry("Captain Azmael", "Dark Angels",
                new StatBlock(6, 5, 2, 5, 5), captainGear, "Captain", true);
        // RULE: Rites of Battle — once per battle round, the Captain can use a
        //       Stratagem for 0CP even if it has already been used this phase.
        captain.addAbility(new BattleTrait("Rites of Battle",
                "Once per battle round, the Captain uses one Stratagem for 0 Command Points, even if already used this phase.",
                "speed", 1));
        // RULE: The Imperium's Sword — this model may re-roll its Charge rolls.
        captain.addAbility(new BattleTrait("The Imperium's Sword",
                "Captain Azmael re-rolls all Charge roll dice, closing with the enemy with decisive, unflinching speed.",
                "attacks", 1));
        // RULE: Deep Strike — the Captain can deploy via teleportation anywhere on
        //       the battlefield from Strategic Reserves.
        captain.addAbility(new BattleTrait("Deep Strike",
                "The Captain teleports from orbit, deploying anywhere on the battlefield to join the critical engagement.",
                "speed", 3));
        warband.addUnit(captain);

        // ── Deathwing Knights "Wrath Unbound" ×5 (led by Captain) ────────────
        Wargear dkBetaGear = new Wargear();
        dkBetaGear.addEquipment("Mace of Absolution");
        dkBetaGear.addEquipment("Storm Shield");
        dkBetaGear.setAttackModifier(1);
        Infantry dkBeta = new Infantry(
                "Deathwing Knights \"Wrath Unbound\"", "Dark Angels",
                new StatBlock(15, 5, 2, 5, 4), dkBetaGear, "Deathwing Knights", true);
        dkBeta.addAbility(new BattleTrait("Inner Circle",
                "Subtract 1 from the Damage of all attacks allocated to Deathwing Knights — their Terminator plate and conditioning absorb the worst blows.",
                "toughness", 1, "inner_circle"));
        dkBeta.addAbility(new BattleTrait("Storm Shield",
                "Storm shields grant a 4+ invulnerable save, deflecting blows that pierce even Terminator plate.",
                "toughness", 4, "invuln_save"));
        dkBeta.addAbility(new BattleTrait("Deathwing Assault",
                "Deep Strike: the Deathwing teleport from orbit, materialising anywhere on the battlefield mid-battle.",
                "speed", 3));
        warband.addUnit(dkBeta);

        // ── Librarian Seraphael (Terminator Armour, Psyker) ───────────────────
        // 10th ed: M5  T4  Sv2+  W4
        // Force Staff 4A S+3 AP-1 D3  |  Storm Bolter 4A S4 AP0 D1
        Wargear libTermGear = new Wargear();
        libTermGear.addEquipment("Force Staff");
        libTermGear.addEquipment("Storm Bolter");
        Psyker libTerminator = new Psyker("Librarian Seraphael", "Dark Angels",
                new StatBlock(4, 4, 2, 5, 4), libTermGear, "Librarius", 2);
        // PSYCHIC: Smite — the Librarian unleashes a bolt of pure psychic force
        //          that inflicts D3 mortal wounds on the nearest visible enemy.
        PsychicPower smite = new PsychicPower("Smite",
                "The Librarian manifests a bolt of psychic force, inflicting D3 mortal wounds on the nearest visible enemy unit.", 2);
        // PSYCHIC: Veil of Time (Librarius discipline) — while leading, unit's weapons
        //          gain Sustained Hits 1.
        PsychicPower veilOfTime = new PsychicPower("Veil of Time",
                "Librarius power: the Librarian unravels fate. While leading, the unit's weapons gain Sustained Hits 1.", 1);
        smite.addChainTrigger(veilOfTime);
        libTerminator.addAbility(smite);
        libTerminator.addAbility(veilOfTime);
        // RULE: Psychic Hood (Leader) — while leading, the unit has Feel No Pain 4+
        //       against mortal wounds from psychic attacks.
        libTerminator.addAbility(new BattleTrait("Psychic Hood",
                "Leader: the Psychic Hood grants the led unit Feel No Pain 4+ against mortal wounds caused by psychic attacks.",
                "toughness", 4, "feel_no_pain"));
        warband.addUnit(libTerminator);

        // ── Deathwing Knights "Dark Harbingers" ×5 (led by Librarian) ────────
        Wargear dkGammaGear = new Wargear();
        dkGammaGear.addEquipment("Mace of Absolution");
        dkGammaGear.addEquipment("Storm Shield");
        dkGammaGear.setAttackModifier(1);
        Infantry dkGamma = new Infantry(
                "Deathwing Knights \"Dark Harbingers\"", "Dark Angels",
                new StatBlock(15, 5, 2, 5, 4), dkGammaGear, "Deathwing Knights", true);
        dkGamma.addAbility(new BattleTrait("Inner Circle",
                "Subtract 1 from the Damage of all attacks allocated to Deathwing Knights — their Terminator plate and conditioning absorb the worst blows.",
                "toughness", 1, "inner_circle"));
        dkGamma.addAbility(new BattleTrait("Storm Shield",
                "Storm shields grant a 4+ invulnerable save, deflecting blows that pierce even Terminator plate.",
                "toughness", 4, "invuln_save"));
        dkGamma.addAbility(new BattleTrait("Deathwing Assault",
                "Deep Strike: the Deathwing teleport from orbit, materialising anywhere on the battlefield mid-battle.",
                "speed", 3));
        warband.addUnit(dkGamma);

        // ── Judiciar Vael ─────────────────────────────────────────────────────
        // 10th ed: M6  T4  Sv3+  W4
        // Executioner Relic Blade 4A S+3 AP-3 D3  |  Absolvor Bolt Pistol S5 AP-1 D2
        Wargear judiciarGear = new Wargear();
        judiciarGear.addEquipment("Executioner Relic Blade");
        judiciarGear.addEquipment("Absolvor Bolt Pistol");
        judiciarGear.setAttackModifier(1);
        Infantry judiciar = new Infantry("Judiciar Vael", "Dark Angels",
                new StatBlock(4, 4, 3, 6, 4), judiciarGear, "Judiciar", true);
        // RULE: Tempormortis (Leader) — while leading a unit, that unit has
        //       Fights First in the Fight phase.
        judiciar.addAbility(new BattleTrait("Tempormortis",
                "Leader: while Judiciar Vael leads a unit, that unit has Fights First — it strikes before all enemies in the Fight phase.",
                "attacks", 1, "fights_first"));
        // RULE: Fights First — the Judiciar's unit always fights before units
        //       without this ability in the same Fight phase.
        judiciar.addAbility(new BattleTrait("Fights First",
                "The Judiciar's hourglass marks those whose time has run out. His unit always strikes before enemies in melee.",
                "speed", 2, "fights_first"));
        // RULE: Precision — the Executioner Relic Blade has the Precision ability:
        //       hits can be allocated to CHARACTER models even when they are in units.
        judiciar.addAbility(new BattleTrait("Precision",
                "Executioner Relic Blade: Precision allows wounds to be allocated to CHARACTER models embedded in enemy units.",
                "attacks", 1));
        warband.addUnit(judiciar);

        // ── Inner Circle Companions ×6 ────────────────────────────────────────
        // 10th ed per model: M6  T4  Sv3+  W3  A4
        // Heavenfall Blade 4A S+2 AP-3 D2  |  Frostfury 3A S4 AP-1 D1
        // Squad W = 6×3 = 18.
        Wargear iccGear = new Wargear();
        iccGear.addEquipment("Heavenfall Blades");
        iccGear.addEquipment("Frostfury");
        iccGear.setAttackModifier(1);
        Infantry icc = new Infantry("Inner Circle Companions", "Dark Angels",
                new StatBlock(18, 4, 3, 6, 4), iccGear, "Inner Circle Companions", true);
        // RULE: Enmity for the Unworthy — add 1 to Hit rolls when this unit targets
        //       a CHARACTER unit.
        icc.addAbility(new BattleTrait("Enmity for the Unworthy",
                "Add 1 to Hit rolls when targeting a CHARACTER unit. The Companions are trained to hunt leaders above all else.",
                "attacks", 1));
        // RULE: Braziers of Judgement — while a CHARACTER leads this unit, subtract
        //       1 from the Hit rolls of attacks targeting this unit.
        icc.addAbility(new BattleTrait("Braziers of Judgement",
                "While a CHARACTER leads this unit, attackers subtract 1 from their Hit rolls. The Braziers' light confounds the foe.",
                "toughness", 1));
        // RULE: Storm Shield — models in this unit have a 4+ invulnerable save.
        icc.addAbility(new BattleTrait("Storm Shield",
                "Inner Circle Companions carry storm shields granting a 4+ invulnerable save against all attacks.",
                "toughness", 4, "invuln_save"));
        warband.addUnit(icc);

        // ── Scouts ×5 ─────────────────────────────────────────────────────────
        // 10th ed per model: M6  T4  Sv4+  W2  A2
        // Scout Sniper Rifle 2A S4 AP0 D2 (Precision, Indirect Fire)  |  Combat Knife A3 S3 AP0 D1
        // Squad W = 5×2 = 10.
        Wargear scoutGear = new Wargear();
        scoutGear.addEquipment("Scout Sniper Rifles");
        scoutGear.addEquipment("Combat Knives");
        Infantry scouts = new Infantry("Scout Squad", "Dark Angels",
                new StatBlock(10, 4, 4, 6, 2), scoutGear, "Scout Squad", false);
        // RULE: Scouts 6\" — before the first battle round, this unit can move up to
        //       6\", allowing early repositioning and claiming of objectives.
        scouts.addAbility(new BattleTrait("Scouts 6\"",
                "Before the first battle round begins, this unit moves up to 6\". Scouts advance through concealed routes to claim position.",
                "speed", 2));
        // RULE: Camo Cloaks — when this unit receives the benefit of cover, improve
        //       its armour save by 1 (from Sv4+ to Sv3+).
        scouts.addAbility(new BattleTrait("Camo Cloaks",
                "When Scouts receive cover, improve their armour save by 1. In concealed positions they are as hard to hit as ghosts.",
                "toughness", 1));
        // RULE: Guerrilla Tactics — at end of opponent's Movement phase, if this unit
        //       is more than 6\" from all enemies it can return to Strategic Reserves.
        scouts.addAbility(new BattleTrait("Guerrilla Tactics",
                "If more than 6\" from all enemies at end of the opponent's Movement phase, Scouts can melt away to redeploy from reserves.",
                "speed", 1));
        warband.addUnit(scouts);

        return warband;
    }

    // =========================================================================
    // EMPEROR'S CHILDREN  "Choir of Agony"  — 7 units
    // =========================================================================

    /**
     * Creates the Emperor's Children warband "Choir of Agony".
     *
     * <pre>
     * Fulgrim                  Psyker    W9   T7  Sv2+  M8   A6  +2
     * Lucius the Eternal       Infantry  W5   T4  Sv2+  M6   A9  +2
     * Lord Cacophonist         Infantry  W5   T4  Sv3+  M6   A6  +1
     * Noise Marines ×10        Infantry  W20  T4  Sv3+  M6   A3  +2
     * Infractors ×10           Infantry  W20  T4  Sv3+  M7   A4  +1
     * Tormentors ×10           Infantry  W20  T4  Sv3+  M6   A3  +1
     * Daemon Prince Xerathul   Psyker    W8   T7  Sv3+  M12  A6  +2
     * </pre>
     */
    public static Warband createEmperorsChildrenWarband() {
        Faction faction = new Faction("Emperor's Children", true);
        faction.addUnitType("Infantry");
        faction.addUnitType("Psyker");

        Warband warband = new Warband("Choir of Agony", faction);

        // ── Fulgrim (Daemon Primarch, Psyker) ─────────────────────────────────
        // 10th ed: M8  T7  Sv2+  W9  (MONSTER keyword — not INFANTRY)
        // The Blade of the Laer 6A S7 AP-3 D2  |  Fireblade 6A S5 AP-2 D1
        Wargear fulgrimGear = new Wargear();
        fulgrimGear.addEquipment("The Blade of the Laer");
        fulgrimGear.addEquipment("Fireblade");
        fulgrimGear.setAttackModifier(2);
        Psyker fulgrim = new Psyker("Fulgrim", "Emperor's Children",
                new StatBlock(9, 7, 2, 8, 6), fulgrimGear, "Slaanesh", 4);
        // PSYCHIC: Hysterical Frenzy — waves of ecstatic agony transmitted to the foe.
        //          Chains into Slaaneshi Ritual on a successful chain roll.
        PsychicPower hystericalFrenzy = new PsychicPower("Hysterical Frenzy",
                "Fulgrim transmits waves of ecstatic agony that drive the enemy into self-destructive madness.", 3);
        // SORCERY: Slaaneshi Ritual — Fulgrim draws on daemonic power at corruption cost.
        SorceryRitual slaaneshiRitual = new SorceryRitual("Slaaneshi Ritual",
                "Fulgrim draws deep on daemonic power. A howl of rapturous corruption ripples outward.", 4, 3);
        hystericalFrenzy.addChainTrigger(slaaneshiRitual);
        fulgrim.addAbility(hystericalFrenzy);
        fulgrim.addAbility(slaaneshiRitual);
        // RULE: Beguiling Form — subtract 1 from the Hit rolls of all attacks
        //       targeting Fulgrim. His transcendent beauty confounds the mind.
        fulgrim.addAbility(new BattleTrait("Beguiling Form",
                "Subtract 1 from all Hit rolls targeting Fulgrim. His impossible beauty confounds and distracts those who look upon him.",
                "toughness", 1));
        // RULE: Daemonic Speed — Fulgrim has Fights First in the Fight phase.
        //       He moves with supernatural grace far beyond mortal speed.
        fulgrim.addAbility(new BattleTrait("Daemonic Speed",
                "Fulgrim has Fights First in the Fight phase, striking before all enemies with preternatural, inhuman swiftness.",
                "speed", 2, "fights_first"));
        warband.addUnit(fulgrim);

        // ── Lucius the Eternal ────────────────────────────────────────────────
        // 10th ed: M6  T4  Sv2+  W5
        // Laeran Blade 9A S4 AP-3 D1  |  Doom Sirens 2A S5 AP-2 D2 (blast)
        Wargear luciusGear = new Wargear();
        luciusGear.addEquipment("Laeran Blade");
        luciusGear.addEquipment("Doom Sirens");
        luciusGear.setAttackModifier(2);
        Infantry lucius = new Infantry("Lucius the Eternal", "Emperor's Children",
                new StatBlock(5, 4, 2, 6, 9), luciusGear, "Champion of Slaanesh", true);
        // RULE: The Art of Pain — Lucius re-rolls all hit and wound rolls in melee.
        //       Ten thousand years of practice have made him a blade without equal.
        lucius.addAbility(new BattleTrait("The Art of Pain",
                "Lucius re-rolls all melee hit rolls. Ten thousand years of practice and the ecstasy of pain have made him a blade without equal.",
                "attacks", 2));
        // RULE: A Challenge Worthy of Skill — re-roll both Hit AND Wound rolls when
        //       targeting CHARACTER, MONSTER, or WALKER units. Lucius hunts the mighty.
        lucius.addAbility(new BattleTrait("A Challenge Worthy of Skill",
                "Lucius re-rolls all Hit and Wound rolls when targeting CHARACTER, MONSTER, or WALKER units. Only the worthy hold his interest.",
                "attacks", 2));
        // RULE: Duellist's Hubris — Lucius has Fights First when he is not leading a
        //       unit. His pride demands he strike before any other.
        lucius.addAbility(new BattleTrait("Duellist's Hubris",
                "When not leading a unit, Lucius has Fights First. His pride demands he claim the first blow in every engagement.",
                "speed", 2, "fights_first"));
        warband.addUnit(lucius);

        // ── Lord Cacophonist ──────────────────────────────────────────────────
        // 10th ed: M6  T4  Sv3+  W5
        // Instrument of Excess 6A S5 AP-2 D2  |  Blastmaster 4A S8 AP-2 D3
        Wargear cacophonistGear = new Wargear();
        cacophonistGear.addEquipment("Instrument of Excess");
        cacophonistGear.addEquipment("Blastmaster");
        cacophonistGear.setAttackModifier(1);
        Infantry lordCacophonist = new Infantry("Lord Cacophonist", "Emperor's Children",
                new StatBlock(5, 4, 3, 6, 6), cacophonistGear, "Lord Cacophonist", true);
        // RULE: Obsessive Annunciation (Leader) — while leading, the unit's ranged
        //       weapons gain Sustained Hits 1 (unmodified 6 to hit = 1 extra hit).
        lordCacophonist.addAbility(new BattleTrait("Obsessive Annunciation",
                "Leader: while commanding Noise Marines, all ranged weapons in the unit gain Sustained Hits 1 — every unmodified 6 to hit scores one additional hit.",
                "attacks", 1, "sustained_hits"));
        // RULE: Doom Siren — after shooting, roll 3D6 against a hit INFANTRY unit;
        //       on 4+ per die, it suffers a mortal wound and must take a Battle-shock test.
        lordCacophonist.addAbility(new BattleTrait("Doom Siren",
                "After shooting, roll 3D6 vs a hit Infantry unit. On a 4+ per die, inflict a mortal wound and force a Battle-shock test.",
                "attacks", 2));
        // RULE: Symphony of Pain — the Cacophonist conducts the battlefield; his
        //       presence elevates his warriors' performance to transcendent heights.
        lordCacophonist.addAbility(new BattleTrait("Symphony of Pain",
                "The Lord Cacophonist's presence pushes his warriors to ecstatic excess, conducting carnage as a maestro conducts music.",
                "toughness", 1));
        warband.addUnit(lordCacophonist);

        // ── Noise Marines ×10 ─────────────────────────────────────────────────
        // 10th ed per model: M6  T4  Sv3+  W2  A3
        // Sonic Blaster 3A S4 AP-1 D1 (Ignores Cover)  |  Bolt Pistol S4 AP0 D1
        // Squad W = 10×2 = 20. +2 attack mod for sonic fire volume.
        Wargear nmGear = new Wargear();
        nmGear.addEquipment("Sonic Blasters");
        nmGear.addEquipment("Bolt Pistols");
        nmGear.setAttackModifier(2);
        Infantry noiseMarines = new Infantry("Noise Marines", "Emperor's Children",
                new StatBlock(20, 4, 3, 6, 3), nmGear, "Noise Marine Squad", true);
        // RULE: Terrifying Crescendo — after shooting, select one unit that was hit;
        //       until your next Shooting phase it subtracts 1 from Battle-shock tests.
        noiseMarines.addAbility(new BattleTrait("Terrifying Crescendo",
                "After shooting, one hit unit subtracts 1 from all Battle-shock and Leadership tests until next turn. The noise shatters resolve.",
                "toughness", 1));
        // RULE: Ignores Cover — Sonic Blasters have the Ignores Cover ability.
        //       The sonic waves bypass terrain, cover saves provide no benefit.
        noiseMarines.addAbility(new BattleTrait("Ignores Cover",
                "Sonic Blasters have Ignores Cover: cover saves provide no benefit against the squad's weapons — the sound finds every gap.",
                "attacks", 1));
        // FACTION RULE: Thrill Seekers — Emperor's Children units can Advance and
        //       still shoot or charge, maintaining relentless momentum.
        noiseMarines.addAbility(new BattleTrait("Thrill Seekers",
                "Emperor's Children faction rule: this unit can Advance and still shoot and/or declare a charge in the same turn.",
                "speed", 1));
        warband.addUnit(noiseMarines);

        // ── Infractors ×10 ────────────────────────────────────────────────────
        // Emperor's Children assault infantry: M7  T4  Sv3+  W2  A4
        // Duelling Sabres (Precision) 4A S4 AP-1 D1  |  Excruciator Pistol 3A S4 AP-1 D1
        // Squad W = 10×2 = 20. +1 attack mod for pistol volley on the charge.
        Wargear infractorGear = new Wargear();
        infractorGear.addEquipment("Duelling Sabres");
        infractorGear.addEquipment("Excruciator Pistols");
        infractorGear.setAttackModifier(1);
        Infantry infractors = new Infantry("Infractors", "Emperor's Children",
                new StatBlock(20, 4, 3, 7, 4), infractorGear, "Infractors", false);
        // RULE: Scouts 6\" — Infractors move up to 6\" before the first battle round,
        //       harassing forward positions before the main engagement.
        infractors.addAbility(new BattleTrait("Scouts 6\"",
                "Before the first battle round, Infractors move up to 6\", advancing through forward positions to seize the initiative.",
                "speed", 2));
        // RULE: Excessive Assault — re-roll Wound rolls of 1 in melee; if targeting
        //       a unit on an objective marker, re-roll the full Wound roll instead.
        infractors.addAbility(new BattleTrait("Excessive Assault",
                "Re-roll Wound rolls of 1 in melee. When fighting a unit on an objective, re-roll the entire Wound roll for every attack.",
                "attacks", 1));
        // FACTION RULE: Thrill Seekers — advance and still shoot or charge.
        infractors.addAbility(new BattleTrait("Thrill Seekers",
                "Emperor's Children faction rule: this unit can Advance and still shoot and/or declare a charge in the same turn.",
                "speed", 1));
        warband.addUnit(infractors);

        // ── Tormentors ×10 ────────────────────────────────────────────────────
        // Emperor's Children infiltrating heavy infantry: M6  T4  Sv3+  W2  A3
        // Sonic Shrieker 3A S5 AP-1 D2  |  Pain Gauntlet 3A S4 AP-2 D1
        // Squad W = 10×2 = 20.
        Wargear tormentorGear = new Wargear();
        tormentorGear.addEquipment("Sonic Shriekers");
        tormentorGear.addEquipment("Pain Gauntlets");
        tormentorGear.setAttackModifier(1);
        Infantry tormentors = new Infantry("Tormentors", "Emperor's Children",
                new StatBlock(20, 4, 3, 6, 3), tormentorGear, "Tormentors", false);
        // RULE: Exquisite Agony — Tormentors embrace every wound. Represents the
        //       resilience granted by Slaanesh's blessing and their pain-ecstasy state.
        tormentors.addAbility(new BattleTrait("Exquisite Agony",
                "Tormentors revel in pain, treating every wound as a moment of rapture. Their ecstatic state makes them harder to stop.",
                "toughness", 5, "feel_no_pain"));
        // RULE: Infiltrators — Tormentors have Deep Strike, deploying from reserves
        //       anywhere on the battlefield to seize objectives or strike flanks.
        tormentors.addAbility(new BattleTrait("Infiltrators",
                "Deep Strike: Tormentors deploy anywhere on the battlefield from Strategic Reserves, appearing exactly where least expected.",
                "speed", 3));
        // FACTION RULE: Thrill Seekers — advance and still shoot or charge.
        tormentors.addAbility(new BattleTrait("Thrill Seekers",
                "Emperor's Children faction rule: this unit can Advance and still shoot and/or declare a charge in the same turn.",
                "speed", 1));
        warband.addUnit(tormentors);

        // ── Daemon Prince Xerathul with Wings (Psyker) ────────────────────────
        // 10th ed: M12  T7  Sv3+  W8  (FLY, MONSTER, DAEMON)
        // Hellforged Sword 6A S+2 AP-2 D3  |  Malefic Talons 5A S+1 AP-2 D2
        Wargear dpGear = new Wargear();
        dpGear.addEquipment("Hellforged Sword");
        dpGear.addEquipment("Malefic Talons");
        dpGear.setAttackModifier(2);
        Psyker daemonPrince = new Psyker("Daemon Prince Xerathul", "Emperor's Children",
                new StatBlock(8, 7, 3, 12, 6), dpGear, "Slaanesh", 2);
        // PSYCHIC: Diabolic Strength — channels raw daemonic energy into attacks.
        PsychicPower diabolicStrength = new PsychicPower("Diabolic Strength",
                "Xerathul channels pure daemonic power, his strikes landing with the force of a battering ram, shattering armour and bone.", 2);
        // SORCERY: Rapturous Blast — a wave of daemonic ecstasy that overwhelms senses.
        SorceryRitual rapturousBlast = new SorceryRitual("Rapturous Blast",
                "A wave of daemonic ecstasy erupts outward, overloading the senses of all nearby and causing the weak-willed to weep blood.", 3, 2);
        daemonPrince.addAbility(diabolicStrength);
        daemonPrince.addAbility(rapturousBlast);
        // RULE: Stimulated by Pain — subtract 1 from the Damage of all attacks
        //       allocated to this model. The Daemon Prince absorbs pain as pleasure.
        daemonPrince.addAbility(new BattleTrait("Stimulated by Pain",
                "Subtract 1 from the Damage of all attacks targeting Xerathul. The daemon absorbs agony as ecstasy, shrugging off grievous wounds.",
                "toughness", 1, "inner_circle"));
        // RULE: Daemonic Destruction — at the end of a charge move, roll D6 for each
        //       wound; on 4+ inflict a mortal wound on each enemy unit in Engagement Range.
        daemonPrince.addAbility(new BattleTrait("Daemonic Destruction",
                "After charging, roll D6 for each wound characteristic against every enemy in Engagement Range — on 4+ inflict a mortal wound per result.",
                "attacks", 2));
        warband.addUnit(daemonPrince);

        return warband;
    }

    // =========================================================================
    // CHAOS KNIGHTS  "Echoes of Ruin"  — 8 units
    // =========================================================================

    /**
     * Creates the Chaos Knights warband "Echoes of Ruin".
     *
     * <pre>
     * The Sable Lance (Cerastus Lancer)  Knight  W24  T11  Sv2+  M14  A5  +2
     * Tyrant of Ruin (Despoiler)         Knight  W22  T11  Sv3+  M8   A4  +3
     * Wrath Eternal (Twin-Gatling)       Knight  W22  T11  Sv3+  M8   A6  +3
     * Ruinous Tide  (Twin-Gatling)       Knight  W22  T11  Sv3+  M8   A6  +3
     * Vextrix the Abominant              Knight  W18  T12  Sv3+  M8   A5  +2
     * Bloodfang (War Dog Karnivore)      Knight  W10  T9   Sv3+  M12  A6  +1
     * Bonecleaver (War Dog Karnivore)    Knight  W10  T9   Sv3+  M12  A6  +1
     * Darkfire (War Dog Stalker)         Knight  W10  T9   Sv3+  M12  A4  +2
     * </pre>
     */
    public static Warband createChaosKnightsWarband() {
        Faction faction = new Faction("Chaos Knights", true);
        faction.addUnitType("Knight");

        Warband warband = new Warband("Echoes of Ruin", faction);

        // ── Cerastus Knight Lancer "The Sable Lance" (Soul-Bound) ─────────────
        // Forgeworld / 10th ed Legends: M14  T11  Sv2+  W24  (4++ invuln ranged+melee)
        // Shock Lance 5A S14 AP-3 D4 (Sustained Hits 2)  |  Cerastus Ion Gauntlet (melee shield)
        Wargear lancerGear = new Wargear();
        lancerGear.addEquipment("Shock Lance");
        lancerGear.addEquipment("Cerastus Ion Gauntlet");
        lancerGear.setAttackModifier(2);
        Knight lancer = new Knight("The Sable Lance", "Chaos Knights",
                new StatBlock(24, 11, 2, 14, 5), lancerGear, "Cerastus Knight Lancer", true);
        // RULE: Lance Charge — the Sable Lance builds devastating speed; represents
        //       the Shock Charge ability (use Tank Shock for 0CP without restriction).
        lancer.addAbility(new BattleTrait("Lance Charge",
                "The Sable Lance uses Tank Shock for 0 Command Points. Enemies hit by the charge take Impact mortal wounds from the Shock Lance.",
                "speed", 3));
        // RULE: Shock Lance — the weapon has Sustained Hits 2; every unmodified 6
        //       to hit scores 2 additional hits on top of the original.
        lancer.addAbility(new BattleTrait("Shock Lance",
                "Sustained Hits 2: every unmodified 6 to hit with the Shock Lance scores 2 extra hits. The lance crackles with lethal energy.",
                "attacks", 2, "sustained_hits"));
        // RULE: Cerastus Ion Gauntlet Shield — 4+ invulnerable save in both ranged
        //       and melee phases (most Knights only have 5++ vs ranged). Best in class.
        lancer.addAbility(new BattleTrait("Ion Gauntlet Shield",
                "4+ invulnerable save in both ranged and melee phases. The shield ward is superior to any other Chaos Knight protection.",
                "toughness", 4, "invuln_save"));
        // RULE: Harbingers of Dread — army-wide aura: enemy units within 18\" must
        //       subtract 1 from Battle-shock and Leadership tests.
        lancer.addAbility(new BattleTrait("Harbingers of Dread",
                "Army rule: enemy units within 18\" subtract 1 from all Battle-shock and Leadership tests. The titan's approach breaks minds.",
                "toughness", 1));
        warband.addUnit(lancer);

        // ── Knight Tyrant "Tyrant of Ruin" (Soul-Bound Despoiler) ─────────────
        // 10th ed: M8  T11  Sv3+  W22  (5++ vs ranged)
        // Reaper Chainsword 4+D6A S16 AP-3 D6  |  Rapid-Fire Battle Cannon S8 AP-2 D3 (Blast)
        Wargear tyrantGear = new Wargear();
        tyrantGear.addEquipment("Reaper Chainsword");
        tyrantGear.addEquipment("Rapid-Fire Battle Cannon");
        tyrantGear.setAttackModifier(3);
        Knight tyrant = new Knight("Tyrant of Ruin", "Chaos Knights",
                new StatBlock(22, 11, 3, 8, 4), tyrantGear, "Despoiler", true);
        // RULE: Titanic Stomp — D3+3 attacks S8 AP-1 D1 targeting every enemy in
        //       Engagement Range. Represents the Knight's crushing Stomp ability.
        tyrant.addAbility(new BattleTrait("Titanic Stomp",
                "At the end of the Fight phase, the Knight makes D3+3 Stomp attacks against every enemy unit in Engagement Range.",
                "attacks", 2));
        // RULE: Seething Hatred — once per Fight phase, re-roll one Hit roll or
        //       one Wound roll. A dark patron lends its patron's hate to each blow.
        tyrant.addAbility(new BattleTrait("Seething Hatred",
                "Once per Fight phase, re-roll one Hit roll or one Wound roll. The dark patron's hatred flows through every strike.",
                "attacks", 1));
        // RULE: Harbingers of Dread — enemy units within 18\" subtract 1 from
        //       Battle-shock and Leadership tests.
        tyrant.addAbility(new BattleTrait("Harbingers of Dread",
                "Army rule: enemy units within 18\" subtract 1 from all Battle-shock and Leadership tests. The titan's approach breaks minds.",
                "toughness", 1));
        warband.addUnit(tyrant);

        // ── Twin-Gatling Despoiler "Wrath Eternal" (Soul-Bound) ───────────────
        // 10th ed: M8  T11  Sv3+  W22  (5++ vs ranged)
        // Twin Avenger Gatling Cannon 12A S6 AP-1 D2  |  Reaper Chainsword 4+D6A S16 AP-3 D6
        Wargear wrathGear = new Wargear();
        wrathGear.addEquipment("Twin Avenger Gatling Cannon");
        wrathGear.addEquipment("Reaper Chainsword");
        wrathGear.setAttackModifier(3);
        Knight wrathEternal = new Knight("Wrath Eternal", "Chaos Knights",
                new StatBlock(22, 11, 3, 8, 6), wrathGear, "Despoiler", true);
        // RULE: Iron Storm — the twin gatling cannon fires an overwhelming volume
        //       of shells; represents the 12-shot salvo and Sustained Hits 1.
        wrathEternal.addAbility(new BattleTrait("Iron Storm",
                "Twin Avenger Gatling Cannon: 12 shots with Sustained Hits 1. The wall of shells fills the air and breaks formations.",
                "attacks", 1, "sustained_hits"));
        // RULE: Grinding Advance — fire Heavy weapons without penalty while moving.
        //       Wrath Eternal maintains suppressive fire while repositioning.
        wrathEternal.addAbility(new BattleTrait("Grinding Advance",
                "Wrath Eternal fires its Twin Avenger Gatling Cannon without penalty while moving — suppressive fire continues regardless of advance.",
                "speed", 1));
        // RULE: Harbingers of Dread — as above.
        wrathEternal.addAbility(new BattleTrait("Harbingers of Dread",
                "Army rule: enemy units within 18\" subtract 1 from all Battle-shock and Leadership tests. The titan's approach breaks minds.",
                "toughness", 1));
        warband.addUnit(wrathEternal);

        // ── Twin-Gatling Despoiler "Ruinous Tide" (Soul-Bound) ────────────────
        // Identical loadout to Wrath Eternal; second of the pair.
        Wargear ruinGear = new Wargear();
        ruinGear.addEquipment("Twin Avenger Gatling Cannon");
        ruinGear.addEquipment("Reaper Chainsword");
        ruinGear.setAttackModifier(3);
        Knight ruinousTide = new Knight("Ruinous Tide", "Chaos Knights",
                new StatBlock(22, 11, 3, 8, 6), ruinGear, "Despoiler", true);
        ruinousTide.addAbility(new BattleTrait("Iron Storm",
                "Twin Avenger Gatling Cannon: 12 shots with Sustained Hits 1. The wall of shells fills the air and breaks formations.",
                "attacks", 1, "sustained_hits"));
        ruinousTide.addAbility(new BattleTrait("Grinding Advance",
                "Ruinous Tide fires its Twin Avenger Gatling Cannon without penalty while moving — suppressive fire continues regardless of advance.",
                "speed", 1));
        ruinousTide.addAbility(new BattleTrait("Harbingers of Dread",
                "Army rule: enemy units within 18\" subtract 1 from all Battle-shock and Leadership tests. The titan's approach breaks minds.",
                "toughness", 1));
        warband.addUnit(ruinousTide);

        // ── Knight Abominant "Vextrix the Abominant" (Soul-Bound, Psyker Knight) ─
        // 10th ed: M8  T12  Sv3+  W18  (PSYKER keyword — only psychic Chaos Knight)
        // Electro-Gauntlets 5A S14 AP-3 D2  |  Volkite Combustor S7 AP-1 D2 (Devastating Wounds)
        Wargear abominantGear = new Wargear();
        abominantGear.addEquipment("Electro-Gauntlets");
        abominantGear.addEquipment("Volkite Combustor");
        abominantGear.setAttackModifier(2);
        Knight abominant = new Knight("Vextrix the Abominant", "Chaos Knights",
                new StatBlock(18, 12, 3, 8, 5), abominantGear, "Abominant", true);
        // RULE: Fell Bond: Infernal Quest — bound to a dark patron's pact granting
        //       supernatural resilience; represents the Abominant's enhanced durability.
        abominant.addAbility(new BattleTrait("Fell Bond: Infernal Quest",
                "Vextrix is bound to a dark patron's infernal pact, granting supernatural resilience that makes it harder to kill than any other Knight.",
                "toughness", 5, "feel_no_pain"));
        // RULE: Obsessive Ruthlessness — ranged attacks targeting MONSTER or VEHICLE
        //       units gain Devastating Wounds (unmodified 6 to wound = mortal wounds).
        abominant.addAbility(new BattleTrait("Obsessive Ruthlessness",
                "Ranged attacks targeting MONSTER or VEHICLE gain Devastating Wounds: unmodified 6 to wound causes mortal wounds, bypassing saves.",
                "attacks", 2, "devastating_wounds"));
        // PSYCHIC: Vortex Terrors — the Abominant forces Battle-shock tests on
        //          enemy units within 24\" at the start of the Shooting phase.
        abominant.addAbility(new PsychicPower("Vortex Terrors",
                "At start of the Shooting phase, force a Battle-shock test on one enemy unit within 24\". A maw of warp energy tears open before them.", 1));
        // PSYCHIC: Warp Storms — inflict D3 mortal wounds on all enemy units within
        //          9\" on a roll of 3+. The Abominant's warp power erupts outward.
        abominant.addAbility(new PsychicPower("Warp Storms",
                "At the end of the Movement phase, roll for each enemy unit within 9\": on a 3+, inflict D3 mortal wounds as warp energy erupts.", 2));
        warband.addUnit(abominant);

        // ── War Dog Karnivore "Bloodfang" (Unbound) ───────────────────────────
        // 10th ed: M12  T9  Sv3+  W10  (5++ vs ranged)
        // Reaper Chaintalon 6A S12 AP-3 D3  |  Slaughterclaw 3A S14 AP-4 D4
        Wargear bloodfangGear = new Wargear();
        bloodfangGear.addEquipment("Reaper Chaintalon");
        bloodfangGear.addEquipment("Slaughterclaw");
        bloodfangGear.setAttackModifier(1);
        Knight bloodfang = new Knight("Bloodfang", "Chaos Knights",
                new StatBlock(10, 9, 3, 12, 6), bloodfangGear, "War Dog Karnivore", false);
        // RULE: Karnivore — re-roll all Advance rolls and all Charge rolls. The
        //       Karnivore hunts with terrifying, daemon-driven relentlessness.
        bloodfang.addAbility(new BattleTrait("Karnivore",
                "Re-roll all Advance and Charge rolls. Bloodfang hunts with daemon-driven relentlessness, closing the gap faster than physics should allow.",
                "speed", 2));
        // RULE: Frenzied Rampage — both melee weapons (Chaintalon + Slaughterclaw)
        //       combine for devastating output; represents the dual-weapon frenzy.
        bloodfang.addAbility(new BattleTrait("Frenzied Rampage",
                "Both the Reaper Chaintalon and Slaughterclaw attack simultaneously in an uncontrolled frenzy. Bloodfang tears apart anything it reaches.",
                "attacks", 2));
        // RULE: Harbingers of Dread — as above; War Dogs share the army-wide rule.
        bloodfang.addAbility(new BattleTrait("Harbingers of Dread",
                "Army rule: enemy units within 18\" subtract 1 from all Battle-shock and Leadership tests. The titan's approach breaks minds.",
                "toughness", 1));
        warband.addUnit(bloodfang);

        // ── War Dog Karnivore "Bonecleaver" (Unbound) ────────────────────────
        // Identical loadout and rules to Bloodfang; the second Karnivore of the pack.
        Wargear bonecleaverGear = new Wargear();
        bonecleaverGear.addEquipment("Reaper Chaintalon");
        bonecleaverGear.addEquipment("Slaughterclaw");
        bonecleaverGear.setAttackModifier(1);
        Knight bonecleaver = new Knight("Bonecleaver", "Chaos Knights",
                new StatBlock(10, 9, 3, 12, 6), bonecleaverGear, "War Dog Karnivore", false);
        bonecleaver.addAbility(new BattleTrait("Karnivore",
                "Re-roll all Advance and Charge rolls. Bonecleaver hunts with daemon-driven relentlessness, closing the gap faster than physics should allow.",
                "speed", 2));
        bonecleaver.addAbility(new BattleTrait("Frenzied Rampage",
                "Both the Reaper Chaintalon and Slaughterclaw attack simultaneously in an uncontrolled frenzy. Bonecleaver tears apart anything it reaches.",
                "attacks", 2));
        bonecleaver.addAbility(new BattleTrait("Harbingers of Dread",
                "Army rule: enemy units within 18\" subtract 1 from all Battle-shock and Leadership tests. The titan's approach breaks minds.",
                "toughness", 1));
        warband.addUnit(bonecleaver);

        // ── War Dog Stalker "Darkfire" (Unbound) ──────────────────────────────
        // 10th ed: M12  T9  Sv3+  W10  (5++ vs ranged)
        // Avenger Chaincannon 12A S6 AP-1 D2  |  Slaughterclaw 4A S12 AP-4 D4
        Wargear darkfireGear = new Wargear();
        darkfireGear.addEquipment("Avenger Chaincannon");
        darkfireGear.addEquipment("Slaughterclaw");
        darkfireGear.setAttackModifier(2);
        Knight darkfire = new Knight("Darkfire", "Chaos Knights",
                new StatBlock(10, 9, 3, 12, 4), darkfireGear, "War Dog Stalker", false);
        // RULE: Daemonic Pact — Darkfire's pilot has bound itself to a lesser daemon;
        //       represents the 5++ invuln save from daemonic protection.
        darkfire.addAbility(new BattleTrait("Daemonic Pact",
                "Darkfire's pilot has bound itself to a lesser daemon, hardening the war engine: 5+ invulnerable save vs ranged attacks.",
                "toughness", 5, "invuln_save"));
        // RULE: Stalker — add +1 to all Wound rolls when attacking an enemy unit that
        //       has no other enemy units within 6\" of it. The Stalker isolates prey.
        darkfire.addAbility(new BattleTrait("Stalker",
                "Add +1 to all Wound rolls when attacking an isolated enemy unit (no other enemy units within 6\" of target). Pick off the stragglers.",
                "attacks", 1));
        // RULE: Suppressing Fire — the Avenger Chaincannon's volume of fire (12 shots)
        //       pins targets; represents the suppressive capability of this weapon.
        darkfire.addAbility(new BattleTrait("Suppressing Fire",
                "Avenger Chaincannon: 12 shots that pin enemy units in place. Units that suffer casualties cannot advance in their next turn.",
                "attacks", 2));
        // RULE: Harbingers of Dread — as above.
        darkfire.addAbility(new BattleTrait("Harbingers of Dread",
                "Army rule: enemy units within 18\" subtract 1 from all Battle-shock and Leadership tests. The titan's approach breaks minds.",
                "toughness", 1));
        warband.addUnit(darkfire);

        return warband;
    }
}

/*
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 3; three static factory
 *                             methods (createDarkAngelsWarband, createEmperorsChildrenWarband,
 *                             createChaosKnightsWarband); each builds a named Warband
 *                             with all units pre-populated with stats and abilities
 *   2026-04-08  Shane Potts  Expanded to full 10th ed datasheets with named
 *                             characters; fleshed out abilities with accurate
 *                             special rules from each unit's datasheet; all
 *                             special rules mapped to BattleTrait trigger strings
 *                             consumed by CombatEngine attack resolution pipeline
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes this phase
 */
