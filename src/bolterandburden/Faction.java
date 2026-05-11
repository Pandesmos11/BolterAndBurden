/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Represents one of the three playable factions. Stores the
 *              faction's name, chaos alignment flag, and the list of unit
 *              type names that faction may field. Used by Warband during
 *              warband construction and by FactionFactory.
 * Inputs:      factionName (String), isChaosAligned (boolean) via constructor;
 *              unit type names added via addUnitType()
 * Outputs:     Faction info via getters and toString()
 *
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 1; factionName, isChaosAligned,
 *                             availableUnitTypes; isChaosAligned gates corruption
 *                             consequences in Phase 2 combat
 *   2026-04-08  Shane Potts  Restored availableUnitTypes population — required by
 *                             project proposal Phase 1 deliverable spec
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; isChaosAligned
 *                             read by SaveManager for save header and by
 *                             FactionFactory during warband construction
 */

package bolterandburden;

import java.util.ArrayList;

public class Faction {

    private String factionName;
    private boolean isChaosAligned;
    private ArrayList<String> availableUnitTypes;

    /**
     * Constructs a Faction with a name and chaos alignment flag.
     *
     * @param factionName     Display name of the faction (e.g., "Dark Angels")
     * @param isChaosAligned  true if this faction serves Chaos, false otherwise
     */
    public Faction(String factionName, boolean isChaosAligned) {
        this.factionName        = factionName;
        this.isChaosAligned     = isChaosAligned;
        this.availableUnitTypes = new ArrayList<>();
    }

    /**
     * Adds a unit type name to this faction's available roster pool.
     *
     * @param type Unit type name (e.g., "Infantry", "Knight", "Psyker")
     */
    public void addUnitType(String type) {
        availableUnitTypes.add(type);
    }

    /**
     * Checks whether a given unit type is available in this faction.
     *
     * @param type Unit type name to look up
     * @return true if the faction can field this unit type, false otherwise
     */
    public boolean isUnitTypeAvailable(String type) {
        return availableUnitTypes.contains(type);
    }

    // --- Getters ---

    public String getFactionName()                    { return factionName; }
    public boolean isChaosAligned()                   { return isChaosAligned; }
    public ArrayList<String> getAvailableUnitTypes()  { return availableUnitTypes; }

    // --- Setters ---

    public void setFactionName(String factionName)      { this.factionName    = factionName; }
    public void setChaosAligned(boolean isChaosAligned) { this.isChaosAligned = isChaosAligned; }

    /**
     * Returns a formatted summary of the faction.
     *
     * @return Faction name, alignment status, and available unit types
     */
    @Override
    public String toString() {
        String alignment = isChaosAligned ? "Chaos-Aligned" : "Loyalist";
        String units = availableUnitTypes.isEmpty() ? "None" : String.join(", ", availableUnitTypes);
        return String.format("Faction: %s  [%s]  Units: %s", factionName, alignment, units);
    }
}
