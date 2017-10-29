package net.innectis.innplugin.inventory;

import net.innectis.innplugin.inventory.payload.InventoryPayload;
import net.innectis.innplugin.items.IdpItem;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.items.StackBag;
import net.innectis.innplugin.NotchcodeUsage;
import net.innectis.innplugin.player.IdpPlayer;
import net.minecraft.server.v1_12_R1.IInventory;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventoryDoubleChest;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author Hret
 *
 * Inventory that extends the existing craftinventory.
 * This class also accepts and returns IdpItemstacks aside from bukkititemstacks.
 * <p/>
 * Furthermore the customdata can be set or get from this class.
 * <p/>
 * This class also creates automatic proxy objects when supplies with an exising inventory.
 * When this happens the IdpInventory will make sure the original inventory is not altered.
 */
public final class IdpInventory extends CraftInventory implements IdpInventoryDataHolder {

    /** The default name of an IDP container. */
    public static final String DEFAULT_CONTAINER_NAME = "innectis.container";
    /** The default size of a single chest. */
    public static final int DEFAULT_CHEST_SIZE = 27;
    /** The default size of the inventory of a player (without armour). */
    public static final int DEFAULT_INVENTORY_SIZE = 36;

    /**
     * Constructs a new IdpInventory with a default name and the given size.
     * @param size
     */
    public IdpInventory(int size) {
        this(DEFAULT_CONTAINER_NAME, size);
    }

    /**
     * Constructs a new IdpInventory with the given name and size.
     * @param name
     * @param size
     */
    public IdpInventory(String name, int size) {
        super(new MCInventory(name, size));
    }

    /**
     * This will set the inventory to the contents of the given stackbag.
     * @param name
     * The name of the inventory.
     * @param bag
     * The stackbag that has the items in it.
     */
    public IdpInventory(String name, StackBag bag) {
        this(name, bag.getContents());
    }

    /**
     * This will set the inventory to consist of the given items.
     * @param name
     * The name of the inventory.
     * @param items
     * The stackbag that has the items in it.
     */
    public IdpInventory(String name, IdpItemStack[] items) {
        this(name, items.length);
        setContents(items);
    }

    /**
     * This will cause the IdpInventory to proxy the given inventory.
     * @param proxyinventory
     * The inventory to set the contents to
     */
    @NotchcodeUsage()
    public IdpInventory(IInventory proxyinventory) {
        super(proxyinventory);
    }

    /**
     * Creates a new IdpInventory from an bukkitinventory
     * @param bukkitinventory
     * The inventory will proxy this inventory and does not change the actual inventory itself.
     */
    public IdpInventory(Inventory bukkitinventory) {
        super(new MCInventoryProxy(bukkitinventory));
    }

    /**
     * This will create a new inventory based upon the combination of 2 inventories.
     * The items of the second inventory can be accessed by using an index that is higher then the
     * length of the first inventory.
     * <p/>
     * For instance, if the first inventory has 5 items and the second has 4.
     * The items of the first inventory are 0-4 (including) and the second inventory will have
     * 5-8 (including).
     * <p/>
     * Note: this method does not accept null values being passed.
     *
     * @param inventory1
     * The first inventory
     * @param inventory2
     * The second inventory
     *
     */
    public IdpInventory(Inventory inventory1, Inventory inventory2) {
        this(new CraftInventoryDoubleChest((CraftInventory) inventory1, (CraftInventory) inventory2));
    }

    /**
     * Creates a new IdpInventory from an bukkitinventory
     * @param bukkitinventory
     * The inventory will proxy this inventory and does not change the actual inventory itself.
     * @param name
     * Supply a new name that should be given to the chest
     */
    public IdpInventory(Inventory bukkitinventory, String name) {
        super(new MCInventoryProxy(bukkitinventory, name));
    }

    /**
     * Creates a new IdpInventory that uses the inventory of a player.
     * @param player
     */
    public IdpInventory(IdpPlayer player) {
        super(new MCPlayerInventory(player));
    }

    /**
     * The name (or title) on the inventory that is shown.
     * @return the name of the inventory
     */
    @Override
    public String getName() {
        return super.getName();
    }

    /**
     * The contents of the inventory as an IdpItemstacks.
     * @return
     */
    public IdpItemStack[] getContentsIdp() {
        return IdpItemStack.fromBukkitItemStack(super.getContents());
    }

    /**
     * Set the contents of the inventory.
     * @param items
     */
    public void setContents(IdpItemStack[] items) {
        setContents(IdpItemStack.toBukkitItemStack(items));
    }

    /**
     * Set the contents of the inventory.
     * @param items
     * @param startindex - The index in the inventory to start adding the items (inclusive)
     */
    public void setContents(IdpItemStack[] items, int startindex) {
        for (int i = 0; i < items.length; i++) {
            if (startindex >= getSize()) {
                break;
            }
            setItem(startindex, items[i]);
            startindex++;
        }
    }

    /**
     * This will give the item at the given index.
     * @param index
     * @return
     */
    public IdpItemStack getItemIdp(int index) {
        return IdpItemStack.fromBukkitItemStack(getItem(index));
    }

    /**
     * Sets the item at the given index
     * @param index
     * @param itemstack
     */
    public void setItem(int index, IdpItemStack itemstack) {
        setItem(index, (itemstack != null ? itemstack.toBukkitItemstack() : null));
    }

    /**
     * The slot where the given item appears first.
     *
     * @param item
     * @return The index where the item appears first or -1 if not in the inventory.
     */
    public int first(IdpItemStack item) {
        return super.first(item.toBukkitItemstack());
    }

    /**
     * Checks if the given material is in the inventory for the given amount.
     * @param item
     * @param amount
     * @return
     */
    public boolean contains(IdpMaterial material, int amount) {
        return super.contains(material.getBukkitMaterial(), amount);
    }

    /**
     * Checks if the given item is in the inventory for the given amount.
     * @param item
     * @param amount
     * @return
     */
    public boolean contains(IdpItem item, int amount) {
        IdpItemStack stack = new IdpItemStack(item, amount);
        return contains(stack);
    }

    /**
     * Checks if the given item is in the inventory for the given amount.
     * @param item
     * @param amount
     * @return
     */
    public boolean contains(IdpItemStack item) {
        return super.contains(item.toBukkitItemstack(), item.getAmount());
    }

    /**
     * Removes the given material from the inventory.
     * @param material
     */
    public void remove(IdpMaterial material) {
        super.remove(material.getBukkitMaterial());
    }

    /**
     * Removes the given item from the inventory.
     * @param item
     */
    public void remove(IdpItemStack item) {
        super.remove(item.toBukkitItemstack());
    }

    /**
     * Looks for first empty slot.
     * @return index of the first found empty slot. <br/>
     * Or -1 if no empty slots
     */
    @Override
    public int firstEmpty() {
        return super.firstEmpty();
    }

    /**
     * Firsts the first slot that is NOT empty
     * @return
     */
    public int firstNonEmpty() {
        for (int i = 0; i < getSize(); i++) {
            if (getItem(i) != null && getItem(i).getType() != Material.AIR) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Clears the slot with the given index
     * @param index
     */
    @Override
    public void clear(int index) {
        super.clear(index);
    }

    /**
     * Inventory payload that can be given to the inventory
     * @param payload
     */
    @Override
    public void setPayload(InventoryPayload payload) {
        ((IdpInventoryDataHolder) getInventory()).setPayload(payload);
    }

    /**
     * Gets an inventory payload that is given to this inventory
     * @returns custom data
     */
    @Override
    public InventoryPayload getPayload() {
        return ((IdpInventoryDataHolder) getInventory()).getPayload();
    }
}
