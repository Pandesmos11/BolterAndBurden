/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Concrete Ability subclass for Chaos sorcery rituals. Implements
 *              Castable but uses a single-step resolution with no recursion.
 *              Rituals deal damage and optionally apply a corruption penalty
 *              to the caster (price of sorcerous power). Used primarily by
 *              Emperor's Children Sorcerers and Chaos-aligned Psykers.
 * Inputs:      abilityName, description (String), damageValue (int),
 *              casterCorruptionCost (int)
 * Outputs:     Damage applied to target; corruption applied to caster;
 *              narrative console output
 *
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 2; implements Castable with
 *                             single-step resolution (no recursion); damageValue
 *                             deals flat damage; casterCorruptionCost accrues
 *                             corruption on the caster each use; getChainTriggers()
 *                             returns an empty list (rituals never chain)
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes
 */

package bolterandburden;

import java.util.ArrayList;

public class SorceryRitual extends Ability implements Castable {

    private int damageValue;
    private int casterCorruptionCost;
    private ArrayList<Ability> chainTriggers;

    /**
     * Constructs a SorceryRitual with damage output and a corruption cost.
     *
     * @param abilityName           Name of the ritual (e.g., "Cacophonic Choir")
     * @param description           Narrative description
     * @param damageValue           Flat damage dealt to target on resolution
     * @param casterCorruptionCost  Corruption points the caster accrues per use
     */
    public SorceryRitual(String abilityName, String description,
                         int damageValue, int casterCorruptionCost) {
        super(abilityName, description);
        this.damageValue          = damageValue;
        this.casterCorruptionCost = casterCorruptionCost;
        this.chainTriggers        = new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // Castable Implementation
    // -------------------------------------------------------------------------

    /**
     * Casts this ritual: narrates the invocation, deals damage to target,
     * and adds corruption to the caster as the price of dark sorcery.
     *
     * @param caster The sorcerer invoking the ritual
     * @param target The unit bearing the ritual's effect
     */
    @Override
    public void cast(Unit caster, Unit target) {
        System.out.println("  " + caster.getName() + " invokes the ritual ["
                + getAbilityName() + "]! Dark energies tear reality apart!");
        System.out.println("    --> " + target.getName() + " suffers " + damageValue + " damage.");
        target.applyDamage(damageValue);

        if (casterCorruptionCost > 0) {
            System.out.println("    --> " + caster.getName() + " pays the price: +"
                    + casterCorruptionCost + " corruption.");
            caster.gainCorruption(casterCorruptionCost);
        }
    }

    /**
     * Returns an empty trigger list - sorcery rituals do not chain.
     *
     * @return Empty ArrayList
     */
    @Override
    public ArrayList<Ability> getChainTriggers() {
        return chainTriggers;
    }

    // -------------------------------------------------------------------------
    // Ability Abstract Method
    // -------------------------------------------------------------------------

    /**
     * Standalone activation without a caster context. Applies damage only.
     *
     * @param target The unit this ritual targets
     */
    @Override
    public void activate(Unit target) {
        System.out.println("  [" + getAbilityName() + "] activates on "
                + target.getName() + " for " + damageValue + " damage.");
        target.applyDamage(damageValue);
    }

    // --- Getters / Setters ---

    public int getDamageValue()           { return damageValue; }
    public int getCasterCorruptionCost()  { return casterCorruptionCost; }

    public void setDamageValue(int damageValue)                   { this.damageValue          = damageValue; }
    public void setCasterCorruptionCost(int casterCorruptionCost) { this.casterCorruptionCost = casterCorruptionCost; }
}

/*
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 2; implements Castable with
 *                             single-step resolution (no recursion); damageValue
 *                             deals flat damage; casterCorruptionCost accrues
 *                             corruption on the caster each use; getChainTriggers()
 *                             returns an empty list (rituals never chain)
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes
 */
