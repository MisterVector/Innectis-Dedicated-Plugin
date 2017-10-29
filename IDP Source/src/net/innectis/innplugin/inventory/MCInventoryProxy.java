package net.innectis.innplugin.inventory;

import java.util.List;
import net.innectis.innplugin.inventory.payload.InventoryPayload;
import net.innectis.innplugin.NotchcodeUsage;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.IInventory;
import net.minecraft.server.v1_12_R1.ItemStack;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 *
 * @author Hret
 *
 * This class will proxy an exising inventory without making changes to the already existing inventory.
 * Additionally all changes made will be made inside the real inventory.
 * <p/>
 *
 * This class is still able to hold IdpData, without changing the existing inventory.
 */
@NotchcodeUsage()
class MCInventoryProxy implements IInventory, IdpInventoryDataHolder {

    private IInventory inventory;
    private String overridename;

    public MCInventoryProxy(IInventory inventory, String name) {
        this.inventory = inventory;
        this.overridename = name;
    }

    public MCInventoryProxy(IInventory inventory) {
        this(inventory, null);
    }

    public MCInventoryProxy(Inventory inventory, String name) {
        // At version 1.4.6 the bukkitinventory is always a CraftInventory.
        // If that changes, convert it here..
        this.inventory = ((CraftInventory) inventory).getInventory();

        this.overridename = name;
    }

    public MCInventoryProxy(Inventory inventory) {
        this(inventory, null);
    }

    public List<ItemStack> getContents() {
        return inventory.getContents();
    }

    public boolean x_() {
        return inventory.x_();
    }

    public int getSize() {
        return inventory.getSize();
    }

    public ItemStack getItem(int i) {
        return inventory.getItem(i);
    }

    public ItemStack splitStack(int i, int j) {
        return inventory.splitStack(i, j);
    }

    public void setItem(int i, ItemStack itemstack) {
        inventory.setItem(i, itemstack);
    }

    public String getName() {
        // Allows changing the inventory name
        if (overridename == null) {
            return inventory.getName();
        } else {
            return overridename;
        }
    }

    public int getMaxStackSize() {
        return inventory.getMaxStackSize();
    }

    public boolean a(EntityHuman entityhuman) {
        return inventory.a(entityhuman);
    }

    public boolean b(int i, ItemStack is) {
        return inventory.b(i, is);
    }

    public void update() {
        inventory.update();
    }

    public ItemStack splitWithoutUpdate(int i) {
        return inventory.splitWithoutUpdate(i);
    }

    public void onOpen(CraftHumanEntity who) {
        inventory.onOpen(who);
    }

    public void onClose(CraftHumanEntity who) {
        inventory.onClose(who);
    }

    public void closeContainer(EntityHuman human) {
        inventory.closeContainer(human);
    }

    public void startOpen(EntityHuman human) {
        inventory.startOpen(human);
    }

    public List<HumanEntity> getViewers() {
        return inventory.getViewers();
    }

    public InventoryHolder getOwner() {
        return inventory.getOwner();
    }

    public void setMaxStackSize(int s) {
        inventory.setMaxStackSize(s);
    }
    // IDP add
    private InventoryPayload payload = null;

    /**
     * Sets the payload that is a part of this inventory
     * @param data
     */
    @Override
    public void setPayload(InventoryPayload payload) {
        this.payload = payload;
    }

    /**
     * Gets the payload that is a part of this inventory
     * @returns custom data
     */
    @Override
    public InventoryPayload getPayload() {
        return payload;
    }

    /**
     * The real inventory where this object is proxying.
     * @return
     */
    public IInventory getWrapInventory() {
        return inventory;
    }
    // <!-- IDP add

    public int getProperty(int i) {
        return inventory.getProperty(i);
    }

    public void setProperty(int i, int j) {
        inventory.setProperty(i, j);
    }

    public Location getLocation() {
        return inventory.getLocation();
    }

    public int h() {
        return inventory.h();
    }

    public void clear() {
        inventory.clear();
    }

    public IChatBaseComponent getScoreboardDisplayName() {
        return inventory.getScoreboardDisplayName();
    }

    public boolean hasCustomName() {
        return inventory.hasCustomName();
    }
}
