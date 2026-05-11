/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Concrete Ability subclass representing passive or activated
 *              combat traits. BattleTrait does NOT implement Castable - it
 *              is not a psychic power and requires no warp charge. When
 *              activated, a BattleTrait grants a temporary flat modifier to
 *              one of the target's StatBlock stats for the current engagement.
 * Inputs:      abilityName, description (String), statTarget (String),
 *              modifier (int)
 * Outputs:     Stat modifier applied to target's StatBlock; console narrative
 *
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 2; concrete Ability subclass
 *                             with statTarget, modifier, and trigger fields;
 *                             BattleTrait does NOT implement Castable — no warp
 *                             charge required; activate() switches on statTarget
 *                             to apply flat modifier to the target's StatBlock
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-09  Shane Potts  Added trigger field and overloaded constructor so CombatEngine
 *                             can identify which combat hook to invoke for each trait
 *                             (fights_first, invuln_save, inner_circle, feel_no_pain,
 *                             sustained_hits, lethal_hits, devastating_wounds)
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; trigger field read by
 *                             CombatEngine, SaveManager, GenestealerCultFactory, and
 *                             Stratagem.applyEffect() throughout the full system
 */

package bolterandburden;

public class BattleTrait extends Ability {

    /**
     * Which stat this trait modifies. Valid values: "attacks", "speed",
     * "toughness", "save", "wounds".
     */
    private String statTarget;
    private int    modifier;

    /**
     * When this trait's mechanical effect fires. CombatEngine checks this
     * field to know which hook to invoke. Standard values:
     * <ul>
     *   <li>"passive"          — no combat hook; stored as data/flavour only</li>
     *   <li>"fights_first"     — unit acts before all non-Fights-First units</li>
     *   <li>"invuln_save"      — modifier = invuln save threshold (e.g. 4 = 4++)</li>
     *   <li>"inner_circle"     — modifier = damage reduction per attack</li>
     *   <li>"feel_no_pain"     — modifier = FNP threshold (e.g. 4 = 4+)</li>
     *   <li>"sustained_hits"   — modifier = extra hits per natural 6 to hit</li>
     *   <li>"lethal_hits"      — natural 6s to hit auto-wound (modifier unused)</li>
     *   <li>"devastating_wounds" — natural 6s to wound bypass saves (mortal wound)</li>
     * </ul>
     */
    private String trigger;

    /**
     * Constructs a BattleTrait with no combat trigger (passive flavour/data).
     *
     * @param abilityName Name of the trait (e.g., "Veteran's Instincts")
     * @param description Narrative description
     * @param statTarget  The stat to modify: "attacks", "speed", "toughness",
     *                    "save", or "wounds"
     * @param modifier    The flat amount added to the stat (may be negative)
     */
    public BattleTrait(String abilityName, String description,
                       String statTarget, int modifier) {
        this(abilityName, description, statTarget, modifier, "passive");
    }

    /**
     * Constructs a BattleTrait with an explicit combat trigger.
     *
     * @param abilityName Name of the trait
     * @param description Narrative description
     * @param statTarget  The stat associated with this trait
     * @param modifier    Numeric value used by the trigger (e.g. save threshold)
     * @param trigger     Combat hook identifier (see field Javadoc)
     */
    public BattleTrait(String abilityName, String description,
                       String statTarget, int modifier, String trigger) {
        super(abilityName, description);
        this.statTarget = statTarget;
        this.modifier   = modifier;
        this.trigger    = trigger;
    }

    // -------------------------------------------------------------------------
    // Ability Abstract Method
    // -------------------------------------------------------------------------

    /**
     * Activates this trait on the target, applying the stat modifier to the
     * appropriate field in the target's StatBlock.
     *
     * @param target The unit receiving the trait's benefit
     */
    @Override
    public void activate(Unit target) {
        StatBlock stats = target.getStats();
        System.out.println("  [" + getAbilityName() + "] activates on " + target.getName()
                + " - " + statTarget + " " + (modifier >= 0 ? "+" : "") + modifier);

        switch (statTarget.toLowerCase()) {
            case "attacks":
                stats.setAttacks(stats.getAttacks() + modifier);
                System.out.println("    " + target.getName() + " attacks: "
                        + (stats.getAttacks() - modifier) + " -> " + stats.getAttacks());
                break;
            case "speed":
                stats.setSpeed(stats.getSpeed() + modifier);
                System.out.println("    " + target.getName() + " speed: "
                        + (stats.getSpeed() - modifier) + " -> " + stats.getSpeed());
                break;
            case "toughness":
                stats.setToughness(stats.getToughness() + modifier);
                System.out.println("    " + target.getName() + " toughness: "
                        + (stats.getToughness() - modifier) + " -> " + stats.getToughness());
                break;
            case "save":
                stats.setSave(stats.getSave() + modifier);
                System.out.println("    " + target.getName() + " save: "
                        + (stats.getSave() - modifier) + "+ -> " + stats.getSave() + "+");
                break;
            case "wounds":
                stats.setWounds(stats.getWounds() + modifier);
                System.out.println("    " + target.getName() + " wounds: "
                        + (stats.getWounds() - modifier) + " -> " + stats.getWounds());
                break;
            default:
                System.out.println("    Unknown stat target: " + statTarget);
        }
    }

    // --- Getters / Setters ---

    public String getStatTarget() { return statTarget; }
    public int    getModifier()   { return modifier; }
    public String getTrigger()    { return trigger; }

    public void setStatTarget(String statTarget) { this.statTarget = statTarget; }
    public void setModifier(int modifier)        { this.modifier   = modifier; }
    public void setTrigger(String trigger)       { this.trigger    = trigger; }
}

/*
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 2; concrete Ability subclass
 *                             with statTarget, modifier, and trigger fields;
 *                             BattleTrait does NOT implement Castable
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-09  Shane Potts  Added trigger field and overloaded constructor so CombatEngine
 *                             can identify which combat hook to invoke for each trait
 *                             (fights_first, invuln_save, inner_circle, feel_no_pain,
 *                             sustained_hits, lethal_hits, devastating_wounds)
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; trigger field read by
 *                             CombatEngine, SaveManager, GenestealerCultFactory, and
 *                             Stratagem.applyEffect() throughout the full system
 */
