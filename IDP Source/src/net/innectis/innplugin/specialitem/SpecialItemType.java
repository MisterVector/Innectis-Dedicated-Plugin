package net.innectis.innplugin.specialitem;

import net.innectis.innplugin.specialitem.types.IceShardSpecialItem;
import net.innectis.innplugin.specialitem.types.ChristmasCandleSpecialItem;
import net.innectis.innplugin.specialitem.types.AdamantineArmorSpecialItem;
import net.innectis.innplugin.specialitem.types.WinterLandSpecialItem;
import net.innectis.innplugin.specialitem.types.PresentSpecialItem;
import net.innectis.innplugin.specialitem.types.BankNoteSpecialItem;
import net.innectis.innplugin.specialitem.types.EntityJumpBoostSpecialItem;
import net.innectis.innplugin.specialitem.types.EntityMountSpecialItem;
import net.innectis.innplugin.specialitem.types.FallingBlockSpecialItem;
import net.innectis.innplugin.specialitem.types.FlamethrowerSpecialItem;
import net.innectis.innplugin.specialitem.types.FrostmourneSpecialItem;
import net.innectis.innplugin.specialitem.types.LightningRodSpecialItem;
import net.innectis.innplugin.specialitem.types.LostWingSpecialItem;
import net.innectis.innplugin.specialitem.types.MagicFlowerSpecialItem;
import net.innectis.innplugin.specialitem.types.PartyBowSpecialItem;
import net.innectis.innplugin.specialitem.types.PotatoLauncherSpecialItem;
import net.innectis.innplugin.specialitem.types.ProjectileGunSpecialItem;
import net.innectis.innplugin.specialitem.types.TransmutationSpecialItem;
import net.innectis.innplugin.specialitem.types.UmbraSpecialItem;
import net.innectis.innplugin.specialitem.types.VoltTackleSpecialItem;
import net.innectis.innplugin.specialitem.types.XRaySpecialItem;

/**
 * Lists the different types of special items
 * Each item has its ID, name, handing class, and
 * whether it can be created from /itemdata -type, -t <ID>.
 *
 * @author Hret
 */
public enum SpecialItemType {

    FROSTMOURNE(1, "Frostmourne", new FrostmourneSpecialItem()),
    UMBRA(2, "Umbra", new UmbraSpecialItem()),
    ADAMANTINE_ARMOR(3, "Adamantine Armor", new AdamantineArmorSpecialItem()),
    BANK_NOTE(4, "Bank Note", new BankNoteSpecialItem(), false),
    // 5, 6 free
    PRESENT(7, "Present", new PresentSpecialItem(), false),
    // Winter bonus items
    CHRISTMAS_CANDLE(8, "Christmas Candle", new ChristmasCandleSpecialItem()),
    WINTER_WONDER(9, "Winter Wonder", new WinterLandSpecialItem()),
    // New effects
    ICE_SHARD(10, "Ice Shard", new IceShardSpecialItem()),
    FLAMETHROWER(11, "Flamethrower", new FlamethrowerSpecialItem()),
    VOLT_TACKLE(12, "Volt Tackle", new VoltTackleSpecialItem()),
    LOST_WING(13, "Lost Wing", new LostWingSpecialItem()),
    X_RAY(14, "X Ray", new XRaySpecialItem()),
    //LASER_BEAM(15, "Laser Beam", new LaserbeamSpecialItem(false)),
    //HIGH_LASER_BEAM(16, "High Powered Laser Beam", new LaserbeamSpecialItem(true)),
    PARTY_BOW(17, "Party Bow", new PartyBowSpecialItem()),
    MAGIC_FLOWER(18, "Magical Flower", new MagicFlowerSpecialItem()),
    TRANSMUTE(19, "Transmutation", new TransmutationSpecialItem()),
    POTATO_LAUNCHER(20, "Poison Potato Launcher", new PotatoLauncherSpecialItem()),
    LIGHTNING_ROD(21, "Lightning Rod", new LightningRodSpecialItem()),
    JUMP_BOOSTER(22, "Entity Jump Boost", new EntityJumpBoostSpecialItem()),
    PROJECTILE_GUN(23, "Projectile Gun", new ProjectileGunSpecialItem()),
    FALLING_BLOCK_ITEM(24, "Falling Block Item", new FallingBlockSpecialItem()),
    ENTITY_MOUNT_ITEM(25, "Entity Mount Item", new EntityMountSpecialItem());

    //
    private final int id;
    private final String name;
    private final SpecialItem effect;
    private final boolean spawnable;

    private SpecialItemType(int id, String name, SpecialItem effect) {
        this(id, name, effect, true);
    }

    private SpecialItemType(int id, String name, SpecialItem effect, boolean spawnable) {
        this.id = id;
        this.name = name;
        this.effect = effect;
        this.spawnable = spawnable;
    }

    /**
     * The ID of this item
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * The name of this item
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * The special item handler for this item
     * @return
     */
    public SpecialItem getSpecialItem() {
        return effect;
    }

    /**
     * Checks if this item can be spawned by
     * the /itemdata command
     * @return
     */
    public boolean isSpawnable() {
        return spawnable;
    }

    /**
     * This will lookup the special item type from the id.
     * @param id
     * @return the special item type or null if not found
     */
    public static SpecialItemType fromId(int id) {
        for (SpecialItemType type : values()) {
            if (type.getId() == id
                    && type.getSpecialItem() != null) { // check if the item type exists.
                return type;
            }
        }

        return null;
    }

}
