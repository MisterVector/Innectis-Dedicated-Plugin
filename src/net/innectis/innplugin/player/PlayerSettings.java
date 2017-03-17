package net.innectis.innplugin.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Hret
 *
 * This file contains different settings that can be set to players.
 * <p/>
 * Note: the default value (if that is enabled or disabled) is <b><u>ALWAYS</u><b>
 * the situation where the flag is <b><u>NOT</u><b> set. <br/>
 * This can be check with the ::isDefaultOn() value
 */
public enum PlayerSettings {

    //#FORMAT_START
    SADDLE                  (1  , PlayerGroup.GUEST             , "Saddle"                , false, "Sets if players are allowed to sit on you."),
    HUNGER                  (2  , PlayerGroup.GOLDY             , "Hunger"                , true , "Sets if hunger should be on or off."),
    DEATHMESSAGE            (3  , PlayerGroup.GUEST             , "Death Message"         , true , "Sets if death messages should be shown."),
    SHOPNOTIFICATION        (4  , PlayerGroup.GUEST             , "Shop Message"          , true , "Sets if shop notifications should be shown."),
    //
    VALUTA_MESSAGE          (5  , PlayerGroup.GUEST             , "Valuta Messages"       , true , "Toggles vT gained on ore break message."),
    LOS_MESSAGE             (6  , PlayerGroup.GUEST             , "Line of Sight Message" , true , "Toggles the line of sight error message."),
    BONUS_MESSAGE           (7  , PlayerGroup.VIP               , "Bonus Message"         , true , "Sets if messages are sent when using bonuses."),
    HEAR_MUTED              (8  , PlayerGroup.MODERATOR         , "Hear Muted Players"    , true , "Sets if player can hear muted players."),
    //
    ITEM_PICKUP             (9  , PlayerGroup.ADMIN             , "Item Pickup"           , true , "Sets if player can pickup items."),
    TWE_INVUSEAGE           (10 , PlayerGroup.ADMIN             , "TinyWE Inventory"      , true , "Sets if the player should use the inventory with TinyWE."),
    INVERT_LOCALCHAT        (11 , PlayerGroup.GUEST             , "Invert Local Chat"     , false, "When set the '@' will be used for global chat instead of local."),
    EMPTY                   (12 , PlayerGroup.NONE              , "Empty"                 , false, "This is a placeholder."),
    //
    FLIGHT                  (13 , PlayerGroup.MODERATOR         , "Flight"                , false, "Allows the player flight abilities."),
    TIPS                    (14 , PlayerGroup.GUEST             , "Tips"                  , true , "Allows the viewing of periodic tip messages."),
    TWWAND                  (15 , PlayerGroup.VIP               , "TinyWE Wand"           , true , "Allows the use of the TinyWE axe as a wand."),
    ALLOW_TP                (16 , PlayerGroup.GUEST             , "Allow Teleport"        , true , "Allows other players to teleport to you."),
    //
    PVP                     (17, PlayerGroup.GUEST              , "PvP"                   , false, "Allows the player to engage in player-vs-player combat."),
    EMPTY_2                 (18, PlayerGroup.NONE               , "Empty"                 , false, "This is a placeholder."),
    CHAT_FILTER             (19, PlayerGroup.GUEST              , "Chat Filter"           , true , "Toggles the use of the chat filter."),
    INSTANT_TP              (20, PlayerGroup.GUEST              , "Instant Teleport"      , false, "Toggles whether teleports from others are instant."),

    GOD                     (21, PlayerGroup.ADMIN              , "God"                   , false,  "Toggles immunity from all sources.");
    //#FORMAT_END
    //
    private final long flagBit;
    private final long id;
    private final PlayerGroup group;
    private final String flagName;
    private final boolean defaultOn;
    private final String description;

    private PlayerSettings(long flagBit, PlayerGroup requiredGroup, String name, boolean defaultOn, String description) {
        if (flagBit <= 0 || flagBit > 64) {
            throw new RuntimeException("Bit id must be between 1 and 64 (included).");
        }

        this.id = flagBit;
        this.flagBit = (long) Math.pow(2L, flagBit - 1);
        this.defaultOn = defaultOn;
        this.flagName = name;
        this.group = requiredGroup;
        this.description = description;
    }

    /**
     * This checks what the default state is.
     * When true, the flag needs to be turned ON to be enabled.
     */
    public final boolean isDefaultOn() {
        return defaultOn;
    }

    /**
     * Gets the ID of this setting
     * @return
     */
    public long getId() {
        return id;
    }

    /**
     * The BIT that this setting uses
     */
    public final long getSettingBit() {
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
     * Checks if this is a staff setting
     * @return
     */
    public boolean isStaffSetting() {
        return getRequiredGroup().equalsOrInherits(PlayerGroup.MODERATOR);
    }

    /**
     * An description of the setting
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets a setting by its ID
     * @param id
     * @return
     */
    public static PlayerSettings getSetting(int id) {
        for (PlayerSettings setting : values()) {
            if (setting.getRequiredGroup() == PlayerGroup.NONE) {
                continue;
            }

            if (setting.getId() == id) {
                return setting;
            }
        }

        return null;
    }

    /**
     * Looks for the setting flag with the given name
     * @param name
     * @return
     */
    public static PlayerSettings getSetting(String name) {
        for (PlayerSettings setting : values()) {

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

    /**
     * Gets all settings in sorted order
     * @return
     */
    public static List<PlayerSettings> getSortedSettings() {
        List<PlayerSettings> settings = new ArrayList<PlayerSettings>();

        for (PlayerSettings setting : PlayerSettings.values()) {
            if (setting.getRequiredGroup() == PlayerGroup.NONE) {
                continue;
            }

            settings.add(setting);
        }

        Collections.sort(settings, new Comparator<PlayerSettings>() {
            @Override
            public int compare(PlayerSettings ps1, PlayerSettings ps2) {
                return (ps1.getName().compareTo(ps2.getName()));
            }
        });

        return settings;
    }

}
