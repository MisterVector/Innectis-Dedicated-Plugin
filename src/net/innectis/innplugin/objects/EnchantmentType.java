package net.innectis.innplugin.objects;

import net.innectis.innplugin.items.IdpItemStack;
import org.bukkit.enchantments.Enchantment;

/**
 *
 * @author AlphaBlend
 *
 * Holds all the enchantments and their restrictions
 */
public enum EnchantmentType {

    PROTECTION_ENVIRONMENTAL(0, "Environmental Protection", "environmentalprotection", "ep"),
    PROTECTION_FIRE(1, "Fire Protection", "fireprotection", "fp"),
    PROTECTION_FALL(2, "Fall Protection", "fallprotection"),
    PROTECTION_EXPLOSIONS(3, "Explosion Protection", "explosionprotection", "ep"),
    PROTECTION_PROJECTILE(4, "Projectile Protection", "projectileprotection", "pt"),
    PROTECTION_RESPIRATION(5, "Respiration Protection", "respirationprotection", "rp"),
    PROTECTION_AQUAAFFINITY(6, "Aqua Affinity", "aquaaffinity", "af"),
    PROTECTION_THORNS(7, "Thorns", "thorns"),
    DEPTH_STRIDER(8, "Depth Strider", "depthstrider", "ds"),
    FROST_WALKER(9, "Frost Walker", "frostwalker", "fw"),
    CURSE_OF_BINDING(10, "Curse of Binding", "curseofbinding", "cob"),
    DAMAGE_ALL(16, "Damage All", "damageall", "da"),
    DAMAGE_UNDEAD(17, "Damage Undead", "damageundead", "du"),
    DAMAGE_ARTHROPODS(18, "Damage Arthropods", "damagearthropods", "da"),
    KNOCKBACK(19, "Knockback", "knockback", "kb"),
    FIRE_ASPECT(20, "Fire Aspect", "fireaspect", "fa"),
    LOOTING(21, "Looting", "looting", "loot"),
    SWEEPING_EDGE(22, "Sweeping Edge", "sweepingedge", "se"),
    EFFICIENCY(32, "Efficiency", "efficiency"),
    SILK_TOUCH(33, "Silk Touch", "silktouch"),
    DURABILITY(34, "Durability", "durability"),
    FORTUNE(35, "Fortune", "fortune"),
    ARROW_POWER(48, "Arrow Power", "arrowpower", "ap"),
    ARROW_KNOCKBACK(49, "Arrow Knockback", "arrowknockback", "ak"),
    ARROW_FLAME(50, "Arrow Flame", "arrowflame", "af"),
    ARROW_INFINITE(51, "Infinite Arrows", "infinitearrows", "ia"),
    LUCK(61, "Luck of the Sea", "luck"),
    LURE(62, "Lure", "lure"),
    MENDING(70, "Mending", "mending"),
    CURSE_OF_VANISHING(71, "Curse of Vanishing", "curseofvanishing", "cov"),
    NONE(-1, "");

    private final int id;
    private final String name;
    private final String[] argnames;

    private EnchantmentType(int id, String name, String... argnames) {
        this.id = id;
        this.name = name;
        this.argnames = argnames;
    }

    /**
     * Returns the Enchantment ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the argument names associated with this enchantment
     * @return
     */
    public String[] getArgNames() {
        return argnames;
    }

    /**
     * Gets the name of this enchantment
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the enchantment from the specified argument
     * @param argument
     * @return
     */
    public static EnchantmentType fromArgument(String argument) {
        for (EnchantmentType et : values()) {
            for (String arg : et.argnames) {
                if (argument.equalsIgnoreCase(arg)) {
                    return et;
                }
            }
        }

        return NONE;
    }

    /**
     * Gets the enchantment with the given ID.
     * @param id
     * @return
     */
    public static EnchantmentType fromId(int id) {
        for (EnchantmentType et : values()) {
            if (et.getId() == id) {
                return et;
            }
        }

        return NONE;
    }

    /**
     * Gets an EnchantmentType from a bukkit enchantment
     * @param ench
     * @return
     */
    public static EnchantmentType fromBukkitEnchantment(Enchantment ench) {
        for (EnchantmentType et : values()) {
            if (et.getId() == ench.getId()) {
                return et;
            }
        }

        return null;
    }

    /**
     * Returns if an item can be enchanted by the specified ID
     * @param stack
     * @param id
     * @return
     */
    public boolean canEnchantItem(IdpItemStack stack) {
        Enchantment ench = Enchantment.getById(id);

        if (ench != null) {
            if (ench.canEnchantItem(stack.toBukkitItemstack())) {
                return true;
            }
        }

        return false;
    }

}
