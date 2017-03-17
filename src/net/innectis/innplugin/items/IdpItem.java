package net.innectis.innplugin.items;

import java.util.Arrays;
import net.innectis.innplugin.InnPlugin;

/**
 * Represents an item
 *
 */
public class IdpItem {

    protected IdpMaterial mat;
    protected int data;
    private ItemData itemdata;

    /** Private empty constructor */
    private IdpItem() {
        itemdata = new ItemData();
    }

    /**
     * Construct the object.
     *
     * @param an other item to contruct this from
     */
    public IdpItem(IdpItem item) {
        this.mat = item.getMaterial();
        this.data = item.getData();

        byte[] bytes = item.getItemdata().toByte();

        // Clone the item data if applicable
        if (bytes != null) {
            this.itemdata = ItemData.fromByte(bytes);
        } else {
            this.itemdata = new ItemData();
        }
   }

    /**
     * Construct the object.
w     *
     * @param id
     * @param damage
     */
    public IdpItem(IdpMaterial mat, int data, ItemData itemdata) {
        this(mat);
        this.data = data;
        // Make sure itemdata cannot be null
        this.itemdata = itemdata == null ? new ItemData() : itemdata;
    }

    /**
     * Construct the object.
     *
     * @param id
     * @param damage
     */
    public IdpItem(IdpMaterial mat) {
        this(mat, mat.getData());
    }

    /**
     * Construct the object.
     *
     * @param id
     * @param damage
     */
    public IdpItem(IdpMaterial mat, int data) {
        this();
        this.mat = mat;
        this.data = data;
    }

    /**
     * Gets the material of this item
     * @return
     */
    public IdpMaterial getMaterial() {
        return mat;
    }

    /**
     * Sets the material of this item
     * @param mat
     */
    public void setMaterial(IdpMaterial mat) {
        this.mat = mat;
        this.data = mat.getData();
    }

    /**
     * Gets the data or damage value of the item.<br/>
     * This value is for blocks (id &rt; 255) a real data value (0-15). <br/>
     * For items (id > 256) this is the damage value. <br/>
     *
     * @return
     */
    public int getData() {
        return data;
    }

    /**
     * Sets the data of this time
     * @param data
     */
    public void setData(int data) {
        this.data = data;
    }

    /**
     * @param damage the damage to set
     */
    public void setDataDamage(short damage) {
        if (mat.getId() > 255) {
            this.data = damage;
        } else {
            InnPlugin.logError("Setting damage on a block! " + mat.getName() + " (data: " + data + ")");
        }
    }

    /**
     * Returns the itemdata of the object.
     * @return
     */
    public ItemData getItemdata() {
        return itemdata;
    }

    /**
     * Sets the itemdata of the object
     * @param itemdata
     */
    public void setItemdata(ItemData itemdata) {
        this.itemdata = itemdata;
    }

    /**
     * This method will check if the current item has the same id and data value as the given item.
     * As well as checking for wildcard data which is -1, and for checking if the items both have
     * the same item data
     * <p/>
     * If the item material is durable this will always return false.
     * @param item
     * @return true if the items can be stacked, false if not.
     * <p/>
     * <b>Note:</b> this does not check for the max amount of an itemstack!
     *
     */
    public boolean mayStack(IdpItem item) {
        if (item != null) {
            if (mat.getBukkitMaterial() == item.getMaterial().getBukkitMaterial()
                    && (data == item.data || item.data == -1)) {
                // Items that don't stack cannot go together
                if (!item.getMaterial().canStack()) {
                    return false;
                }

                ItemData thisData = getItemdata();
                ItemData thatData = item.getItemdata();

                // Item data is the same
                if (Arrays.equals(thisData.toByte(), thatData.toByte())) {
                    return true;
                }
            }
        }

        return false;
    }
    
}