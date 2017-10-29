package net.innectis.innplugin.objects.owned;

import net.innectis.innplugin.player.PlayerGroup;

/**
 *
 * Enum containing the flags that are settable on owned objects
 */
public enum ChestFlagType implements FlagType {

    // #FORMAT_START
    UNUSED_1                (1  , "Unused 1"              , PlayerGroup.NONE),
    LOT_MEMBER_CHEST        (2  , "LotMemberChest"        , PlayerGroup.GUEST),
    UNUSED_2                (3  , "Unused 2"              , PlayerGroup.NONE),
    SHOWCASE                (4  , "Showcase"              , PlayerGroup.GUEST),
    INTERACT_CURRENT        (5  , "InteractCurrent"       , PlayerGroup.MODERATOR),
    AUTO_REFILL             (6  , "AutoRefill"            , PlayerGroup.ADMIN);
    // #FORMAT_END

    private final long flagBit;
    private final String flagName;
    private final PlayerGroup group;

    private ChestFlagType(long flagBit, String name, PlayerGroup requiredGroup) {
        if (flagBit <= 0 || flagBit > 64) {
            throw new RuntimeException("Bit id must be between 1 and 64 (included).");
        }

        this.flagBit =  (long) Math.pow(2L, flagBit - 1);
        this.flagName = name;
        this.group = requiredGroup;
    }

    /**
     * @inherit
     */
    @Override
    public final long getFlagBit() {
        return flagBit;
    }

    /**
     * @inherit
     */
    @Override
    public final String getFlagName() {
        return this.flagName;
    }

    /**
     * @inherit
     */
    @Override
    public final PlayerGroup getRequiredGroup() {
        return this.group;
    }

}
