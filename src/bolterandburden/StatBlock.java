/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Holds all core combat statistics for a unit. Composed into Unit
 *              as a private field. Acts as a pure data carrier with no logic.
 * Inputs:      wounds, toughness, save, speed, attacks (all int, via constructor)
 * Outputs:     Individual stat values via getters; formatted summary via toString()
 *
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 1; pure data carrier with
 *                             wounds, toughness, save, speed, attacks; five getters
 *                             and five setters; toString() formats as
 *                             "W:x  T:x  Sv:x+  Spd:x  A:x"
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; CombatEngine
 *                             reads and writes all five stats during skirmish resolution
 */

package bolterandburden;

public class StatBlock {

    private int wounds;
    private int toughness;
    private int save;
    private int speed;
    private int attacks;

    /**
     * Constructs a StatBlock with all five combat stats.
     *
     * @param wounds    Total wound points before the unit is destroyed
     * @param toughness Resistance to damage; compared against weapon strength
     * @param save      Armor save value (lower is better, e.g. 3 = 3+ save)
     * @param speed     Movement speed; used as the initiative tiebreaker
     * @param attacks   Number of attack dice rolled per combat phase
     */
    public StatBlock(int wounds, int toughness, int save, int speed, int attacks) {
        this.wounds    = wounds;
        this.toughness = toughness;
        this.save      = save;
        this.speed     = speed;
        this.attacks   = attacks;
    }

    // --- Getters ---

    public int getWounds()    { return wounds; }
    public int getToughness() { return toughness; }
    public int getSave()      { return save; }
    public int getSpeed()     { return speed; }
    public int getAttacks()   { return attacks; }

    // --- Setters ---

    public void setWounds(int wounds)       { this.wounds    = wounds; }
    public void setToughness(int toughness) { this.toughness = toughness; }
    public void setSave(int save)           { this.save      = save; }
    public void setSpeed(int speed)         { this.speed     = speed; }
    public void setAttacks(int attacks)     { this.attacks   = attacks; }

    /**
     * Returns a formatted one-line summary of all five stats.
     *
     * @return String in the format "W:x T:x Sv:x+ Spd:x A:x"
     */
    @Override
    public String toString() {
        return String.format("W:%d  T:%d  Sv:%d+  Spd:%d  A:%d",
                wounds, toughness, save, speed, attacks);
    }
}

/*
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 1; pure data carrier with
 *                             wounds, toughness, save, speed, attacks; five getters
 *                             and five setters; toString() formats as
 *                             "W:x  T:x  Sv:x+  Spd:x  A:x"
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; CombatEngine
 *                             reads and writes all five stats during skirmish resolution
 */
