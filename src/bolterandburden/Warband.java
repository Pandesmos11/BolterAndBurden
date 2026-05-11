/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Represents the player's collection of units. Wraps a custom
 *              generic LinkedList<Unit> and delegates all roster operations to
 *              it. Units are inserted in speed-sorted order so the roster
 *              always reflects initiative ordering.
 * Inputs:      warbandName (String) and faction (Faction) via constructor;
 *              Unit objects added via addUnit()
 * Outputs:     Roster summary printed via displayRoster(); unit count and
 *              name via toString()
 *
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 1; wraps LinkedList<Unit>;
 *                             addUnit() inserts in speed-sorted order; removeUnit(),
 *                             findUnit(), displayRoster(), getRosterSize()
 *   2026-03-23  Shane Potts  Phase 2 - added getLivingUnits() (filters destroyed
 *                             and routing) and isDefeated() for combat end detection
 *   2026-03-23  Shane Potts  Phase 3 - added getRosterSnapshot() for possession
 *                             consequence checks in CampaignEngine
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-08  Shane Potts  Added findFirstByType() binary search for roster lookup by unit type
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes
 */

package bolterandburden;

import java.util.ArrayList;

public class Warband {

    private String warbandName;
    private Faction faction;
    private LinkedList<Unit> roster;

    /**
     * Constructs a new empty Warband for the given faction.
     *
     * @param warbandName Display name for this warband
     * @param faction     The faction this warband belongs to
     */
    public Warband(String warbandName, Faction faction) {
        this.warbandName = warbandName;
        this.faction     = faction;
        this.roster      = new LinkedList<>();
    }

    /**
     * Adds a unit to the warband roster in initiative (speed-sorted) order.
     *
     * @param unit The unit to add
     */
    public void addUnit(Unit unit) {
        roster.insertSorted(unit);
    }

    /**
     * Removes a unit from the warband roster.
     *
     * @param unit The unit to remove
     * @return true if the unit was found and removed, false otherwise
     */
    public boolean removeUnit(Unit unit) {
        return roster.remove(unit);
    }

    /**
     * Searches the roster for a unit whose name contains the given string.
     *
     * @param name The name (or partial name) to search for
     * @return The matching Unit, or null if not found
     */
    public Unit findUnit(String name) {
        return roster.search(name);
    }

    /**
     * Returns the first unit in the roster whose concrete type (simple class
     * name) exactly matches typeName. Copies the roster to an ArrayList, sorts
     * by type name, then delegates to a recursive binary search.
     *
     * @param typeName Simple class name to search for (e.g. "Infantry", "Psyker")
     * @return First matching Unit, or null if no unit of that type exists
     */
    public Unit findFirstByType(String typeName) {
        ArrayList<Unit> sorted = roster.toArrayList();
        sorted.sort((a, b) -> a.getClass().getSimpleName()
                                .compareTo(b.getClass().getSimpleName()));
        return binarySearchByType(sorted, typeName, 0, sorted.size() - 1);
    }

    /**
     * Recursive binary search over a type-sorted ArrayList of Units.
     *
     * <p><b>Base case:</b> {@code low > high} — range is empty, type not found.
     *
     * <p><b>Recursive case:</b> compare the midpoint's type name to {@code typeName}.
     * Recurse left if target is alphabetically earlier, right if later.
     *
     * @param sorted   ArrayList sorted ascending by simple class name
     * @param typeName Target type name to find
     * @param low      Inclusive lower bound of current search range
     * @param high     Inclusive upper bound of current search range
     * @return A Unit whose type matches typeName, or null if not found
     */
    private Unit binarySearchByType(ArrayList<Unit> sorted, String typeName,
                                    int low, int high) {
        if (low > high) return null;
        int mid = (low + high) / 2;
        String midType = sorted.get(mid).getClass().getSimpleName();
        int cmp = midType.compareTo(typeName);
        if (cmp == 0)  return sorted.get(mid);
        if (cmp < 0)   return binarySearchByType(sorted, typeName, mid + 1, high);
        return binarySearchByType(sorted, typeName, low, mid - 1);
    }

    /**
     * Prints all units in the roster in their current sorted order.
     */
    public void displayRoster() {
        System.out.println("--- " + warbandName + " Roster (" + faction.getFactionName()
                + ") [" + roster.getSize() + " units] ---");
        roster.display();
    }

    /**
     * Returns the number of units currently in the roster.
     *
     * @return Current roster size
     */
    public int getRosterSize() {
        return roster.getSize();
    }

    /**
     * Returns an ArrayList of all units in the roster that have not yet been
     * destroyed (wounds > 0) and are not routing. Used by CombatEngine to
     * build the initiative queue each round.
     *
     * @return ArrayList of living, non-routing Unit objects
     */
    public ArrayList<Unit> getLivingUnits() {
        ArrayList<Unit> all = roster.toArrayList();
        ArrayList<Unit> living = new ArrayList<>();
        for (Unit u : all) {
            if (!u.isDestroyed() && !u.isRouting()) {
                living.add(u);
            }
        }
        return living;
    }

    /**
     * Returns true if every unit in the roster has been destroyed or is routing.
     * Used by CombatEngine to detect end-of-combat conditions.
     *
     * @return true if no living units remain
     */
    public boolean isDefeated() {
        return getLivingUnits().isEmpty();
    }

    /**
     * Returns an ArrayList of ALL units currently in the roster regardless of
     * health or routing state. Used by CampaignEngine.checkPossessionConsequences()
     * to iterate over every unit - including critically wounded ones - for
     * corruption state evaluation.
     *
     * @return ArrayList of all Unit objects in the roster
     */
    public ArrayList<Unit> getRosterSnapshot() {
        return roster.toArrayList();
    }

    // --- Getters ---

    public String getWarbandName() { return warbandName; }
    public Faction getFaction()    { return faction; }

    // --- Setters ---

    public void setWarbandName(String warbandName) { this.warbandName = warbandName; }
    public void setFaction(Faction faction)        { this.faction     = faction; }

    /**
     * Returns a brief summary of the warband.
     *
     * @return "WarbandName (FactionName) - N units"
     */
    @Override
    public String toString() {
        return warbandName + " (" + faction.getFactionName() + ") - "
                + roster.getSize() + " unit(s)";
    }
}

/*
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 1; wraps LinkedList<Unit>;
 *                             addUnit() inserts in speed-sorted order; removeUnit(),
 *                             findUnit(), displayRoster(), getRosterSize()
 *   2026-03-23  Shane Potts  Phase 2 - added getLivingUnits() (filters destroyed
 *                             and routing) and isDefeated() for combat end detection
 *   2026-03-23  Shane Potts  Phase 3 - added getRosterSnapshot() for possession
 *                             consequence checks in CampaignEngine
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-08  Shane Potts  Added findFirstByType() binary search for roster lookup by unit type
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes
 */
