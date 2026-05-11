/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: A single node in the binary campaign tree. Each node represents
 *              one narrative encounter that the player's warband can arrive at
 *              during a campaign. Non-leaf nodes have two children representing
 *              the binary choice available at that encounter (e.g., "Press
 *              forward" vs "Fall back"). Leaf nodes are terminal: they have no
 *              children and resolve the campaign branch when navigate() reaches
 *              them. CampaignEngine.navigate() uses left and right child
 *              references to recurse through the tree.
 * Inputs:      Encounter data via constructor; child references set via
 *              setLeft() / setRight() after construction
 * Outputs:     Encounter narrative via getters; tree structure via isLeaf()
 *
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 3; binary tree node storing
 *                             encounterTitle, encounterDescription, leftChoiceLabel,
 *                             rightChoiceLabel, isCombatEncounter flag, xpReward, and
 *                             wargearReward; isLeaf() returns true when both children
 *                             are null; CampaignEngine.navigate() recurses via
 *                             getLeft()/getRight()
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; nodes constructed
 *                             by CampaignEngine.buildActOneCampaign(),
 *                             buildActTwoCampaign(), buildActThreeCampaign()
 */

package bolterandburden;

public class CampaignNode {

    private String encounterTitle;
    private String encounterDescription;
    private String leftChoiceLabel;     // Text shown for the left-branch choice
    private String rightChoiceLabel;    // Text shown for the right-branch choice
    private boolean isCombatEncounter;  // true -> CombatEngine resolves this node
    private int xpReward;               // XP awarded to surviving units on completion
    private String wargearReward;       // Optional wargear name awarded at this node ("" = none)
    private CampaignNode left;
    private CampaignNode right;

    /**
     * Constructs a CampaignNode with all encounter metadata.
     * Child references default to null (leaf node) until set explicitly.
     *
     * @param encounterTitle       Short title for this encounter
     * @param encounterDescription Full narrative description shown to the player
     * @param leftChoiceLabel      Action label for the left branch (ignored on leaf nodes)
     * @param rightChoiceLabel     Action label for the right branch (ignored on leaf nodes)
     * @param isCombatEncounter    true if this node triggers a CombatEngine skirmish
     * @param xpReward             XP awarded to all surviving units when this node resolves
     * @param wargearReward        Name of wargear awarded here, or "" for no reward
     */
    public CampaignNode(String encounterTitle,
                        String encounterDescription,
                        String leftChoiceLabel,
                        String rightChoiceLabel,
                        boolean isCombatEncounter,
                        int xpReward,
                        String wargearReward) {
        this.encounterTitle       = encounterTitle;
        this.encounterDescription = encounterDescription;
        this.leftChoiceLabel      = leftChoiceLabel;
        this.rightChoiceLabel     = rightChoiceLabel;
        this.isCombatEncounter    = isCombatEncounter;
        this.xpReward             = xpReward;
        this.wargearReward        = wargearReward;
        this.left  = null;
        this.right = null;
    }

    // -------------------------------------------------------------------------
    // Tree Utility
    // -------------------------------------------------------------------------

    /**
     * Returns true if this node has no children, indicating a terminal
     * encounter that ends the current campaign branch.
     *
     * @return true if both left and right children are null
     */
    public boolean isLeaf() {
        return left == null && right == null;
    }

    /**
     * Returns true if this node has a wargear reward to distribute.
     *
     * @return true if wargearReward is non-null and non-empty
     */
    public boolean hasWargearReward() {
        return wargearReward != null && !wargearReward.isEmpty();
    }

    // --- Getters ---

    public String getEncounterTitle()       { return encounterTitle; }
    public String getEncounterDescription() { return encounterDescription; }
    public String getLeftChoiceLabel()      { return leftChoiceLabel; }
    public String getRightChoiceLabel()     { return rightChoiceLabel; }
    public boolean isCombatEncounter()      { return isCombatEncounter; }
    public int getXpReward()                { return xpReward; }
    public String getWargearReward()        { return wargearReward; }
    public CampaignNode getLeft()           { return left; }
    public CampaignNode getRight()          { return right; }

    // --- Setters ---

    public void setLeft(CampaignNode left)   { this.left  = left; }
    public void setRight(CampaignNode right) { this.right = right; }
    public void setXpReward(int xpReward)    { this.xpReward = xpReward; }
    public void setWargearReward(String w)   { this.wargearReward = w; }
}

/*
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 3; binary tree node storing
 *                             encounterTitle, encounterDescription, leftChoiceLabel,
 *                             rightChoiceLabel, isCombatEncounter flag, xpReward, and
 *                             wargearReward; isLeaf() returns true when both children
 *                             are null; CampaignEngine.navigate() recurses via
 *                             getLeft()/getRight()
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; nodes constructed
 *                             by CampaignEngine.buildActOneCampaign(),
 *                             buildActTwoCampaign(), buildActThreeCampaign()
 */
