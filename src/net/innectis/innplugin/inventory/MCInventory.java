package net.innectis.innplugin.inventory;

import java.util.ArrayList;
import java.util.List;
import net.innectis.innplugin.inventory.payload.InventoryPayload;
import net.innectis.innplugin.NotchcodeUsage;
import net.minecraft.server.v1_11_R1.ChatComponentText;
import net.minecraft.server.v1_11_R1.EntityHuman;
import net.minecraft.server.v1_11_R1.IChatBaseComponent;
import net.minecraft.server.v1_11_R1.IInventory;
import net.minecraft.server.v1_11_R1.ItemStack;
import net.minecraft.server.v1_11_R1.NonNullList;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

/**
 *
 * Inventory equivalent to a single chest
 */
@NotchcodeUsage()
class MCInventory implements IInventory, IdpInventoryDataHolder {

    private NonNullList<ItemStack> items;
    private String name;
    public List<HumanEntity> transaction = new ArrayList<HumanEntity>();

    private MCInventory() {
        this(27);
    }

    private MCInventory(int size) {
        this("innectis.container", size);
    }

    private MCInventory(String name) {
        this(name, 27);
    }

    public MCInventory(String name, int size) {
        this.name = name;
        items = NonNullList.a(size, ItemStack.a);
    }

    public NonNullList<ItemStack> getContents() {
        return items;
    }

    public boolean w_() {
        return false;
    }

    public int getSize() {
        return items.size();
    }

    public ItemStack getItem(int i) {
        return items.get(i);
    }

    public String getName() {
        return name;
    }

    public boolean hasCustomName() {
        return true;
    }

    public IChatBaseComponent getScoreboardDisplayName() {
        return new ChatComponentText(name);
    }

    public ItemStack splitStack(int i, int j) {
        NonNullList<ItemStack> is = this.items;

        if (!is.get(i).isEmpty()) {
            if (is.get(i).getCount() <= j) {
                ItemStack itemstack = is.get(i);
                is.set(i, ItemStack.a);
                return itemstack;
            }

            ItemStack itemstack = is.get(i).cloneAndSubtract(j);
            if (is.get(i).isEmpty()) {
                is.set(i, ItemStack.a);
            }

            return itemstack;
        }

        return null;
    }

    public void setItem(int i, ItemStack itemstack) {
        items.set(i, itemstack);
    }

    public int getMaxStackSize() {
        return 64;
    }

    public boolean b(int i, ItemStack is) {
        return true;
    }

    public boolean a(EntityHuman entityhuman) {
        return false;
    }

    public void update() {
    }

    public ItemStack splitWithoutUpdate(int i) {
        if (!this.items.get(i).isEmpty()) {
            ItemStack itemstack = this.items.get(i);

            this.items.set(i, ItemStack.a);
            return itemstack;
        } else {
            return null;
        }
    }

    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    public void closeContainer(EntityHuman human) {

    }

    public void startOpen(EntityHuman human) {
    }

    public List<HumanEntity> getViewers() {
        return transaction;
    }

    public InventoryHolder getOwner() {
        return null;
    }

    public void setMaxStackSize(int s) {
    }

    // IDP add
    private InventoryPayload payload = null;

    /**
     * Custom data that can be attached to the inventory.
     * @param data
     */
    @Override
    public void setPayload(InventoryPayload payload) {
        this.payload = payload;
    }

    /**
     * Any custom data attached to the inventory
     * @returns custom data
     */
    @Override
    public InventoryPayload getPayload() {
        return payload;
    }
    // <!-- IDP add

    public int getProperty(int i) {
        return 0;
    }

    public void setProperty(int i, int j) {

    }

    public Location getLocation() {
        return null;
    }

    public int h() {
        return 0;
    }

    public void clear() {

    }
}
