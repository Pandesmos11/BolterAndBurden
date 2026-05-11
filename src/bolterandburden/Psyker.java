/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Concrete Unit subclass representing warp-wielding psyker units
 *              such as Chaos Sorcerers and Dark Angels Librarians. Tracks a
 *              psychic discipline name and a warp charge pool used in combat.
 *              Full recursive psychic chain resolution is implemented in Phase 2.
 * Inputs:      Inherits Unit constructor args; additionally requires
 *              disciplineName (String) and warpCharge (int)
 * Outputs:     Turn actions and damage results printed to console;
 *              formatted stat block via toString()
 *
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 1; Psyker extends Unit with
 *                             disciplineName and warpCharge; warpCharge consumed
 *                             each turn; charge spent in applyDamage() as a stub
 *                             defensive deflection
 *   2026-03-23  Shane Potts  Phase 3 - overrode onRankUp() to unlock
 *                             discipline-specific abilities and wargear: rank 2
 *                             adds Warp Bolts PsychicPower; rank 3 adds Force
 *                             Barrier BattleTrait (+1 toughness); rank 4 adds +1
 *                             warpCharge permanently
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes
 */

package bolterandburden;

public class Psyker extends Unit {

    private String disciplineName;
    private int warpCharge;

    /**
     * Constructs a Psyker unit with all required fields.
     *
     * @param name           Unique name for this psyker (e.g., "Sorcerer Malachar")
     * @param faction        Faction name this psyker belongs to
     * @param stats          Combat StatBlock for this psyker
     * @param wargear        Equipment carried by this psyker
     * @param disciplineName Psychic discipline this psyker draws from
     *                       (e.g., "Tzeentch", "Obscuration", "Interromancy")
     * @param warpCharge     Starting warp charge pool
     */
    public Psyker(String name, String faction, StatBlock stats,
                  Wargear wargear, String disciplineName, int warpCharge) {
        super(name, faction, stats, wargear);
        this.disciplineName = disciplineName;
        this.warpCharge     = warpCharge;
    }

    /**
     * Executes this psyker's turn. Draws on their warp charge to manifest
     * a power from their discipline. Charge is consumed on use.
     */
    @Override
    public void takeTurn() {
        if (warpCharge > 0) {
            warpCharge--;
            System.out.println(getName() + " reaches into the warp ("
                    + disciplineName + " discipline) and manifests a power! "
                    + "Warp charge remaining: " + warpCharge);
        } else {
            System.out.println(getName() + " strains against the veil but the warp runs dry. "
                    + "Psychic phase skipped!");
        }
    }

    /**
     * Applies damage to this psyker. Psykers can partially redirect warp
     * energy to blunt incoming strikes as a stub defensive effect.
     *
     * @param amount Number of damage points incoming
     */
    @Override
    public void applyDamage(int amount) {
        int finalDamage = amount;
        if (warpCharge > 0) {
            // Stub: spend charge to reduce damage
            warpCharge--;
            finalDamage = Math.max(0, amount - 1);
            System.out.println(getName() + " channels warp energy to deflect 1 damage. "
                    + "Warp charge remaining: " + warpCharge);
        }
        int current = getStats().getWounds();
        getStats().setWounds(current - finalDamage);
        System.out.println(Color.c(getName() + " takes " + finalDamage + " damage. Wounds remaining: "
                + Math.max(0, getStats().getWounds()), Color.RED));
        if (isDestroyed()) {
            System.out.println(Color.c(getName() + " collapses - the warp reclaims their soul!", Color.BOLD + Color.MAGENTA));
        }
    }

    /**
     * Returns a formatted stat block string for this psyker.
     *
     * @return Multi-line string with all psyker details
     */
    @Override
    public String toString() {
        return "=== Psyker: " + getName() + " ===\n"
                + "  Faction    : " + getFaction() + "\n"
                + "  Discipline : " + disciplineName + "\n"
                + "  Warp Charge: " + warpCharge + "\n"
                + "  Stats      : " + getStats() + "\n"
                + "  " + getWargear() + "\n"
                + "  XP: " + getExperiencePoints()
                + "  Morale: " + getMoraleLevel()
                + "  Corruption: " + getCorruptionLevel();
    }

    /**
     * Unlocks discipline-specific abilities when this Psyker advances in rank.
     * Rank 2: Warp Bolts PsychicPower added to ability list.
     * Rank 3: Force Barrier BattleTrait added (defensive trait).
     * Rank 4: +1 warp charge permanently added to the pool.
     *
     * @param newRank The rank just reached (2, 3, or 4)
     */
    @Override
    protected void onRankUp(int newRank) {
        switch (newRank) {
            case 2:
                PsychicPower warpBolts = new PsychicPower("Warp Bolts",
                        "Splinters of solidified warp energy hurled at the foe.", 1);
                addAbility(warpBolts);
                System.out.println(Color.c("    --> " + getName()
                        + " unlocks [Warp Bolts] from the "
                        + disciplineName + " discipline!", Color.YELLOW));
                break;
            case 3:
                BattleTrait forceBarrier = new BattleTrait("Force Barrier",
                        "A psychic field deflects incoming strikes.",
                        "toughness", 1);
                addAbility(forceBarrier);
                System.out.println(Color.c("    --> " + getName()
                        + " unlocks [Force Barrier] - toughness +1!", Color.YELLOW));
                break;
            case 4:
                warpCharge++;
                System.out.println(Color.c("    --> " + getName()
                        + " deepens their warp conduit! Warp charge now " + warpCharge, Color.YELLOW));
                break;
            default:
                break;
        }
    }

    // --- Getters ---

    public String getDisciplineName() { return disciplineName; }
    public int getWarpCharge()        { return warpCharge; }

    // --- Setters ---

    public void setDisciplineName(String disciplineName) { this.disciplineName = disciplineName; }
    public void setWarpCharge(int warpCharge)            { this.warpCharge     = warpCharge; }
}

/*
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 1; Psyker extends Unit with
 *                             disciplineName and warpCharge; warpCharge consumed
 *                             each turn; charge spent in applyDamage() as a stub
 *                             defensive deflection
 *   2026-03-23  Shane Potts  Phase 3 - overrode onRankUp() to unlock
 *                             discipline-specific abilities and wargear: rank 2
 *                             adds Warp Bolts PsychicPower; rank 3 adds Force
 *                             Barrier BattleTrait (+1 toughness); rank 4 adds +1
 *                             warpCharge permanently
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes
 */
