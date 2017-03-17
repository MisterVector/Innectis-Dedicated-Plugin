package net.innectis.innplugin.objects.owned;

import net.innectis.innplugin.player.PlayerGroup;

/**
 *
 * @author Lynxy
 *
 * Enum containing the flags that are settable on owned objects
 */
public enum LotFlagType implements FlagType {

    // #FORMAT_START
// #FORMAT_START
    PVP                     (1  , "PvP"                   , PlayerGroup.GUEST),
    SPLEEF                  (2  , "Spleef"                , PlayerGroup.MODERATOR),
    NOFREEZE                (3  , "NoFreeze"              , PlayerGroup.GUEST),
    NODAMAGE                (4  , "NoDamage"              , PlayerGroup.ADMIN),
    BIGSTRUCTURE            (5  , "BigStructure"          , PlayerGroup.MODERATOR),
    NODROPS                 (6  , "NoDrops"               , PlayerGroup.ADMIN),
    PIXELBUILD              (7  , "PixelBuild"            , PlayerGroup.ADMIN),
    FOOTBALLBLOCKS          (8  , "Football"              , PlayerGroup.VIP),
    NOMOBS                  (9  , "NoMobs"                , PlayerGroup.MODERATOR),
    ITEMPICKUP              (10 , "ItemPickup"            , PlayerGroup.GUEST),
    FARM                    (11 , "Farm"                  , PlayerGroup.GUEST),
    NOTELEPORT              (12 , "NoTeleport"            , PlayerGroup.GUEST),
    DESTRUCTION             (13 , "Destruction"           , PlayerGroup.GUEST),
    GOLEMTRAIL              (14 , "GolemTrail"            , PlayerGroup.GUEST),
    BLINDNESS               (15 , "Blindness"             , PlayerGroup.VIP),
    NOSIT                   (16 , "NoSit"                 , PlayerGroup.GUEST),
    FREEFLOW                (17 , "FreeFlow"              , PlayerGroup.GUEST),
    NOMELEE                 (18 , "NoMelee"               , PlayerGroup.GUEST),
    NORANGED                (19 , "NoRanged"              , PlayerGroup.GUEST),
    ETERNALDAY              (20 , "EternalDay"            , PlayerGroup.MODERATOR),
    ETERNALNIGHT            (21 , "EternalNight"          , PlayerGroup.MODERATOR),
    NOPOTION                (22 , "NoPotion"              , PlayerGroup.MODERATOR),
    BOUNCE                  (23 , "Bounce"                , PlayerGroup.ADMIN),
    NOWE                    (24 , "NoWE"                  , PlayerGroup.GUEST),
    NOJUMP                  (25 , "NoJump"                , PlayerGroup.VIP),
    NOHUNGER                (26 , "NoHunger"              , PlayerGroup.ADMIN),
    BANKLOT                 (27 , "BankLot"               , PlayerGroup.ADMIN),
    NOMONSTERS              (28 , "NoMonsters"            , PlayerGroup.MODERATOR),
    NOMOBLOOT               (29 , "NoMobLoot"             , PlayerGroup.ADMIN),
    NOMEMBERLOGOUTSPAWN     (30 , "NoMemberLogoutSpawn"   , PlayerGroup.GUEST),
    NOLIGHTNING             (31 , "NoLightning"           , PlayerGroup.GUEST),
    INFINITEDISPENSER       (33 , "InfiniteDispenser"     , PlayerGroup.ADMIN),
    NOENCHANTMENTS          (34 , "NoEnchantments"        , PlayerGroup.MODERATOR),
    CREATIVEWATER           (35 , "CreativeWater"         , PlayerGroup.ADMIN),
    RESTRICTVEHICLES        (36 , "RestrictVehicles"      , PlayerGroup.GUEST),
    LOCKINPUT               (37 , "LockInput"             , PlayerGroup.GUEST),
    NOMELT                  (38 , "NoMelt"                , PlayerGroup.GUEST),
    LOCKOUTPUT              (39 , "LockOutput"            , PlayerGroup.GUEST),
    NOSTRUCTURE             (40 , "NoStructure"           , PlayerGroup.GUEST),
    INVISIBLE               (41 , "Invisible"             , PlayerGroup.MODERATOR),
    NOTARGET                (42 , "NoTarget"              , PlayerGroup.MODERATOR),
    ALLOW_FIREWORKS         (43 , "AllowFireworks"        , PlayerGroup.GUEST),
    HARDCORE                (44 , "Hardcore"              , PlayerGroup.USER),
    ETERNALWEATHER          (45 , "EternalWeather"        , PlayerGroup.MODERATOR),
    NOWEATHER               (46 , "NoWeather"             , PlayerGroup.MODERATOR),
    NOPEARLS                (47,  "NoPearls"              , PlayerGroup.GUEST),
    NOFALLDAMAGE            (48,  "NoFallDamage"          , PlayerGroup.ADMIN),
    INFINITEWATER           (49,  "InfiniteWater"         , PlayerGroup.GUEST),
    NOSAVEINVENTORY         (50,  "NoSaveInventory"       , PlayerGroup.ADMIN),
    NOESCAPE                (51,  "NoEscape"              , PlayerGroup.ADMIN),
    NONAMETAG               (52,  "NoNametag"             , PlayerGroup.GOLDY),
    ANTICOLLISION           (53,  "AntiCollision"         , PlayerGroup.MODERATOR),
    HUNGER                  (54,  "Hunger"                , PlayerGroup.MODERATOR);
    // #FORMAT_END

    private final long flagBit;
    private final String flagName;
    private final PlayerGroup group;

    private LotFlagType(long flagBit, String name, PlayerGroup requiredGroup) {
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

    /**
     * Gets a lot flag type from the specified name
     * @param name
     * @return
     */
    public static LotFlagType fromName(String name) {
        for (LotFlagType lft : values()) {
            if (lft.getFlagName().equalsIgnoreCase(name)) {
                return lft;
            }
        }

        return null;
    }

}
