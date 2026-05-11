/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Static utility class providing a recursive quicksort
 *              implementation for ranked unit display. Units are sorted
 *              in descending order by a caller-specified stat key so the
 *              highest-ranked unit always appears first. Valid stat keys
 *              are: "speed", "attacks", "toughness", "wounds", "save", "xp".
 *              The sort operates in-place on the provided ArrayList.
 *              No instances of UnitSorter are ever constructed - all methods
 *              are static.
 * Inputs:      ArrayList<Unit>, a stat key String, and low/high indices
 *              (passed internally during recursion); public entry point is
 *              sort(ArrayList<Unit>, String)
 * Outputs:     The ArrayList is sorted in-place; sorted display printed via
 *              displaySorted()
 *
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 3; static recursive quicksort
 *                             operating in-place on ArrayList<Unit>; sort() is the public
 *                             entry point; quickSort() and partition() are private
 *                             recursive helpers; getStat() maps stat key strings to
 *                             unit stat values; displaySorted() prints ranked results
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; sort(list, "xp")
 *                             called by Game.printFinalResults() for end-of-campaign
 *                             survivor ranking
 */

package bolterandburden;

import java.util.ArrayList;
import java.util.Collections;

public class UnitSorter {

    /** Private constructor - this class is not meant to be instantiated. */
    private UnitSorter() {}

    // =========================================================================
    // Public Entry Points
    // =========================================================================

    /**
     * Sorts a list of units in descending order of the specified stat using
     * recursive quicksort. The highest value appears at index 0.
     *
     * @param units   The list to sort in-place
     * @param statKey One of: "speed", "attacks", "toughness", "wounds",
     *                "save", "xp"
     */
    public static void sort(ArrayList<Unit> units, String statKey) {
        if (units == null || units.size() <= 1) return;
        quickSort(units, statKey, 0, units.size() - 1);
    }

    /**
     * Prints a ranked display of the sorted unit list, labelled by stat.
     *
     * @param units   List of units (should be pre-sorted)
     * @param statKey The stat used for ranking
     */
    public static void displaySorted(ArrayList<Unit> units, String statKey) {
        System.out.println("  Ranked by [" + statKey.toUpperCase() + "] (descending):");
        int rank = 1;
        for (Unit u : units) {
            System.out.println("  [" + rank + "] " + u.getName()
                    + "  " + statKey + ": " + getStat(u, statKey));
            rank++;
        }
    }

    // =========================================================================
    // Recursive Quicksort
    // =========================================================================

    /**
     * Recursive quicksort: sorts the sub-array units[low..high] in descending
     * order by the specified stat.
     *
     * <p><b>Base case:</b> {@code low >= high} - sub-array of length 0 or 1,
     * already sorted. Method returns immediately.
     *
     * <p><b>Recursive case:</b> partition the sub-array around a pivot (the
     * element at {@code high}). Then quickSort is called recursively on the
     * left partition {@code [low, pivotIndex - 1]} and right partition
     * {@code [pivotIndex + 1, high]}.
     *
     * @param units   The ArrayList being sorted in-place
     * @param statKey Stat key determining comparison order
     * @param low     Left boundary of the current partition (inclusive)
     * @param high    Right boundary of the current partition (inclusive)
     */
    public static void quickSort(ArrayList<Unit> units, String statKey, int low, int high) {
        // --- Base Case ---
        if (low >= high) {
            return;
        }

        // --- Recursive Case ---
        int pivotIndex = partition(units, statKey, low, high);
        quickSort(units, statKey, low, pivotIndex - 1);     // Sort left partition
        quickSort(units, statKey, pivotIndex + 1, high);    // Sort right partition
    }

    // =========================================================================
    // Partition Helper
    // =========================================================================

    /**
     * Partitions the sub-array around the pivot element (units[high]).
     * Elements with a stat value GREATER THAN OR EQUAL TO the pivot move to
     * the left (lower indices), achieving descending order.
     *
     * @param units   The ArrayList being partitioned
     * @param statKey Stat used for comparisons
     * @param low     Left boundary of the partition (inclusive)
     * @param high    Right boundary (pivot position, inclusive)
     * @return Index of the pivot element after partitioning
     */
    private static int partition(ArrayList<Unit> units, String statKey, int low, int high) {
        int pivot = getStat(units.get(high), statKey);
        int i = low - 1; // Pointer to the last "greater than pivot" element

        for (int j = low; j < high; j++) {
            if (getStat(units.get(j), statKey) >= pivot) { // Descending: >= puts larger left
                i++;
                Collections.swap(units, i, j);
            }
        }
        // Place pivot in its final sorted position
        Collections.swap(units, i + 1, high);
        return i + 1;
    }

    // =========================================================================
    // Stat Accessor
    // =========================================================================

    /**
     * Returns the integer value of the requested stat for the given unit.
     * Defaults to speed if an unknown key is provided.
     *
     * @param u       The unit to read stats from
     * @param statKey One of: "speed", "attacks", "toughness", "wounds",
     *                "save", "xp"
     * @return The integer value of the requested stat
     */
    private static int getStat(Unit u, String statKey) {
        switch (statKey.toLowerCase()) {
            case "attacks":   return u.getStats().getAttacks();
            case "toughness": return u.getStats().getToughness();
            case "wounds":    return u.getStats().getWounds();
            case "save":      return u.getStats().getSave();
            case "xp":        return u.getExperiencePoints();
            case "speed":
            default:          return u.getStats().getSpeed();
        }
    }
}

/*
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 3; static recursive quicksort
 *                             operating in-place on ArrayList<Unit>; sort() is the public
 *                             entry point; quickSort() and partition() are private
 *                             recursive helpers; getStat() maps stat key strings to
 *                             unit stat values; displaySorted() prints ranked results
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; sort(list, "xp")
 *                             called by Game.printFinalResults() for end-of-campaign
 *                             survivor ranking
 */
