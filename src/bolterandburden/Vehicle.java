/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Concrete Unit subclass representing armored vehicles such as
 *              Rhino transports and Land Raiders. Tracks vehicle type and a
 *              separate hull points pool in addition to the inherited wound stat.
 * Inputs:      Inherits Unit constructor args; additionally requires vehicleType
 *              (String) and hullPoints (int)
 * Outputs:     Turn actions and damage results printed to console;
 *              formatted stat block via toString()
 *
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 1; Vehicle extends Unit
 *                             with vehicleType and hullPoints fields; stub armor
 *                             check deflects 1 damage; hullPoints synced to
 *                             wounds stat after each hit; vehicleOnly flag in
 *                             Detachment restricts stratagem targets to this type
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes
 */

package bolterandburden;

public class Vehicle extends Unit {

    private String vehicleType;
    private int hullPoints;

    /**
     * Constructs a Vehicle unit with all required fields.
     *
     * @param name        Unique name for this vehicle (e.g., "Iron Wrath")
     * @param faction     Faction name this vehicle belongs to
     * @param stats       Combat StatBlock for this vehicle
     * @param wargear     Weapon systems and equipment mounted on this vehicle
     * @param vehicleType Classification of the vehicle (e.g., "Battle Tank")
     * @param hullPoints  Starting hull integrity; separate from wound stat
     */
    public Vehicle(String name, String faction, StatBlock stats,
                   Wargear wargear, String vehicleType, int hullPoints) {
        super(name, faction, stats, wargear);
        this.vehicleType = vehicleType;
        this.hullPoints  = hullPoints;
    }

    /**
     * Executes this vehicle's turn: moves and fires its primary weapon system.
     */
    @Override
    public void takeTurn() {
        System.out.println(getName() + " [" + vehicleType + "] thunders forward and fires the "
                + getWargear().getEquipmentList().get(0) + "!");
    }

    /**
     * Applies damage to this vehicle. Performs a stub armor check before
     * reducing hull points, then syncs wounds to hull points for consistency.
     *
     * @param amount Number of damage points incoming before armor mitigation
     */
    @Override
    public void applyDamage(int amount) {
        // Stub: armor check will use dice rolls in Phase 2
        int mitigated = Math.max(0, amount - 1);
        System.out.println(getName() + "'s armor deflects 1 damage. Taking " + mitigated + " hull damage.");
        hullPoints -= mitigated;
        getStats().setWounds(hullPoints);
        System.out.println(Color.c(getName() + " hull integrity: " + Math.max(0, hullPoints), Color.RED));
        if (isDestroyed()) {
            System.out.println(Color.c(getName() + " is DESTROYED - wreckage immobilized!", Color.BOLD + Color.RED));
        }
    }

    /**
     * Returns a formatted stat block string for this vehicle.
     *
     * @return Multi-line string with all vehicle details
     */
    @Override
    public String toString() {
        return "=== Vehicle: " + getName() + " ===\n"
                + "  Faction   : " + getFaction() + "\n"
                + "  Type      : " + vehicleType + "\n"
                + "  Hull Pts  : " + hullPoints + "\n"
                + "  Stats     : " + getStats() + "\n"
                + "  " + getWargear() + "\n"
                + "  XP: " + getExperiencePoints()
                + "  Morale: " + getMoraleLevel()
                + "  Corruption: " + getCorruptionLevel();
    }

    // --- Getters ---

    public String getVehicleType() { return vehicleType; }
    public int getHullPoints()     { return hullPoints; }

    // --- Setters ---

    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public void setHullPoints(int hullPoints)      { this.hullPoints  = hullPoints; }
}

/*
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 1; Vehicle extends Unit
 *                             with vehicleType and hullPoints fields; stub armor
 *                             check deflects 1 damage; hullPoints synced to
 *                             wounds stat after each hit; vehicleOnly flag in
 *                             Detachment restricts stratagem targets to this type
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes
 */
