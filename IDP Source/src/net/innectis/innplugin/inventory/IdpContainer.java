package net.innectis.innplugin.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.innectis.innplugin.items.IdpItem;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.player.IdpPlayerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * This class will take array of items and allows you to do actions on these items.
 * <p/>
 * The IdpContainer itself will in no way store the changes made in this class inside any
 * inventory that is passed to a method or a constructor. Instead it will extract the items
 * and make a local copy.
 * <p/>
 * If needed, the class also provides methods to extract the items where they can be set into the
 * inventory that needs to be changed.
 *
 * @author AlphaBlend
*/
public class IdpContainer {

    protected IdpItemStack[] items;

    /**
     * Constructs an IdpContainer with an empty IdpItemStack
     * array of the specified size
     */
    public IdpContainer(int size) {
        items = new IdpItemStack[size];
    }

    /**
     * Constructs an IdpContainer from the player's IdpInventory
     * @param inv
     */
    public IdpContainer(IdpPlayerInventory inv) {
        this.items = inv.getItems();
    }

    /**
     * Constructs an IdpContainer using the bukkit inventory and target
     * IdpMaterial
     */
    public IdpContainer(Inventory inv) {
        items = IdpItemStack.fromBukkitItemStack(inv.getContents());
    }

    /**
     * Constructs a container object to handle transaction of items
     *
     * @param items the contents of the container
     */
    public IdpContainer(IdpItemStack[] items) {
        this(items, items.length);
    }

    /**
     * Constructs a container object to handle transaction of items
     *
     * @param items the contents of the container
     * @param maxItems the size of the container
     */
    public IdpContainer(IdpItemStack[] items, int maxItems) {
        this.items = new IdpItemStack[maxItems];
        System.arraycopy(items, 0, this.items, 0, items.length);
    }

    /**
     * Adds a new list of materials to this container
     * @param stacks
     */
    public void addMaterial(IdpItemStack[] stacks) {
        IdpItemStack[] tempItems = new IdpItemStack[this.items.length + stacks.length];
        System.arraycopy(this.items, 0, tempItems, 0, this.items.length);
        int idx = 0;

        for (int i = this.items.length; i < (this.items.length + stacks.length); i++) {
            tempItems[i] = stacks[idx++];
        }

        this.items = tempItems;
    }

    /**
     * Adds a new material to this container
     * @param stack
     */
    public void addMaterial(IdpItemStack stack) {
        IdpItemStack[] tempItems = new IdpItemStack[items.length + 1];
        System.arraycopy(this.items, 0, tempItems, 0, items.length);
        tempItems[items.length] = stack;
        items = tempItems;
    }

    /**
     * Clears the container, and gives it the specified size
     *
     * @param size
     */
    public void clearContainer(int size) {
        this.items = new IdpItemStack[size];
    }

    /**
     * Checks to see if the container has enough of the specified material
     *
     * @param mat
     * @param amount
     *
     * @return
     */
    public boolean hasMaterialCount(IdpMaterial mat, int amount) {
        return (countMaterial(mat) >= amount);
    }

    /**
     * Counts how many items of the specified material are present in this container
     * if the target material is AIR, this will count infinite items
     * <b>NOTE:</b> This does NOT care about metadata
     *
     * @param mat
     * @return
     */
    public int countMaterial(IdpMaterial mat) {
        int itemCount = 0;

        if (mat == IdpMaterial.AIR) {
            for (int i = 0; i < items.length; i++) {
                if (items[i] != null && items[i].getMaterial() != IdpMaterial.AIR && items[i].getAmount() == 0) {
                    itemCount++;
                }
            }
        } else {
            for (int i = 0; i < items.length; i++) {
                if (items[i] != null && items[i].getMaterial() != IdpMaterial.AIR) {
                    if (items[i].getMaterial().getBukkitMaterial() == mat.getBukkitMaterial()
                            && (items[i].getData() == mat.getData() || mat.getData() == -1)) {
                        itemCount += items[i].getAmount();
                    }
                }
            }
        }

        return itemCount;
    }

    /**
     * Returns how many items of the specified material may be added from the given count
     * @param count
     * @return
     */
    public int getMaximumAcceptAmount(IdpItem item, int count) {
        int maxAccept = 0;

        for (int i = 0; i < items.length; i++) {
            if (items[i] == null || items[i].getMaterial() == IdpMaterial.AIR) {
                int add = Math.min(count, item.getMaterial().getMaxStackSize());
                maxAccept += add;
                count -= add;
            } else if (items[i].mayStack(item)) {
                int remain = item.getMaterial().getMaxStackSize() - items[i].getAmount();

                if (remain > 0) {
                    int add = Math.min(count, remain);
                    maxAccept += add;
                    count -= add;
                }
            }

            if (count == 0) {
                return maxAccept;
            }
        }

        return maxAccept;
    }

    /**
     * Adds a new ItemStack array to the container
     * @param newitems
     * @return A list representing the item stacks left over
     */
    public List<IdpItemStack> addMaterialsToStack(ItemStack[] newitems) {
        return addMaterialsToStack(IdpItemStack.fromBukkitItemStack(newitems));
    }

    /**
     * Adds the items to the itemstack
     * @param items
     * @return A list representing the item stacks left over
     */
    public List<IdpItemStack> addMaterialsToStack(IdpItemStack[] newitems) {
        List<IdpItemStack> remain = new ArrayList<IdpItemStack>();

        if (newitems.length == 0) {
            return remain;
        }

        for (int i = 0; i < newitems.length; i++) {
            if (newitems[i] == null) {
                continue;
            }

            int leftover = addMaterialToStack(newitems[i], newitems[i].getAmount());

            if (leftover > 0) {
                remain.add(new IdpItemStack(newitems[i], leftover));
            }
        }

        return remain;
    }

    /**
     * Adds a new ItemStack to the container
     * @param newitem
     * @return the amount left over
     */
    public int addMaterialToStack(ItemStack newitem) {
        return addMaterialToStack(IdpItemStack.fromBukkitItemStack(newitem));
    }

    /**
     * Adds a new ItemStack to the container
     * @param newitem
     * @return the amount left over
     */
    public int addMaterialToStack(IdpItemStack newitem) {
        return addMaterialToStack(newitem, newitem.getAmount());
    }

    /**
     * Adds a certain amount of the target material
     * @param count
     * @return the amount left over
     */
    public int addMaterialToStack(IdpItem item, int count) {
        if (item == null || count <= 0) {
            return 0;
        }

        boolean canStack = item.getMaterial().canStack();
        int maxStack = item.getMaterial().getMaxStackSize();
        List<Integer> airIdx = new ArrayList<Integer>();

        boolean isAir;
        int add, idx;
        for (int i = 0; i < items.length; i++) {
            isAir = (items[i] == null || items[i].getMaterial() == IdpMaterial.AIR);

            if (!isAir) {
                // Only process items that are stackable (don't stack metadata as well)
                if (items[i].mayStack(item)) {
                    if (items[i].getMaterial().getBukkitMaterial() == item.getMaterial().getBukkitMaterial()
                            && (items[i].getData() == item.getData() || item.getData() == -1)) {
                        add = Math.min(count, (maxStack - items[i].getAmount()));

                        if (add > 0) {
                            items[i].setAmount(items[i].getAmount() + add);
                            count -= add;

                            if (count <= 0) {
                                return 0;
                            }
                        }
                    }
                }
            } else {
                airIdx.add(i);
            }
        }

        // Process air slots separately
        for (int i = 0; i < airIdx.size(); i++) {
            idx = airIdx.get(i);

            if (canStack) {
                add = Math.min(count, maxStack);
            } else {
                // Dont stack items that cant stack...
                add = 1;
            }

            items[idx] = new IdpItemStack(item, add);
            count -= add;

            if (count <= 0) {
                return 0;
            }
        }

        return count;
    }

    /**
     * Removes the specified amount from the target material in the container
     * <b>NOTE:</b> This does NOT care about metadata
     *
     * @param item
     * @param count
     *
     * @return the amount of items left over, 0 if none
     */
    public int removeMaterialFromStack(IdpItem item, int count) {
        if (count <= 0) {
            return 0;
        }

        for (int i = items.length - 1; i >= 0; i--) {
            if (items[i] != null && items[i].getMaterial() != IdpMaterial.AIR) {
                if (items[i].getMaterial().getBukkitMaterial() == item.getMaterial().getBukkitMaterial()
                        && (items[i].getData() == item.getData() || item.getData() == -1)) {
                    int remove = Math.min(count, items[i].getAmount());
                    items[i].setAmount(items[i].getAmount() - remove);

                    if (items[i].getAmount() <= 0) {
                        items[i] = IdpItemStack.EMPTY_ITEM;
                    }

                    count -= remove;

                    if (count <= 0) {
                        return 0;
                    }
                }
            }
        }

        return count;
    }

    /**
     * Removes the specified amount from the target material in the container
     *
     * @param item
     * @param count
     *
     * @return the first itemstack it finds or null if none
     */
    public IdpItemStack removeFirstMaterialstack(IdpItem item) {
        int data = item.getData();
        boolean canStack = (item.getMaterial().canStack() && item.getItemdata().isEmpty());

        for (int i = items.length - 1; i >= 0; i--) {
            if (items[i] != null && items[i].getMaterial() != IdpMaterial.AIR) {
                if (items[i].getMaterial().getBukkitMaterial() == item.getMaterial().getBukkitMaterial()
                        && (items[i].getData() == data || data == -1 || !canStack)) {
                    IdpItemStack returnstack = items[i];
                    items[i] = null;
                    return returnstack;
                }
            }
        }

        return null;
    }

    /**
     * Retrieves all the items in the container
     *
     * @return
     */
    public IdpItemStack[] getItems() {
        return items;
    }

    /**
     * Gets all items except the armor items
     * @param hotbarFirst returns the hotbar first if true
     * @return
     */
    public IdpItemStack[] getNonArmorItems() {
        // Not a player's inventory
        if (items.length < 36) {
            return null;
        }

        IdpItemStack[] nonArmorItems = new IdpItemStack[36];

        for (int i = 0; i < 36; i++) {
            nonArmorItems[i] = items[i];
        }

        return nonArmorItems;
    }

    /**
     * Gets all the armor items of this container
     * @return
     */
    public IdpItemStack[] getArmorItems() {
        // Not a player's inventory
        if (items.length < 40) {
            return null;
        }

        IdpItemStack[] armorItems = new IdpItemStack[4];

        int idx = 0;

        for (int i = 36; i < 40; i++) {
            armorItems[idx++] = items[i];
        }

        return armorItems;
    }

    /**
     * Gets the offhand item of this inventory, assuming player inventory
     * @return
     */
    public IdpItemStack getOffHandItem() {
        // Not a player's inventory
        if (items.length < 41) {
            return null;
        }

        return items[40];
    }

    /**
     * Gets the item at the specified index
     * @param idx
     * @return
     */
    public IdpItemStack getItemAt(int idx) {
        return items[idx];
    }

    /**
     * Sets the item at the specified index
     * @param idx
     * @param stack
     */
    public void setItemAt(int idx, IdpItemStack stack) {
        items[idx] = stack;
    }

    /**
     * Sorts all item stacks in this container, starting
     * with the first occurrence of each item and expanding
     * that item outward. It will not sort by item ID
     */
    public void sort() {
        List<IdpItemStack> expandedStacks = new ArrayList<IdpItemStack>();

        // First, go through and add all items that are unique
        // including how many of each of them are present
        for (IdpItemStack item : items) {
            // Don't process null stacks
            if (item == null) {
                continue;
            }

            IdpItemStack existingItem = null;

            if (expandedStacks.size() > 0) {
                for (IdpItemStack stack : expandedStacks) {
                    if (stack.mayStack(item)) {
                        existingItem = stack;
                    }
                }
            }

            if (existingItem != null) {
                existingItem.setAmount(existingItem.getAmount() + item.getAmount());
                existingItem = null;
            } else {
                expandedStacks.add(item);
            }
        }

        // Sort all items by item ID
        Collections.sort(expandedStacks, new Comparator<IdpItemStack>() {
            @Override
            public int compare(IdpItemStack stack1, IdpItemStack stack2) {
                IdpMaterial mat1 = stack1.getMaterial();
                IdpMaterial mat2 = stack2.getMaterial();

                if (mat1.getId() > mat2.getId()) {
                    return 1;
                } else if (mat1.getId() < mat2.getId()) {
                    return -1;
                } else {
                    // Sort equal material by data value
                    if (mat1.getData() > mat2.getData()) {
                        return 1;
                    } else if (mat1.getData() < mat2.getData()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            }
        });

        List<IdpItemStack> newItems = new ArrayList<IdpItemStack>();

        for (IdpItemStack stack : expandedStacks) {
            int maxStack = stack.getMaterial().getMaxStackSize();
            int amt = stack.getAmount();

            while (amt > 0) {
                IdpItemStack newStack = new IdpItemStack(stack);
                int amount = Math.min(maxStack, amt);
                newStack.setAmount(amount);
                newItems.add(newStack);
                amt -= amount;
            }
        }

        items = newItems.toArray(new IdpItemStack[newItems.size()]);
    }

    /**
     * Retrieves all the items in the container and converts them to a bukkit
     * ItemStack
     *
     * @return
     */
    public ItemStack[] getBukkitItems() {
        ItemStack[] stack = new ItemStack[items.length];

        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                stack[i] = items[i].toBukkitItemstack();
            }
        }

        return stack;
    }

    /**
     * Returns the size of this container
     * @return
     */
    public int size() {
        return items.length;
    }

    /**
     * Checks if the container is empty
     * @return
     */
    public boolean isEmpty() {
        if (size() == 0) {
            return true;
        }

        int noItemCount = 0;

        for (int i = 0; i < items.length; i++) {
            if (items[i] == null || items[i].getMaterial() == IdpMaterial.AIR) {
                noItemCount++;
            }
        }

        if (noItemCount == items.length) {
            return true;
        }

        return false;
    }
}
