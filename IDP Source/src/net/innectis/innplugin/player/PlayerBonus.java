package net.innectis.innplugin.player;

public enum PlayerBonus {

    TREE_FELLING             (1  , PlayerGroup.USER    , 500    , "TreeFelling"                , "Fell a whole tree with a single swing.");

    private final long flagBit;
    private final PlayerGroup group;
    private final int cost;
    private final String flagName;
    private final String description;

    private PlayerBonus(long flagBit, PlayerGroup requiredGroup, int cost, String name, String description) {
        if (flagBit <= 0 || flagBit > 64) {
            throw new RuntimeException("Bit id must be between 1 and 64 (included).");
        }

        this.flagBit = (long) Math.pow(2L, flagBit - 1);
        this.flagName = name;
        this.group = requiredGroup;
        this.cost = cost;
        this.description = description;
    }

    /**
     * The cost (in referral points) for this bonus.
     */
    public int getCost() {
        return cost;
    }

    /**
     * The BIT that this setting uses
     */
    public final long getBonusBit() {
        return flagBit;
    }

    /**
     * Returns the name of this setting
     */
    public final String getName() {
        return this.flagName;
    }

    /**
     * The group that is required to set this setting
     */
    public final PlayerGroup getRequiredGroup() {
        return this.group;
    }

    /**
     * An description of the setting
     */
    public String getDescription() {
        return description;
    }

    /**
     * Looks for the bonus flag with the given name
     * @param name
     * @return
     */
    public static PlayerBonus getBonus(String name) {

        if (name == null)
            return null;

        for (PlayerBonus setting : values()) {

            // Check if its fully disabled
            if (setting.getRequiredGroup() == PlayerGroup.NONE) {
                continue;
            }

            if (setting.getName().equalsIgnoreCase(name)) {
                return setting;
            }
        }

        return null;
    }
    
}
