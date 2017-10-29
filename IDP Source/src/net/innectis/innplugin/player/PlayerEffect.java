package net.innectis.innplugin.player;

import net.minecraft.server.v1_11_R1.MobEffect;
import net.minecraft.server.v1_11_R1.MobEffectList;
import net.minecraft.server.v1_11_R1.PacketPlayOutEntityEffect;
import net.minecraft.server.v1_11_R1.PacketPlayOutRemoveEntityEffect;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Hret
 *
 * Enum class to add/remove effects to a player.
 * The class uses native MC objects aswell as reflection and code to add/remove effects.
 * This is needed due to the lack of support of (craft) bukkit.
 */
public enum PlayerEffect {

    SPEED(1),
    SLOW(2),
    HASTE(3),
    MINING_FATIQUE(4),
    STRENGHT(5),
    INSTANT_HEALTH(6),
    INSTANT_DAMAGE(7),
    JUMP_BOOST(8),
    NAUSIA(9),
    REGENERATION(10),
    RESISTANCE(11),
    FIRE_RESISTANCE(12),
    WATER_BREATHING(13),
    INVISIBILITY(14),
    BLINDNESS(15),
    NIGHT_VISION(16),
    HUNGER(17),
    WEAKNESS(18),
    POISION(19),
    WITHER(20),
    HEALTH_BOOST(21),
    ABSORBTION(22),
    SATURATION(23),
    GLOWING(24),
    LEVITATION(25),
    LUCK(26),
    BAD_LUCK(27);
    /** The ID of the effect */
    public final int typeid;

    private PlayerEffect(int typeid) {
        this.typeid = typeid;
    }

    /**
     * Apply an effect the normal way, where it will be deactivated later
     * @param player
     * @param ticks (100 is about 5 seconds, 1000 about 5 min)
     * @param intensity
     */
    public void applyEffect(IdpPlayer player, int ticks, int intensity) {
        player.getHandle().addPotionEffect(getEffect(ticks, intensity));
    }

    /**
     * Remove an effect that was added the normal way <br/>
     * <b>Note this uses reflection to reach its goal.</b>
     * @param player
     */
    public void removeEffect(IdpPlayer player) {
        PotionEffectType type = PotionEffectType.getById(typeid);
        player.getHandle().removePotionEffect(type);
    }

    /**
     * Apply an effect where it will not be followed by the server, meaning it will work in a different way.<br/>
     * Sometimes it wont expire, also it wont show the bubbles around the player<br/>
     * <b>This sends a packet to the player, so use this only when your applying the effect in a special way.</b>
     * @param player
     * @param ticks (100 is about 5 seconds, 1000 about 5 min)
     * @param intensity
     */
    public void applySpecial(IdpPlayer player, int ticks, int intensity) {
        PacketPlayOutEntityEffect test = new PacketPlayOutEntityEffect(player.getHandle().getEntityId(), getNMSEffect(ticks, intensity));
        player.getHandle().getHandle().playerConnection.sendPacket(test);

    }

    /**
     * Remove an active effect.<br/>
     * <b>This sends a packet to the player, so use this only when the effect is applied in the special way.</b>
     * @param player
     */
    public void removeSpecial(IdpPlayer player) {
        PacketPlayOutRemoveEntityEffect test = new PacketPlayOutRemoveEntityEffect(player.getHandle().getEntityId(), MobEffectList.fromId(typeid));
        player.getHandle().getHandle().playerConnection.sendPacket(test);
    }

    /**
     * Looks for the given effect with the same id.
     * @param typeid
     * @return Null if no effect found
     */
    public static PlayerEffect findEffect(int id) {
        for (PlayerEffect effect : values()) {
            if (effect.typeid == id) {
                return effect;
            }
        }
        return null;
    }

    /**
     * Looks for the given effect with the same name.
     * @param name
     * @return Null if no effect found
     */
    public static PlayerEffect findEffect(String name) {
        for (PlayerEffect effect : values()) {
            if (effect.name().replace("_", "").equalsIgnoreCase(name)) {
                return effect;
            }
        }
        return null;
    }

    /**
     * Gets a potion effect from this effect ID plus ticks and intensity
     * @param ticks
     * @param intensity
     * @return null if invalid parameters
     */
    private PotionEffect getEffect(int ticks, int intensity) {
        if (ticks <= 0) {
            return null;
        }

        if (intensity <= 0) {
            return null;
        }

        PotionEffectType type = PotionEffectType.getById(typeid);
        return new PotionEffect(type, ticks, intensity, true, true);
    }

    /**
     * Gets the NMS potion effect from this potion ID
     * with ticks and intensity
     * @param ticks
     * @param intensity
     * @return null if invalid parameters
     */
    private MobEffect getNMSEffect(int ticks, int intensity) {
        if (ticks <= 0) {
            return null;
        }

        if (intensity <= 0) {
            return null;
        }

        MobEffectList list = MobEffectList.fromId(typeid);

        return new MobEffect(list, ticks, intensity);
    }

}
