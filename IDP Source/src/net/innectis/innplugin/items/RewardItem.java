package net.innectis.innplugin.items;

import javax.annotation.Nullable;
import net.innectis.innplugin.objects.EnchantmentType;
import net.innectis.innplugin.specialitem.SpecialItemManager;
import net.innectis.innplugin.specialitem.SpecialItemType;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse.Variant;

/**
 * Enum for managing items redeemed from vote points
 *
 * @author Nosliw
 */

@Nullable
public enum RewardItem {

    WITHER_SKULL(0,  "Wither Skull", new IdpItemStack(IdpMaterial.WITHER_SKELETON_SKULL, 1), 70),
    ELYTRA(1, "Elytra", new IdpItemStack(IdpMaterial.ELYTRA, 1), 450),
    SPONGE(2, "Sponge", new IdpItemStack(IdpMaterial.SPONGE, 1), 35),
    DRAGONS_BREATH(3, "Dragon's Breath", new IdpItemStack(IdpMaterial.DRAGONS_BREATH, 1), 10),
    LOST_WINGS(4, "Lost Wings", new IdpItemStack(IdpMaterial.FEATHER, 1), 35, SpecialItemType.LOST_WING),
    SKELETON_HORSE(5, "Skeleton Horse", new IdpItemStack(IdpMaterial.SPAWN_EGG, 1), 300, EntityType.SKELETON_HORSE),
    MOOSHROOM(6, "Mooshroom", new IdpItemStack(IdpMaterial.SPAWN_EGG, 1), 150, EntityType.MUSHROOM_COW),
    MYCELIUM(7, "Mycelium", new IdpItemStack(IdpMaterial.MYCELIUM, 1), 210),
    BLAZE_POWDER(8, "10 Blaze Powder", new IdpItemStack(IdpMaterial.BLAZE_POWDER, 10), 5),
    LUCK_OF_SEA_III(9, "Luck of the Sea III", new IdpItemStack(IdpMaterial.ENCHANTED_BOOK, 1), 35, Enchantment.LUCK, 3),
    LURE_III(10, "Lure III", new IdpItemStack(IdpMaterial.ENCHANTED_BOOK, 1), 35, Enchantment.LURE, 3),
    MAGMA_CREAM(11, "Magma Cream", new IdpItemStack(IdpMaterial.MAGMA_CREAM, 1), 5),
    ENDER_PEARL(12, "3 Ender Pearls", new IdpItemStack(IdpMaterial.ENDER_PEARL, 3), 5),
    FROST_WALKER_III(13, "Frost Walker III", new IdpItemStack(IdpMaterial.ENCHANTED_BOOK, 1), 70, Enchantment.FROST_WALKER, 3),
    MENDING(14, "Mending", new  IdpItemStack(IdpMaterial.ENCHANTED_BOOK, 1), 70, Enchantment.MENDING, 1);

    private final int slot;
    private final String name;
    private final IdpItemStack stack;
    private final int cost;

    // These are optional for tweaking reward items
    private final Enchantment enchantment;
    private final int enchantmentLevel;

    RewardItem(int slot, String name, IdpItemStack stack, int cost) {
        this(slot, name, stack, cost, null, 0, null, null);
    }

    RewardItem(int slot, String name, IdpItemStack stack, int cost, Enchantment enchantment, int enchantmentLevel) {
        this(slot, name, stack, cost, enchantment, enchantmentLevel, null, null);
    }

    RewardItem(int slot, String name, IdpItemStack stack, int cost, SpecialItemType itemType) {
        this(slot, name, stack, cost, null, 0, itemType, null);
    }

    RewardItem(int slot, String name, IdpItemStack stack, int cost, EntityType entityType) {
        this(slot, name, stack, cost, null, 0, null, entityType);
    }

    RewardItem(int slot, String name, IdpItemStack stack, int cost, Enchantment enchantment, int enchantmentLevel, SpecialItemType specialItemType, EntityType entityType) {
        this.slot = slot;
        this.name= name;
        this.stack = stack;
        this.cost = cost;
        this.enchantment = enchantment;
        this.enchantmentLevel = enchantmentLevel;

        ItemData itemData = this.stack.getItemdata();
        itemData.setItemName(ChatColor.RESET + this.name);

        // If this reward item includes an enchantment, apply it
        if (enchantment != null) {
            itemData.addEnchantment(EnchantmentType.fromBukkitEnchantment(enchantment), enchantmentLevel);
        }

        if (specialItemType != null) {
            SpecialItemManager.createSpecialItem(this.stack, specialItemType);
        }

        if (entityType != null) {
            itemData.setValue("EntityTag/id", entityType.getName());
            itemData.setValue("specialEgg", "true");
        }
    }

    public static RewardItem fromSlot(int slot) {
        for (RewardItem ri : values()) {
            if (ri.getSlot() == slot) {
                return ri;
            }
        }

        return null;
    }

    public int getSlot() {
        return slot;
    }

    public String getName() {
        return name;
    }

    public IdpItemStack getItemStack() {
        return stack;
    }

    public int getCost() {
        return cost;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public int getEnchantmentLevel() {
        return enchantmentLevel;
    }

}
