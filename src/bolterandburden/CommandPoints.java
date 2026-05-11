/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Tracks the Command Point (CP) pool available to the player
 *              during a combat engagement. CP is a core resource in Warhammer
 *              40,000 10th edition used to activate Stratagems at key moments
 *              in the battle. The pool starts at a configurable value (set per
 *              act), gains +1 at the start of each new round, and is spent
 *              when the player triggers a Stratagem. Spending is gated behind
 *              canAfford() so the pool never goes negative.
 * Inputs:      int startingPool (constructor), int cost (spend/canAfford),
 *              int amount (add)
 * Outputs:     Updated CP pool value; boolean results for affordability checks
 *
 * Change Log:
 *   2026-04-09  Shane Potts  Initial creation — Phase 4 Detachment/CP system;
 *                             int pool backed by configurable starting value;
 *                             canAfford() gates all spending so pool never goes
 *                             negative; spend() returns boolean success flag;
 *                             addRoundGain() called by CombatEngine each round
 *                             (+1 CP, matching 10th ed Command phase rules);
 *                             starting pools: Act I=4, Act II=6, Act III=9
 *   2026-04-26  Shane Potts  Phase 4 complete - no further changes this phase
 */

package bolterandburden;

public class CommandPoints {

    private int pool;

    // -------------------------------------------------------------------------
    // Construction
    // -------------------------------------------------------------------------

    /**
     * Creates a CP pool with the given starting value.
     *
     * @param startingPool Initial CP (e.g. 4 for Act I, 6 for Act II, 9 for Act III)
     */
    public CommandPoints(int startingPool) {
        this.pool = startingPool;
    }

    // -------------------------------------------------------------------------
    // CP Management
    // -------------------------------------------------------------------------

    /**
     * Returns the current number of Command Points in the pool.
     *
     * @return Current CP total
     */
    public int getPool() {
        return pool;
    }

    /**
     * Returns true if the pool contains at least {@code cost} CP.
     *
     * @param cost CP required
     * @return true if affordable
     */
    public boolean canAfford(int cost) {
        return pool >= cost;
    }

    /**
     * Spends {@code cost} CP if affordable. Returns true on success.
     * Does nothing and returns false if insufficient CP remain.
     *
     * @param cost CP to spend
     * @return true if CP was successfully spent; false if insufficient
     */
    public boolean spend(int cost) {
        if (!canAfford(cost)) return false;
        pool -= cost;
        return true;
    }

    /**
     * Adds one CP to the pool (called at the start of each new round,
     * matching the 10th edition Command phase rule).
     */
    public void addRoundGain() {
        pool += 1;
    }

    /**
     * Adds an arbitrary amount of CP to the pool (used for detachment bonuses
     * or campaign rewards).
     *
     * @param amount CP to add (must be positive)
     */
    public void add(int amount) {
        if (amount > 0) pool += amount;
    }
}

/*
 * Change Log:
 *   2026-04-09  Shane Potts  Initial creation — Phase 4 Detachment/CP system;
 *                             int pool backed by configurable starting value;
 *                             canAfford() gates all spending so pool never goes
 *                             negative; spend() returns boolean success flag;
 *                             addRoundGain() called by CombatEngine each round
 *                             (+1 CP, matching 10th ed Command phase rules);
 *                             starting pools: Act I=4, Act II=6, Act III=9
 *   2026-04-26  Shane Potts  Phase 4 complete - no further changes this phase
 */
