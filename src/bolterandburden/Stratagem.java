/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Represents a single Warhammer 40,000 10th edition Stratagem —
 *              a special ability the player activates by spending Command Points
 *              during combat. Each Stratagem has a CP cost, a name, a flavour
 *              description, an EffectType that controls what applyEffect() does
 *              to the chosen unit, and a TargetScope that governs valid targets.
 *
 *              Effects are delivered by injecting a temporary BattleTrait with
 *              the appropriate trigger string into the target unit's ability list.
 *              This reuses the existing CombatEngine trigger infrastructure
 *              (feel_no_pain, invuln_save, fights_first, sustained_hits,
 *              devastating_wounds) without requiring new engine code.
 *
 *              A HEAL effect directly restores wounds instead of adding a trait.
 *              A BUFF effect adds a passive +attacks BattleTrait (trigger "passive")
 *              that increases the effective attack count via Wargear modifier paths.
 *
 *              Stratagems are one-use per combat: usedThisCombat tracks whether
 *              the Stratagem has already fired. Call resetForNewCombat() between
 *              engagements to make it available again.
 * Inputs:      String name/description, int cost, EffectType, TargetScope,
 *              int modifier; target Unit passed to applyEffect()
 * Outputs:     Console narrative; BattleTrait injected into target unit; or
 *              direct wound restoration for HEAL effects
 *
 * Change Log:
 *   2026-04-09  Shane Potts  Initial creation — Phase 4 Detachment/CP system;
 *                             EffectType enum (FEEL_NO_PAIN, INVULN_BOOST,
 *                             FIGHTS_FIRST, SUSTAINED_HITS, DEVASTATING, HEAL,
 *                             EXTRA_ATTACKS); TargetScope enum (ANY_FRIENDLY,
 *                             INFANTRY_ONLY, VEHICLE_ONLY, PSYKER_ONLY);
 *                             applyEffect() injects temporary BattleTrait with
 *                             appropriate trigger into target unit's ability list,
 *                             reusing existing CombatEngine trigger pipeline;
 *                             HEAL directly restores wounds; usedThisCombat flag
 *                             enforces one-use-per-engagement limit;
 *                             isValidTarget() uses instanceof to scope targets
 *   2026-04-26  Shane Potts  Phase 4 complete - no further changes this phase
 */

package bolterandburden;

public class Stratagem {

    // -------------------------------------------------------------------------
    // Enums
    // -------------------------------------------------------------------------

    /**
     * The mechanical effect this Stratagem produces when applied.
     * <ul>
     *   <li>FEEL_NO_PAIN  — injects a feel_no_pain BattleTrait (modifier = threshold)</li>
     *   <li>INVULN_BOOST  — injects an invuln_save BattleTrait (modifier = save threshold)</li>
     *   <li>FIGHTS_FIRST  — injects a fights_first BattleTrait for this round</li>
     *   <li>SUSTAINED_HITS— injects a sustained_hits BattleTrait (modifier = bonus hits per 6)</li>
     *   <li>DEVASTATING   — injects a devastating_wounds BattleTrait</li>
     *   <li>HEAL          — directly restores modifier wounds on the target</li>
     *   <li>EXTRA_ATTACKS — injects a sustained_hits trait (sustained hits approximate extra attacks)</li>
     * </ul>
     */
    public enum EffectType {
        FEEL_NO_PAIN,
        INVULN_BOOST,
        FIGHTS_FIRST,
        SUSTAINED_HITS,
        DEVASTATING,
        HEAL,
        EXTRA_ATTACKS
    }

    /**
     * Which units may legally be targeted by this Stratagem.
     * <ul>
     *   <li>ANY_FRIENDLY — any living unit in the player's warband</li>
     *   <li>INFANTRY_ONLY — only Infantry or Psyker units</li>
     *   <li>VEHICLE_ONLY  — only Vehicle or Knight units</li>
     *   <li>PSYKER_ONLY   — only Psyker units</li>
     * </ul>
     */
    public enum TargetScope {
        ANY_FRIENDLY,
        INFANTRY_ONLY,
        VEHICLE_ONLY,
        PSYKER_ONLY
    }

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private final String     name;
    private final String     description;
    private final int        cost;
    private final EffectType effectType;
    private final TargetScope targetScope;
    private final int        modifier;
    private       boolean    usedThisCombat;

    // -------------------------------------------------------------------------
    // Construction
    // -------------------------------------------------------------------------

    /**
     * Creates a fully specified Stratagem.
     *
     * @param name        Display name (e.g. "Transhuman Physiology")
     * @param description Flavour/rules summary shown in the menu
     * @param cost        CP cost to activate
     * @param effectType  What mechanical effect fires on the target
     * @param targetScope Which units are valid targets
     * @param modifier    Numeric parameter for the effect (save threshold, bonus hits, wounds healed, etc.)
     */
    public Stratagem(String name, String description, int cost,
                     EffectType effectType, TargetScope targetScope, int modifier) {
        this.name           = name;
        this.description    = description;
        this.cost           = cost;
        this.effectType     = effectType;
        this.targetScope    = targetScope;
        this.modifier       = modifier;
        this.usedThisCombat = false;
    }

    // -------------------------------------------------------------------------
    // Core Effect
    // -------------------------------------------------------------------------

    /**
     * Applies this Stratagem's effect to the given target unit. Injects a
     * temporary BattleTrait (or heals wounds for HEAL effects). Prints a
     * narrative line describing what fired.
     *
     * <p>Call this only after spending CP via CommandPoints.spend(). This method
     * does not check CP — the caller is responsible for affordability.
     *
     * @param target The unit receiving the effect
     */
    public void applyEffect(Unit target) {
        usedThisCombat = true;
        System.out.println(Color.c("  [STRATAGEM] " + name + " activated on "
                + target.getName() + "!", Color.BOLD + Color.YELLOW));

        switch (effectType) {

            case FEEL_NO_PAIN:
                target.addAbility(new BattleTrait(
                        "Strat: " + name,
                        "Stratagem-granted Feel No Pain " + modifier + "+",
                        "toughness", modifier, "feel_no_pain"));
                System.out.println(Color.c("     " + target.getName()
                        + " gains Feel No Pain " + modifier + "+!", Color.YELLOW));
                break;

            case INVULN_BOOST:
                target.addAbility(new BattleTrait(
                        "Strat: " + name,
                        "Stratagem-granted invulnerable save " + modifier + "++",
                        "toughness", modifier, "invuln_save"));
                System.out.println(Color.c("     " + target.getName()
                        + " gains a " + modifier + "++ invulnerable save!", Color.YELLOW));
                break;

            case FIGHTS_FIRST:
                target.addAbility(new BattleTrait(
                        "Strat: " + name,
                        "Stratagem-granted Fights First this round",
                        "speed", 0, "fights_first"));
                System.out.println(Color.c("     " + target.getName()
                        + " fights first this round!", Color.YELLOW));
                break;

            case SUSTAINED_HITS:
                target.addAbility(new BattleTrait(
                        "Strat: " + name,
                        "Stratagem-granted Sustained Hits " + modifier,
                        "attacks", modifier, "sustained_hits"));
                System.out.println(Color.c("     " + target.getName()
                        + " gains Sustained Hits " + modifier + "!", Color.YELLOW));
                break;

            case DEVASTATING:
                target.addAbility(new BattleTrait(
                        "Strat: " + name,
                        "Stratagem-granted Devastating Wounds",
                        "attacks", 1, "devastating_wounds"));
                System.out.println(Color.c("     " + target.getName()
                        + " gains Devastating Wounds this round!", Color.YELLOW));
                break;

            case HEAL:
                // Restore up to modifier wounds; StatBlock has no max-wounds ceiling
                // so we simply add the value (works correctly for any wound pool)
                target.getStats().setWounds(target.getStats().getWounds() + modifier);
                System.out.println(Color.c("     " + target.getName()
                        + " recovers " + modifier + " wound(s)! (now W:"
                        + target.getStats().getWounds() + ")", Color.YELLOW));
                break;

            case EXTRA_ATTACKS:
                // Represent bonus attacks as Sustained Hits so they flow through
                // the existing CombatEngine pipeline without new engine code
                target.addAbility(new BattleTrait(
                        "Strat: " + name,
                        "Stratagem-granted +" + modifier + " extra attacks (Sustained Hits)",
                        "attacks", modifier, "sustained_hits"));
                System.out.println(Color.c("     " + target.getName()
                        + " gains +" + modifier + " extra attacks this round!", Color.YELLOW));
                break;
        }
    }

    // -------------------------------------------------------------------------
    // State Management
    // -------------------------------------------------------------------------

    /**
     * Resets the used-this-combat flag so this Stratagem is available again
     * for the next engagement. Call between combat encounters.
     */
    public void resetForNewCombat() {
        usedThisCombat = false;
    }

    /**
     * Returns true if this Stratagem has already been activated this combat.
     * One-use per engagement enforces 10th edition limits.
     *
     * @return true if already used
     */
    public boolean isUsedThisCombat() {
        return usedThisCombat;
    }

    // -------------------------------------------------------------------------
    // Scope Check
    // -------------------------------------------------------------------------

    /**
     * Returns true if the given unit is a valid target according to this
     * Stratagem's TargetScope.
     *
     * @param u Unit to test
     * @return true if targeting rules are satisfied
     */
    public boolean isValidTarget(Unit u) {
        switch (targetScope) {
            case ANY_FRIENDLY:   return true;
            case INFANTRY_ONLY:  return (u instanceof Infantry) || (u instanceof Psyker);
            case VEHICLE_ONLY:   return (u instanceof Vehicle) || (u instanceof Knight);
            case PSYKER_ONLY:    return (u instanceof Psyker);
            default:             return true;
        }
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /** @return Stratagem display name */
    public String getName()           { return name; }

    /** @return Flavour / rules description */
    public String getDescription()    { return description; }

    /** @return CP cost to activate */
    public int getCost()              { return cost; }

    /** @return Effect type enum */
    public EffectType getEffectType() { return effectType; }

    /** @return Target scope enum */
    public TargetScope getTargetScope() { return targetScope; }

    /** @return Numeric modifier for the effect */
    public int getModifier()          { return modifier; }
}

/*
 * Change Log:
 *   2026-04-09  Shane Potts  Initial creation — Phase 4 Detachment/CP system;
 *                             EffectType enum (FEEL_NO_PAIN, INVULN_BOOST,
 *                             FIGHTS_FIRST, SUSTAINED_HITS, DEVASTATING, HEAL,
 *                             EXTRA_ATTACKS); TargetScope enum (ANY_FRIENDLY,
 *                             INFANTRY_ONLY, VEHICLE_ONLY, PSYKER_ONLY);
 *                             applyEffect() injects temporary BattleTrait with
 *                             appropriate trigger into target unit's ability list,
 *                             reusing existing CombatEngine trigger pipeline;
 *                             HEAL directly restores wounds; usedThisCombat flag
 *                             enforces one-use-per-engagement limit;
 *                             isValidTarget() uses instanceof to scope targets
 *   2026-04-26  Shane Potts  Phase 4 complete - no further changes this phase
 */
