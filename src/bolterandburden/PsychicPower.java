/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Concrete Ability subclass for warp-fueled psychic powers.
 *              Implements the Castable interface. The core feature of this
 *              class is resolve(), a recursive method that chains secondary
 *              triggered effects up to a maximum depth of MAX_CHAIN_DEPTH.
 *              The base case terminates recursion when the chain depth limit
 *              is reached OR when no further chain triggers exist.
 * Inputs:      abilityName, description (String), damageValue (int), and
 *              an optional list of chain-trigger PsychicPower objects
 * Outputs:     Damage applied to target, console output per chain step
 *
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 2; implements Castable;
 *                             MAX_CHAIN_DEPTH = 3; resolve(caster, target, depth)
 *                             is the recursive entry point; base cases: depth >=
 *                             MAX_CHAIN_DEPTH or chainTriggers.isEmpty();
 *                             addChainTrigger() builds the trigger list
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; used by Psyker
 *                             onRankUp() to grant Warp Bolts at rank 2
 */

package bolterandburden;

import java.util.ArrayList;

public class PsychicPower extends Ability implements Castable {

    /** Maximum recursion depth for psychic chain resolution. */
    public static final int MAX_CHAIN_DEPTH = 3;

    private int damageValue;
    private ArrayList<Ability> chainTriggers;

    /**
     * Constructs a PsychicPower with damage value and an empty trigger list.
     *
     * @param abilityName Name of the power (e.g., "Smite", "Warp Bolts")
     * @param description Narrative description
     * @param damageValue Flat damage this power inflicts when it resolves
     */
    public PsychicPower(String abilityName, String description, int damageValue) {
        super(abilityName, description);
        this.damageValue   = damageValue;
        this.chainTriggers = new ArrayList<>();
    }

    /**
     * Adds a secondary Ability to this power's chain trigger list.
     * When this power resolves recursively, each trigger in the list is
     * resolved at the next depth level.
     *
     * @param trigger An Ability to chain after this power resolves
     */
    public void addChainTrigger(Ability trigger) {
        chainTriggers.add(trigger);
    }

    // -------------------------------------------------------------------------
    // Castable Implementation
    // -------------------------------------------------------------------------

    /**
     * Entry point for casting this power. Prints the cast event and starts
     * recursive chain resolution at depth 0.
     *
     * @param caster The psyker manifesting this power
     * @param target The unit the power is directed at
     */
    @Override
    public void cast(Unit caster, Unit target) {
        System.out.println("  " + caster.getName() + " manifests ["
                + getAbilityName() + "]! The warp tears open...");
        resolve(caster, target, 0);
    }

    /**
     * Returns this power's chain trigger list for inspection by CombatEngine.
     *
     * @return List of chain-triggered Ability objects
     */
    @Override
    public ArrayList<Ability> getChainTriggers() {
        return chainTriggers;
    }

    // -------------------------------------------------------------------------
    // Recursive Chain Resolution
    // -------------------------------------------------------------------------

    /**
     * Recursively resolves this psychic power and any triggered secondary
     * effects. Each call increments the depth counter. Recursion terminates
     * when depth reaches MAX_CHAIN_DEPTH or when the chain trigger list is
     * empty (whichever comes first).
     *
     * @param caster The unit that originally manifested the power
     * @param target The unit receiving the chain's effects
     * @param depth  Current recursion depth (caller passes 0 for initial call)
     */
    public void resolve(Unit caster, Unit target, int depth) {
        String indent = "  ".repeat(depth + 2);

        // --- Base Case ---
        if (depth >= MAX_CHAIN_DEPTH) {
            System.out.println(indent + "[depth " + depth + "] Chain depth limit reached - "
                    + "warp energy dissipates.");
            return;
        }
        if (chainTriggers.isEmpty()) {
            System.out.println(indent + "[depth " + depth + "] " + getAbilityName()
                    + " resolves - no further triggers.");
            target.applyDamage(damageValue);
            return;
        }

        // --- Recursive Case ---
        System.out.println(indent + "[depth " + depth + "] " + getAbilityName()
                + " strikes " + target.getName() + " for " + damageValue + " damage.");
        target.applyDamage(damageValue);

        for (Ability trigger : chainTriggers) {
            if (trigger instanceof PsychicPower) {
                System.out.println(indent + "  --> Chain trigger: [" + trigger.getAbilityName() + "]");
                ((PsychicPower) trigger).resolve(caster, target, depth + 1);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Ability Abstract Method
    // -------------------------------------------------------------------------

    /**
     * Standalone activation (used outside of full Castable context).
     * Resolves at depth 0 with no caster reference in output.
     *
     * @param target The unit this power is directed at
     */
    @Override
    public void activate(Unit target) {
        System.out.println("  [" + getAbilityName() + "] activates on " + target.getName());
        target.applyDamage(damageValue);
    }

    // --- Getters / Setters ---

    public int getDamageValue()    { return damageValue; }
    public void setDamageValue(int damageValue) { this.damageValue = damageValue; }
}

/*
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 2; implements Castable;
 *                             MAX_CHAIN_DEPTH = 3; resolve(caster, target, depth)
 *                             is the recursive entry point; base cases: depth >=
 *                             MAX_CHAIN_DEPTH or chainTriggers.isEmpty();
 *                             addChainTrigger() builds the trigger list
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; used by Psyker
 *                             onRankUp() to grant Warp Bolts at rank 2
 */
