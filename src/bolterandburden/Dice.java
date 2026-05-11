/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Static utility class providing D6 dice-rolling operations for
 *              combat resolution. Wraps a single Random instance so the RNG
 *              can be seeded via setSeed() for deterministic unit testing.
 *              No instances of Dice are ever constructed — all methods are static.
 * Inputs:      Number of dice, number of sides, and roll thresholds
 * Outputs:     Individual roll results (roll/d6) and success counts (rollHits)
 *
 * Change Log:
 *   2026-04-08  Shane Potts  Initial creation - Expansion phase; static dice utility
 *                             with seeded Random for deterministic tests; d6(), roll(),
 *                             rollHits(count, threshold) returning success count, and
 *                             rollWithSixes(count, threshold) returning int[]{successes,
 *                             naturalSixes} for Sustained/Lethal/Devastating trigger checks
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; rollWithSixes()
 *                             called by CombatEngine at every hit and wound roll step
 */

package bolterandburden;

import java.util.Random;

public class Dice {

    private static Random rng = new Random();

    /** Private constructor — this class is not meant to be instantiated. */
    private Dice() {}

    /**
     * Seeds the internal RNG for deterministic results. Call before any combat
     * that must produce reproducible output (e.g. automated tests).
     *
     * @param seed The seed value to apply
     */
    public static void setSeed(long seed) {
        rng = new Random(seed);
    }

    /**
     * Rolls a single die with the specified number of sides.
     *
     * @param sides Number of sides (e.g. 6 for a D6)
     * @return A value between 1 and sides inclusive
     */
    public static int roll(int sides) {
        return rng.nextInt(sides) + 1;
    }

    /**
     * Rolls a standard six-sided die.
     *
     * @return A value between 1 and 6 inclusive
     */
    public static int d6() {
        return roll(6);
    }

    /**
     * Rolls count D6 dice and returns the number of dice that meet or exceed
     * the given threshold (e.g. rollHits(3, 4) = three dice needing 4+).
     *
     * @param count     Number of dice to roll
     * @param threshold Minimum roll required for a success
     * @return Number of successful dice
     */
    public static int rollHits(int count, int threshold) {
        int successes = 0;
        for (int i = 0; i < count; i++) {
            if (d6() >= threshold) successes++;
        }
        return successes;
    }

    /**
     * Rolls count D6 dice and returns both the total successes and the count
     * of natural 6s among those rolls. Natural 6s matter for special weapon
     * abilities: Sustained Hits (each 6 generates extra hits), Lethal Hits
     * (6s auto-wound), and Devastating Wounds (6s cause mortal wounds).
     *
     * @param count     Number of dice to roll
     * @param threshold Minimum roll required for a success
     * @return int[]{successes, naturalSixes}
     */
    public static int[] rollWithSixes(int count, int threshold) {
        int successes = 0;
        int sixes     = 0;
        for (int i = 0; i < count; i++) {
            int roll = d6();
            if (roll == 6)          sixes++;
            if (roll >= threshold)  successes++;
        }
        return new int[]{successes, sixes};
    }
}

/*
 * Change Log:
 *   2026-04-08  Shane Potts  Initial creation - Expansion phase; static dice utility
 *                             with seeded Random for deterministic tests; d6(), roll(),
 *                             rollHits(count, threshold) returning success count, and
 *                             rollWithSixes(count, threshold) returning int[]{successes,
 *                             naturalSixes} for Sustained/Lethal/Devastating trigger checks
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; rollWithSixes()
 *                             called by CombatEngine at every hit and wound roll step
 */
