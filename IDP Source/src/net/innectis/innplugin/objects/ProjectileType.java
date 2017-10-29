package net.innectis.innplugin.objects;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.WitherSkull;

/**
 * An enum listing all projectile types
 *
 * @author AlphaBlend
 */
public enum ProjectileType {

    SMALL_FIREBALL(0, SmallFireball.class, "Small Fireball"),
    FIREBALL(1, Fireball.class, "Fireball"),
    LARGE_FIREBALL(2, LargeFireball.class, "Large Fireball"),
    WITHER_SKULL(3, WitherSkull.class, "Wither Skull"),
    ARROW(4, Arrow.class, "Arrow"),
    EGG(5, Egg.class, "Egg"),
    SNOWBALL(6, Snowball.class, "Snowball"),
    ENDER_PEARL(7, EnderPearl.class, "Ender Pearl"),
    THROWN_EXP_POTION(8, ThrownExpBottle.class, "Thrown Experience Bottle");

    private int cycleNumber;
    private Class bukkitClazz;
    private String name;

    private ProjectileType(int cycleNumber, Class bukkitClazz, String name) {
        this.cycleNumber = cycleNumber;
        this.bukkitClazz = bukkitClazz;
        this.name = name;
    }

    /**
     * Gets the cycle number of this projectile type
     * @return
     */
    public int getCycleNumber() {
        return cycleNumber;
    }

    /**
     * Gets the bukkit class of this projectile
     * @return
     */
    public Class getBukkitClass() {
        return bukkitClazz;
    }

    /**
     * Gets the name of this projectile
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the next projectile type
     * @return
     */
    public ProjectileType nextType() {
        int num = this.getCycleNumber();

        if (num == values().length - 1) {
            num = 0;
        } else {
            num++;
        }

        return values()[num];
    }

    /**
     * Returns the projectile type based on the name
     * @param num
     * @return
     */
    public static ProjectileType fromCycleNumber(int num) {
        for (ProjectileType type : values()) {
            if (type.getCycleNumber() == num) {
                return type;
            }
        }

        // Return the first projectile type, if unable to find a projectile type
        return values()[0];
    }

}
