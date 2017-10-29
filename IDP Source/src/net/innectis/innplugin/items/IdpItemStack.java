package net.innectis.innplugin.items;

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a stack of BaseItems.
 */
public class IdpItemStack extends IdpItem {

    /** Static itemstack of air, can be used to clear slots. */
    public static final IdpItemStack EMPTY_ITEM = new IdpItemStack(IdpMaterial.AIR, 0);
    /**
     * Amount of an item.
     */
    protected int amount = 1;

    /**
     * Construct the object.
     *
     * @param id
     */
    private IdpItemStack() {
        super(IdpMaterial.AIR);
    }

    /**
     * Construct a clone of the object.
     *
     * @param item
     */
    public IdpItemStack(IdpItemStack item) {
        super(item);
        this.amount = item.amount;
    }

    /**
     * Construct the object.
     *
     * @param id
     */
    public IdpItemStack(IdpItem item, int amount) {
        super(item);
        this.amount = amount;
    }

    /**
     * Construct the object.
     *
     * @param id
     * @param amount
     */
    public IdpItemStack(IdpMaterial mat, int amount) {
        super(mat);
        this.amount = amount;
    }

    /**
     * Construct the object.
     *
     * @param id
     * @param amount
     * @param name
     * @param lores
     */
    public IdpItemStack(IdpMaterial mat, int amount, String name, String[] lores) {
        super(mat);
        this.amount = amount;
        ItemData itemData = new ItemData();

        if (name != null) {
            itemData.setItemName(name);
        }

        if (lores != null && lores.length != 0) {
            itemData.setLore(lores);
        }

        setItemdata(itemData);
    }

    /**
     * Construct the object.
     *
     * @param id
     * @param amount
     * @param name
     * @param lores
     */
    public IdpItemStack(IdpMaterial mat, int amount, String name) {
        this(mat, amount, name, null);
    }

    /**
     * Construct the object.
     *
     * @param mat
     * @param amount
     * @param data
     */
    public IdpItemStack(IdpMaterial mat, int amount, ItemData data) {
        super(mat);
        this.amount = amount;
        super.setItemdata(data);
    }

    /**
     * Construct the object.
     *
     * @param id
     * @param amount
     * @param damage
     * @param data
     */
    public IdpItemStack(IdpMaterial mat, int amount, int data) {
        super(mat, data);
        this.amount = amount;
    }

    /**
     * Construct the object.
     *
     * @param id
     * @param amount
     * @param data
     */
    public IdpItemStack(IdpMaterial mat, int amount, int data, ItemData itemdata) {
        super(mat, data, itemdata);
        this.amount = amount;
    }

    /**
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * Prints out the <i>materialname</i>x<i>amount</i> value of the item.
     * @return
     */
    @Override
    public String toString() {
        return getMaterial().getName() + ":" + getData() + "x" + getAmount();
    }

    /**
     * Returns the bukkit object of this itemstack
     * @return
     */
    @SuppressWarnings("deprecation")
    public ItemStack toBukkitItemstack() {
        if (this.getMaterial() == IdpMaterial.AIR) {
            return new ItemStack(IdpMaterial.AIR.getBukkitMaterial());
        }

        int amount = getAmount();
        int data = getData();

        ItemStack stack = new ItemStack(mat.getBukkitMaterial(), amount, (short) data);
        net.minecraft.server.v1_12_R1.ItemStack notchStack = CraftItemStack.asNMSCopy(stack);
        ItemData itemdata = getItemdata();

        if (itemdata != null && !itemdata.isEmpty()) {
            notchStack.setTag(itemdata.getTag());
        }

        return CraftItemStack.asBukkitCopy(notchStack);
    }

    /**
     * Construct a idp itemstack from an bukkit itemstack.
     * If the bukkit itemstack is null, it will return an IDP itemstack with air as material
     * @param itemstack
     */
    public static IdpItemStack fromBukkitItemStack(ItemStack item) {
        if (item == null || IdpMaterial.fromItemStack(item) == IdpMaterial.AIR) {
            return new IdpItemStack(IdpMaterial.AIR, 0);
        }

        net.minecraft.server.v1_12_R1.ItemStack notchstack = CraftItemStack.asNMSCopy(item);

        if (notchstack == null) {
            return new IdpItemStack(IdpMaterial.AIR, 0);
        }

        IdpMaterial mat = IdpMaterial.fromItemStack(item);
        IdpItemStack ret = new IdpItemStack(mat, item.getAmount(), notchstack.getData());
        ret.setItemdata(new ItemData(notchstack.getTag()));

        return ret;
    }

    /**
     * Construct an idp itemstack from a bukkit itemstack.
     * If the bukkit itemstack is null, it will return an IDP itemstack with air as material
     * @param items
     * @return newstack
     */
    public static IdpItemStack[] fromBukkitItemStack(ItemStack[] items) {
        IdpItemStack[] newstack = new IdpItemStack[items.length];

        for (int i = 0; i < items.length; i++) {
            newstack[i] = fromBukkitItemStack(items[i]);
        }

        return newstack;
    }

    /**
     * Makes an array of bukkit itemstacks converted from the given array.
     * If an item is null or the type is air, the bukkit itemstack will also be set to a null value.
     * @param oldstack
     * @return newstack
     */
    public static ItemStack[] toBukkitItemStack(IdpItemStack[] items) {
        ItemStack[] newstack = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getMaterial() != IdpMaterial.AIR) {
                newstack[i] = items[i].toBukkitItemstack();
            }
        }

        return newstack;
    }

}
