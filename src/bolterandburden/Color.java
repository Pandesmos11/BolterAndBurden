/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Static utility class providing ANSI terminal color constants
 *              and a convenience wrapper for applying them. All methods are
 *              static; no instances are constructed. Colors degrade gracefully
 *              in terminals that do not support ANSI codes — the escape
 *              sequences are simply displayed as empty strings.
 * Inputs:      Text strings passed to c()
 * Outputs:     Color-wrapped strings ready for System.out.println()
 *
 * Change Log:
 *   2026-04-08  Shane Potts  Initial creation - Expansion phase; static ANSI color
 *                             utility with RESET, BOLD, RED, GREEN, YELLOW, CYAN,
 *                             MAGENTA constants; c() wraps text in escape code +
 *                             RESET; private constructor prevents instantiation
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; used throughout
 *                             Game, CampaignEngine, and CombatEngine for all colored
 *                             console output
 */

package bolterandburden;

public class Color {

    // ANSI escape codes
    public static final String RESET   = "\u001B[0m";
    public static final String BOLD    = "\u001B[1m";
    public static final String RED     = "\u001B[91m";   // damage, death, routing
    public static final String GREEN   = "\u001B[92m";   // saves, survival, rally
    public static final String YELLOW  = "\u001B[93m";   // rank-up, XP, victory
    public static final String CYAN    = "\u001B[96m";   // structure, headers
    public static final String MAGENTA = "\u001B[95m";   // warp, psychic, corruption

    /** Private constructor — this class is not meant to be instantiated. */
    private Color() {}

    /**
     * Wraps text in the given ANSI color code, followed by a reset.
     *
     * @param text  The string to colorize
     * @param ansi  One of the Color constants (e.g. Color.RED)
     * @return      The colorized string
     */
    public static String c(String text, String ansi) {
        return ansi + text + RESET;
    }
}

/*
 * Change Log:
 *   2026-04-08  Shane Potts  Initial creation - Expansion phase; static ANSI color
 *                             utility with RESET, BOLD, RED, GREEN, YELLOW, CYAN,
 *                             MAGENTA constants; c() wraps text in escape code +
 *                             RESET; private constructor prevents instantiation
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; used throughout
 *                             Game, CampaignEngine, and CombatEngine for all colored
 *                             console output
 */
