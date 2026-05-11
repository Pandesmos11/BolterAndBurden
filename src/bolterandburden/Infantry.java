/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Concrete Unit subclass representing standard foot soldiers.
 *              Used by all three factions: Chaos Knights cultists, Emperor's
 *              Children marines, and Dark Angels battle-brothers. Tracks a
 *              squad role and veteran status in addition to inherited Unit state.
 * Inputs:      Inherits Unit constructor args; additionally requires squadRole
 *              (String) and isVeteran (boolean)
 * Outputs:     Turn actions and damage results printed to console;
 *              formatted stat block via toString()
 *
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 1; Infantry extends Unit
 *                             with squadRole and isVeteran fields; veteran units
 *                             shrug the first damage point with a resilience message;
 *                             used by all three factions in FactionFactory
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes
 */

package bolterandburden;

public class Infantry extends Unit {

    private String squadRole;
    private boolean isVeteran;

    /**
     * Constructs an Infantry unit with all required fields.
     *
     * @param name      Unique name for this soldier (e.g., "Brother Ezekiel")
     * @param faction   Faction name this unit belongs to
     * @param stats     Combat StatBlock for this unit
     * @param wargear   Equipment carried by this unit
     * @param squadRole Role within the squad (e.g., "Tactical", "Berserker")
     * @param isVeteran true if this soldier has veteran status
     */
    public Infantry(String name, String faction, StatBlock stats,
                    Wargear wargear, String squadRole, boolean isVeteran) {
        super(name, faction, stats, wargear);
        this.squadRole = squadRole;
        this.isVeteran = isVeteran;
    }

    /**
     * Executes this infantry unit's turn: moves up and fires ranged weapons,
     * or charges into melee based on squad role.
     */
    @Override
    public void takeTurn() {
        String prefix = isVeteran ? "[VETERAN] " : "";
        System.out.println(prefix + getName() + " (" + squadRole + ") advances and opens fire with "
                + getWargear().getEquipmentList().get(0) + "!");
    }

    /**
     * Applies damage to this infantry unit. Veterans shrug off the first
     * point of damage with a narrative resilience message; all damage
     * ultimately reduces wounds in the StatBlock.
     *
     * @param amount Number of damage points to apply
     */
    @Override
    public void applyDamage(int amount) {
        if (isVeteran && amount > 0) {
            System.out.println(getName() + " grits their teeth and takes the hit!");
        }
        int current = getStats().getWounds();
        getStats().setWounds(current - amount);
        System.out.println(Color.c(getName() + " takes " + amount + " damage. Wounds remaining: "
                + Math.max(0, getStats().getWounds()), Color.RED));
        if (isDestroyed()) {
            System.out.println(Color.c(getName() + " has fallen!", Color.BOLD + Color.RED));
        }
    }

    /**
     * Returns a formatted stat block string for this infantry unit.
     *
     * @return Multi-line string with all unit details
     */
    @Override
    public String toString() {
        String veteranTag = isVeteran ? " [VETERAN]" : "";
        return "=== Infantry: " + getName() + veteranTag + " ===\n"
                + "  Faction  : " + getFaction() + "\n"
                + "  Role     : " + squadRole + "\n"
                + "  Stats    : " + getStats() + "\n"
                + "  " + getWargear() + "\n"
                + "  XP: " + getExperiencePoints()
                + "  Morale: " + getMoraleLevel()
                + "  Corruption: " + getCorruptionLevel();
    }

    // --- Getters ---

    public String getSquadRole() { return squadRole; }
    public boolean isVeteran()   { return isVeteran; }

    // --- Setters ---

    public void setSquadRole(String squadRole) { this.squadRole = squadRole; }
    public void setVeteran(boolean isVeteran)  { this.isVeteran = isVeteran; }
}

/*
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 1; Infantry extends Unit
 *                             with squadRole and isVeteran fields; veteran units
 *                             shrug the first damage point with a resilience message;
 *                             used by all three factions in FactionFactory
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes
 */
