/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Abstract base class for all combatant units. Defines the shared
 *              state (name, faction, stats, wargear, abilities, XP, morale,
 *              corruption) and the abstract contract every concrete subclass
 *              must fulfill. Implements Comparable<Unit> ordering by speed so
 *              units can be sorted for initiative order.
 * Inputs:      name (String), faction (String), stats (StatBlock),
 *              wargear (Wargear) via constructor
 * Outputs:     Unit data via getters; subclass-specific combat output via
 *              abstract methods takeTurn() and applyDamage()
 *
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 1; abstract Unit with name,
 *                             faction, StatBlock, Wargear, ArrayList<Ability>;
 *                             implements Comparable<Unit> by speed descending;
 *                             abstract takeTurn() and applyDamage()
 *   2026-03-23  Shane Potts  Phase 2 - added morale/corruption constants,
 *                             isRouting field, checkMorale(), rally(), route(),
 *                             loseMorale(), gainCorruption(), checkCorruption()
 *   2026-03-23  Shane Potts  Phase 3 - added XP rank thresholds, unitRank field,
 *                             isDaemonPossessed field, gainXP(), checkProgression(),
 *                             calculateRank(), onRankUp(), becomePossessed(),
 *                             isPossessed(), getUnitRank(), setExperiencePoints()
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-08  Shane Potts  Added setUnitRank(), setRouting(), setDaemonPossessed()
 *                             quiet setters for SaveManager load restoration
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; isDestroyed()
 *                             checks wounds <= 0; used as base by all four concrete types
 */

package bolterandburden;

import java.util.ArrayList;

public abstract class Unit implements Comparable<Unit> {

    // -------------------------------------------------------------------------
    // XP Rank Thresholds (Phase 3)
    // -------------------------------------------------------------------------
    /** Minimum XP to reach rank 2 - first ability slot unlocked. */
    public static final int XP_THRESHOLD_RANK2 = 10;
    /** Minimum XP to reach rank 3 - wargear acquisition event fires. */
    public static final int XP_THRESHOLD_RANK3 = 25;
    /** Minimum XP to reach rank 4 - elite trait unlocked. */
    public static final int XP_THRESHOLD_RANK4 = 50;

    // Morale falls to this value or below to trigger a route check
    public static final int MORALE_THRESHOLD           = 3;
    // Corruption reaching this value triggers a minor mutation narrative event
    public static final int CORRUPTION_MINOR_THRESHOLD = 5;
    // Corruption reaching this value triggers a major possession narrative event
    public static final int CORRUPTION_MAJOR_THRESHOLD = 10;

    private String name;
    private String faction;
    private StatBlock stats;
    private Wargear wargear;
    private ArrayList<Ability> abilities;
    private int experiencePoints;
    private int moraleLevel;
    private int corruptionLevel;
    private boolean isRouting;
    private int unitRank;
    private boolean isDaemonPossessed;

    /**
     * Constructs a Unit with required identity and combat statistics.
     * Morale starts at 10 (full), corruption at 0, and XP at 0.
     *
     * @param name    The unit's unique name (e.g., "Brother Ezekiel")
     * @param faction The name of the faction this unit belongs to
     * @param stats   A fully initialized StatBlock for this unit
     * @param wargear A Wargear object (may be empty, never null)
     */
    public Unit(String name, String faction, StatBlock stats, Wargear wargear) {
        this.name             = name;
        this.faction          = faction;
        this.stats            = stats;
        this.wargear          = wargear;
        this.abilities          = new ArrayList<>();
        this.experiencePoints   = 0;
        this.moraleLevel        = 10;
        this.corruptionLevel    = 0;
        this.isRouting          = false;
        this.unitRank           = 1;
        this.isDaemonPossessed  = false;
    }

    // -------------------------------------------------------------------------
    // Abstract Methods - each subclass provides its own implementation
    // -------------------------------------------------------------------------

    /**
     * Executes this unit's turn action. Each subclass produces output
     * appropriate to its type (e.g., Infantry shoots, Knights stomp).
     */
    public abstract void takeTurn();

    /**
     * Applies incoming damage to this unit, reducing wounds in the StatBlock.
     * Subclasses may apply defensive rolls before reducing wounds.
     *
     * @param amount Number of damage points to apply
     */
    public abstract void applyDamage(int amount);

    /**
     * Returns a formatted stat block string for this unit.
     * Each subclass includes its own type-specific fields in the output.
     *
     * @return Multi-line formatted unit summary
     */
    @Override
    public abstract String toString();

    // -------------------------------------------------------------------------
    // Comparable - natural ordering by speed (highest speed = first in queue)
    // -------------------------------------------------------------------------

    /**
     * Compares this unit to another by speed for natural ordering.
     * Higher speed sorts first (descending order).
     *
     * @param other The unit to compare against
     * @return Negative if this unit is faster, positive if slower, 0 if equal
     */
    @Override
    public int compareTo(Unit other) {
        return Integer.compare(other.stats.getSpeed(), this.stats.getSpeed());
    }

    // -------------------------------------------------------------------------
    // Utility Methods
    // -------------------------------------------------------------------------

    /**
     * Adds an Ability to this unit's ability list.
     *
     * @param ability The ability to add
     */
    public void addAbility(Ability ability) {
        abilities.add(ability);
    }

    // -------------------------------------------------------------------------
    // XP Progression System (Phase 3)
    // -------------------------------------------------------------------------

    /**
     * Awards XP to this unit and checks whether a rank-up threshold has been
     * crossed. If the unit advances in rank, onRankUp() fires so subclasses
     * can unlock faction-specific abilities or wargear.
     *
     * @param amount XP points to award
     */
    public void gainXP(int amount) {
        experiencePoints += amount;
        checkProgression();
    }

    /**
     * Evaluates the current XP total against rank thresholds. If the unit's
     * calculated rank is higher than the stored rank, updates the rank and
     * calls onRankUp().
     */
    private void checkProgression() {
        int newRank = calculateRank();
        if (newRank > unitRank) {
            unitRank = newRank;
            System.out.println(Color.c("  *** RANK UP: " + name + " advances to Rank " + unitRank
                    + "! (XP: " + experiencePoints + ") ***", Color.BOLD + Color.YELLOW));
            onRankUp(unitRank);
        }
    }

    /**
     * Returns the rank tier corresponding to the current XP total.
     *
     * @return Rank 1-4 based on XP thresholds
     */
    private int calculateRank() {
        if (experiencePoints >= XP_THRESHOLD_RANK4) return 4;
        if (experiencePoints >= XP_THRESHOLD_RANK3) return 3;
        if (experiencePoints >= XP_THRESHOLD_RANK2) return 2;
        return 1;
    }

    /**
     * Called whenever this unit advances to a new rank. Subclasses override
     * this method to unlock faction-specific abilities or wargear at the
     * appropriate rank threshold. The default implementation does nothing.
     *
     * @param newRank The rank just reached (2, 3, or 4)
     */
    protected void onRankUp(int newRank) {
        // Default: no action. Subclasses override for faction-specific rewards.
    }

    // -------------------------------------------------------------------------
    // Daemonic Possession (Phase 3)
    // -------------------------------------------------------------------------

    /**
     * Triggers daemonic possession: sets the possession flag, prints the
     * narrative event, and marks this unit as no longer under player control.
     * The CampaignEngine is responsible for removing possessed units from
     * the player's warband roster.
     */
    public void becomePossessed() {
        isDaemonPossessed = true;
        System.out.println(Color.c("  *** DAEMONIC POSSESSION: " + name
                + " is no longer their own master! "
                + "A Greater Daemon stirs within their flesh! ***", Color.BOLD + Color.MAGENTA));
    }

    /**
     * Returns true if this unit has been claimed by daemonic possession.
     *
     * @return true if possessed
     */
    public boolean isPossessed() {
        return isDaemonPossessed;
    }

    /**
     * Returns true if this unit has been reduced to zero wounds.
     *
     * @return true if wounds <= 0
     */
    public boolean isDestroyed() {
        return stats.getWounds() <= 0;
    }

    // -------------------------------------------------------------------------
    // Morale System (Phase 2)
    // -------------------------------------------------------------------------

    /**
     * Reduces this unit's morale by the given amount (minimum 0) and then
     * checks whether the morale threshold has been crossed.
     *
     * @param amount Morale points to deduct
     */
    public void loseMorale(int amount) {
        moraleLevel = Math.max(0, moraleLevel - amount);
        checkMorale();
    }

    /**
     * Checks whether this unit's morale has fallen to or below the threshold.
     * If so, and the unit is not already routing, triggers route().
     */
    public void checkMorale() {
        if (moraleLevel <= MORALE_THRESHOLD && !isRouting) {
            System.out.println(Color.c("  !! " + name + "'s nerve breaks! (morale: " + moraleLevel + ")", Color.RED));
            route();
        }
    }

    /**
     * Attempts to rally this unit: clears the routing flag and restores 2
     * morale points (capped at 10).
     */
    public void rally() {
        isRouting  = false;
        moraleLevel = Math.min(10, moraleLevel + 2);
        System.out.println(Color.c("  " + name + " rallies! Morale restored to " + moraleLevel + ".", Color.GREEN));
    }

    /**
     * Causes this unit to break and flee. Sets the routing flag and prints
     * a narrative message.
     */
    public void route() {
        isRouting = true;
        System.out.println(Color.c("  " + name + " BREAKS and flees the battlefield!", Color.RED));
    }

    // -------------------------------------------------------------------------
    // Corruption System (Phase 2)
    // -------------------------------------------------------------------------

    /**
     * Adds corruption points to this unit and checks whether a narrative
     * threshold has been crossed.
     *
     * @param amount Corruption points to add
     */
    public void gainCorruption(int amount) {
        corruptionLevel += amount;
        checkCorruption();
    }

    /**
     * Evaluates the current corruption level against narrative thresholds and
     * prints the appropriate event message. Major threshold supersedes minor.
     */
    private void checkCorruption() {
        if (corruptionLevel >= CORRUPTION_MAJOR_THRESHOLD) {
            System.out.println(Color.c("  *** POSSESSION EVENT: " + name
                    + " is consumed by the warp! Daemonic possession imminent! "
                    + "(corruption: " + corruptionLevel + ") ***", Color.BOLD + Color.MAGENTA));
        } else if (corruptionLevel >= CORRUPTION_MINOR_THRESHOLD) {
            System.out.println(Color.c("  ** MUTATION EVENT: " + name
                    + "'s flesh ripples with warp energy. "
                    + "(corruption: " + corruptionLevel + ") **", Color.MAGENTA));
        }
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public String getName()              { return name; }
    public String getFaction()           { return faction; }
    public StatBlock getStats()          { return stats; }
    public Wargear getWargear()          { return wargear; }
    public ArrayList<Ability> getAbilities() { return abilities; }
    public int getExperiencePoints()     { return experiencePoints; }
    public int getMoraleLevel()          { return moraleLevel; }
    public int getCorruptionLevel()      { return corruptionLevel; }
    public boolean isRouting()           { return isRouting; }
    public int getUnitRank()             { return unitRank; }

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

    public void setName(String name)               { this.name             = name; }
    public void setFaction(String faction)         { this.faction          = faction; }
    public void setStats(StatBlock stats)          { this.stats            = stats; }
    public void setWargear(Wargear wargear)        { this.wargear          = wargear; }
    public void setMoraleLevel(int moraleLevel)         { this.moraleLevel      = moraleLevel; }
    public void setCorruptionLevel(int level)           { this.corruptionLevel  = level; }
    public void setExperiencePoints(int xp)             { this.experiencePoints = xp; }

    /**
     * Directly sets the unit's rank without triggering onRankUp() or printing
     * any rank-up messages. Used exclusively by SaveManager during load to
     * restore persisted rank state.
     *
     * @param rank The rank value to restore (1–4)
     */
    public void setUnitRank(int rank)                   { this.unitRank         = rank; }

    /**
     * Silently sets the routing flag. Used by SaveManager during load to
     * restore persisted routing state without printing battle narrative.
     *
     * @param routing true if this unit should be marked as routing
     */
    public void setRouting(boolean routing)             { this.isRouting        = routing; }

    /**
     * Silently sets the daemon possession flag. Used by SaveManager during
     * load to restore persisted possession state without printing narrative.
     *
     * @param possessed true if this unit should be marked as possessed
     */
    public void setDaemonPossessed(boolean possessed)   { this.isDaemonPossessed = possessed; }
}

/*
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 1; abstract Unit with name,
 *                             faction, StatBlock, Wargear, ArrayList<Ability>;
 *                             implements Comparable<Unit> by speed descending;
 *                             abstract takeTurn() and applyDamage()
 *   2026-03-23  Shane Potts  Phase 2 - added morale/corruption constants,
 *                             isRouting field, checkMorale(), rally(), route(),
 *                             loseMorale(), gainCorruption(), checkCorruption()
 *   2026-03-23  Shane Potts  Phase 3 - added XP rank thresholds, unitRank field,
 *                             isDaemonPossessed field, gainXP(), checkProgression(),
 *                             calculateRank(), onRankUp(), becomePossessed(),
 *                             isPossessed(), getUnitRank(), setExperiencePoints()
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-08  Shane Potts  Added setUnitRank(), setRouting(), setDaemonPossessed()
 *                             quiet setters for SaveManager load restoration
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; isDestroyed()
 *                             checks wounds <= 0; used as base by all four concrete types
 */
