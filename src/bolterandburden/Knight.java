/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Concrete Unit subclass representing Chaos Knights and Armiger-
 *              class war engines. The most durable unit type in the game.
 *              Tracks a knightTitle and a pilot bond flag that affects
 *              damage mitigation behavior.
 * Inputs:      Inherits Unit constructor args; additionally requires knightTitle
 *              (String) and isPilotBound (boolean)
 * Outputs:     Turn actions and damage results printed to console;
 *              formatted stat block via toString()
 *
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 1; Knight extends Unit
 *                             with knightTitle and isPilotBound fields; soul-bound
 *                             pilots grant a stub ion shield damage reduction;
 *                             Chaos Knights faction uses only this unit type
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes
 */

package bolterandburden;

public class Knight extends Unit {

    private String knightTitle;
    private boolean isPilotBound;

    /**
     * Constructs a Knight unit with all required fields.
     *
     * @param name         Unique name for this war engine (e.g., "Tyrant of Ruin")
     * @param faction      Faction name this knight belongs to
     * @param stats        Combat StatBlock for this knight
     * @param wargear      Weapons and systems mounted on this knight
     * @param knightTitle  Title or class of knight (e.g., "Despoiler", "Abominant")
     * @param isPilotBound true if the pilot is soul-bound to the machine
     */
    public Knight(String name, String faction, StatBlock stats,
                  Wargear wargear, String knightTitle, boolean isPilotBound) {
        super(name, faction, stats, wargear);
        this.knightTitle  = knightTitle;
        this.isPilotBound = isPilotBound;
    }

    /**
     * Executes this knight's turn: stomps forward, sweeping aside infantry
     * before bringing its main weapon to bear.
     */
    @Override
    public void takeTurn() {
        String bound = isPilotBound ? "the bound pilot screaming through the neural link - " : "";
        System.out.println(getName() + " [" + knightTitle + "] ADVANCES! " + bound
                + "Titanic feet crush the earth as the "
                + getWargear().getEquipmentList().get(0) + " tracks a target!");
    }

    /**
     * Applies damage to this knight. If the pilot is soul-bound, an ion shield
     * stub roll reduces incoming damage; otherwise damage is applied directly.
     *
     * @param amount Number of damage points incoming
     */
    @Override
    public void applyDamage(int amount) {
        int finalDamage = amount;
        if (isPilotBound) {
            // Stub: ion shield roll - Phase 2 will use actual dice logic
            finalDamage = Math.max(0, amount - 2);
            System.out.println(getName() + "'s ion shield flares! "
                    + (amount - finalDamage) + " damage deflected.");
        }
        int current = getStats().getWounds();
        getStats().setWounds(current - finalDamage);
        System.out.println(Color.c(getName() + " takes " + finalDamage + " damage through the shields. "
                + "Wounds remaining: " + Math.max(0, getStats().getWounds()), Color.RED));
        if (isDestroyed()) {
            System.out.println(Color.c(getName() + " FALLS! The war engine crashes to the ground in a catastrophic explosion!", Color.BOLD + Color.RED));
        }
    }

    /**
     * Returns a formatted stat block string for this knight.
     *
     * @return Multi-line string with all knight details
     */
    @Override
    public String toString() {
        String bondTag = isPilotBound ? " [SOUL-BOUND]" : "";
        return "=== Knight: " + getName() + bondTag + " ===\n"
                + "  Faction   : " + getFaction() + "\n"
                + "  Title     : " + knightTitle + "\n"
                + "  Stats     : " + getStats() + "\n"
                + "  " + getWargear() + "\n"
                + "  XP: " + getExperiencePoints()
                + "  Morale: " + getMoraleLevel()
                + "  Corruption: " + getCorruptionLevel();
    }

    // --- Getters ---

    public String getKnightTitle()  { return knightTitle; }
    public boolean isPilotBound()   { return isPilotBound; }

    // --- Setters ---

    public void setKnightTitle(String knightTitle)    { this.knightTitle  = knightTitle; }
    public void setPilotBound(boolean isPilotBound)   { this.isPilotBound = isPilotBound; }
}

/*
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 1; Knight extends Unit
 *                             with knightTitle and isPilotBound fields; soul-bound
 *                             pilots grant a stub ion shield damage reduction;
 *                             Chaos Knights faction uses only this unit type
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes
 */
