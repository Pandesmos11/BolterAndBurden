/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Interface implemented by Abilities that can be actively
 *              manifested in the psychic phase. Provides the cast entry
 *              point and exposes any chain-triggered secondary effects.
 *              PsychicPower and SorceryRitual implement this interface.
 * Inputs:      caster and target Unit references via cast(); no inputs for
 *              getChainTriggers()
 * Outputs:     Ability effects applied to target via cast(); list of triggered
 *              Abilities returned by getChainTriggers()
 *
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 2; interface with cast(caster,
 *                             target) and getChainTriggers(); implemented by
 *                             PsychicPower and SorceryRitual; allows polymorphic
 *                             psychic phase dispatch without an abstract class
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes
 */

package bolterandburden;

import java.util.ArrayList;

public interface Castable {

    /**
     * Activates this ability, applying its effect from caster to target.
     *
     * @param caster The unit manifesting the power
     * @param target The unit receiving the effect
     */
    void cast(Unit caster, Unit target);

    /**
     * Returns the list of secondary Abilities this power can trigger when it
     * resolves. Used by PsychicPower.resolve() for recursive chain resolution.
     *
     * @return List of chain-triggered Ability objects (may be empty)
     */
    ArrayList<Ability> getChainTriggers();
}

/*
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 2; interface with cast(caster,
 *                             target) and getChainTriggers(); implemented by
 *                             PsychicPower and SorceryRitual; allows polymorphic
 *                             psychic phase dispatch without an abstract class
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes
 */
