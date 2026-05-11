/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Holds a unit's equipment list and flat stat modifiers granted
 *              by that equipment. Composed into Unit as a private field.
 * Inputs:      Equipment names (String) and stat modifier values (int) added
 *              via methods after construction
 * Outputs:     Effective stat values via getEffective*() methods;
 *              equipment summary via toString()
 *
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 1; holds ArrayList<String>
 *                             equipment list and int attackModifier; addEquipment(),
 *                             removeEquipment(), getEffectiveAttacks(); composed into
 *                             Unit and populated by FactionFactory and GenestealerCultFactory
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; SaveManager
 *                             serializes equipment list and attackModifier
 */

package bolterandburden;

import java.util.ArrayList;

public class Wargear {

    private ArrayList<String> equipmentList;
    private int attackModifier;

    /**
     * Constructs an empty Wargear object with no equipment and zero modifiers.
     */
    public Wargear() {
        equipmentList  = new ArrayList<>();
        attackModifier = 0;
    }

    /**
     * Adds a named piece of equipment to this unit's gear list.
     *
     * @param item Name of the equipment (e.g., "Bolter", "Chainsword")
     */
    public void addEquipment(String item) {
        equipmentList.add(item);
    }

    /**
     * Removes a named piece of equipment from this unit's gear list.
     * Does nothing if the item is not present.
     *
     * @param item Name of the equipment to remove
     */
    public void removeEquipment(String item) {
        equipmentList.remove(item);
    }

    /**
     * Returns the effective attack count after applying this wargear's modifier.
     *
     * @param base The unit's base attack stat from StatBlock
     * @return base + attackModifier
     */
    public int getEffectiveAttacks(int base) { return base + attackModifier; }

    // --- Getters ---

    public ArrayList<String> getEquipmentList() { return equipmentList; }
    public int getAttackModifier()              { return attackModifier; }

    // --- Setters ---

    public void setAttackModifier(int attackModifier) { this.attackModifier = attackModifier; }

    /**
     * Returns a formatted summary of all equipment and any active modifiers.
     *
     * @return Comma-separated equipment list followed by modifier values
     */
    @Override
    public String toString() {
        String gear = equipmentList.isEmpty() ? "None" : String.join(", ", equipmentList);
        return String.format("Gear: [%s]  Mods(A:%+d)", gear, attackModifier);
    }
}

/*
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 1; holds ArrayList<String>
 *                             equipment list and int attackModifier; addEquipment(),
 *                             removeEquipment(), getEffectiveAttacks(); composed into
 *                             Unit and populated by FactionFactory and GenestealerCultFactory
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; SaveManager
 *                             serializes equipment list and attackModifier
 */
