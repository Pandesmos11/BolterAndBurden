/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Abstract base class for all unit abilities. Provides a common
 *              type for the ArrayList<Ability> held in Unit. Concrete subclasses
 *              (PsychicPower, SorceryRitual, BattleTrait) and the Castable
 *              interface are implemented in Phase 2.
 * Inputs:      abilityName and description (String) via constructor
 * Outputs:     Ability name and description via getters; summary via toString()
 *
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 1 stub; abstract Ability base
 *                             class with abilityName, description, abstract activate();
 *                             provides common ArrayList<Ability> element type for Unit
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; subclassed by
 *                             PsychicPower, SorceryRitual, and BattleTrait throughout all phases
 */

package bolterandburden;

public abstract class Ability {

    private String abilityName;
    private String description;

    /**
     * Constructs an Ability with a name and description.
     *
     * @param abilityName Short name of the ability (e.g., "Smite")
     * @param description Narrative or mechanical description of the ability
     */
    public Ability(String abilityName, String description) {
        this.abilityName = abilityName;
        this.description = description;
    }

    /**
     * Activates this ability, applying its effect to the target unit.
     * Full implementation deferred to Phase 2.
     *
     * @param target The unit this ability is directed at
     */
    public abstract void activate(Unit target);

    // --- Getters ---

    public String getAbilityName() { return abilityName; }
    public String getDescription() { return description; }

    // --- Setters ---

    public void setAbilityName(String abilityName) { this.abilityName = abilityName; }
    public void setDescription(String description) { this.description = description; }

    /**
     * Returns the ability name and description as a formatted string.
     *
     * @return "AbilityName: description"
     */
    @Override
    public String toString() {
        return abilityName + ": " + description;
    }
}

/*
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 1 stub; abstract Ability base
 *                             class with abilityName, description, abstract activate();
 *                             provides common ArrayList<Ability> element type for Unit
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; subclassed by
 *                             PsychicPower, SorceryRitual, and BattleTrait throughout all phases
 */
