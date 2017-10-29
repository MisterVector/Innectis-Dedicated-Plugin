package net.innectis.innplugin.handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.inventory.IdpContainer;
import net.innectis.innplugin.items.IdpItem;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.items.ItemData;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.system.window.PagedInventory;
import net.innectis.innplugin.system.window.WindowSettings;
import org.bukkit.inventory.Inventory;

/**
 * The handler for the trash
 *
 * @author AlphaBlend
 */
public class TrashHandler {

    // Indicates how long until the trash is marked for wiping
    public static final long TRASH_LIFE_TIME = (7 * 24 * 60 * 60 * 1000L); // 30 days

    private static final int MAX_STACKS_ALLOWED = 5000;
    private static final List<IdpMaterial> ONE_PAGE_ITEMS = new ArrayList<IdpMaterial>();

    private static PagedInventory trashContents = null;
    private static int viewerCount = 0;

    static {
        ONE_PAGE_ITEMS.add(IdpMaterial.DIRT);
        ONE_PAGE_ITEMS.add(IdpMaterial.COBBLESTONE);
        ONE_PAGE_ITEMS.add(IdpMaterial.ANDESITE);
        ONE_PAGE_ITEMS.add(IdpMaterial.GRANITE);
        ONE_PAGE_ITEMS.add(IdpMaterial.DIORITE);
        ONE_PAGE_ITEMS.add(IdpMaterial.SAND);
        ONE_PAGE_ITEMS.add(IdpMaterial.GRAVEL);
    }

    /**
     * Adds a viewer to the view count
     */
    public static void viewerAdded() {
        viewerCount++;
    }

    /**
     * Removes a viewer from the view count
     */
    public static void viewerRemoved() {
        viewerCount--;
    }

    /**
     * Gets the view count
     * @return
     */
    public static int getViewerCount() {
        return viewerCount;
    }

    /**
     * Clears the trash
     */
    public static void clear() {
        trashContents.clear();
    }

    /**
     * Gets the contents of the trash
     * @return
     */
    public static PagedInventory getTrashContents() {
        return trashContents;
    }

    /**
     * Adds all non-air items from the specified inventory
     * @param inv
     * @return A list of error messages if any items could not be added
     */
    public static List<String> addTrashFromInventory(Inventory inv) {
        List<String> errorMessages = new ArrayList<String>();
        IdpItemStack[] addedItems = IdpItemStack.fromBukkitItemStack(inv.getContents());

        // Don't process if no items have been added
        if (!hasTrashItems(addedItems)) {
            errorMessages.add("No items were added to the trash.");
            return errorMessages;
        }

        // If the trash is being filled after being empty, mark
        // the length of time from when the trash will be wiped
        if (trashContents.size() == 0) {
            setTrashStartTime(System.currentTimeMillis());
        }

        IdpContainer trashContainer = new IdpContainer(MAX_STACKS_ALLOWED);
        int trashCount = trashContents.size();

        if (trashCount > 0) {
            trashContainer.addMaterialsToStack(trashContents.getContents());
        }

        IdpContainer addedItemsContainer = new IdpContainer(addedItems);
        int onePageItemsRemain = 0;

        // Only allow a certain amount of specific materials
        for (IdpMaterial mat : ONE_PAGE_ITEMS) {
            int maxTotalMaterialCount = (mat.getMaxStackSize() * 36);
            int trashItemCount = trashContainer.countMaterial(mat);
            int addedItemCount = addedItemsContainer.countMaterial(mat);

            if ((trashItemCount + addedItemCount) > maxTotalMaterialCount) {
                int remain = ((trashItemCount + addedItemCount) - maxTotalMaterialCount);
                onePageItemsRemain += remain;
                addedItemsContainer.removeMaterialFromStack(new IdpItem(mat), remain);
                errorMessages.add("Could not add " + remain + " remaining item" + (remain > 1 ? "s" : "") + " of " + mat.getName() + ".");
            }
        }

        List<IdpItemStack> remainItems = trashContainer.addMaterialsToStack(addedItems);
        int trashRemain = 0;

        // Calculate how much of the remaining items could
        // not be added to the trash
        for (IdpItemStack remainItem : remainItems) {
            trashRemain += remainItem.getAmount();
        }

        // Sort the trash if no one is viewing it
        if (viewerCount == 0) {
            trashContainer.sort();
        }

        trashContents.clear();
        trashContents.addNewItems(trashContainer.getItems());

        if (trashRemain > 0) {
            trashRemain += onePageItemsRemain;
            errorMessages.add("The trash is full! Unable to add " + trashRemain + " more item" + (trashRemain > 1 ? "s" : "") + ".");
        }

        return errorMessages;
    }

    /**
     * Checks if the items contain a valid material
     * @param items
     * @return
     */
    private static boolean hasTrashItems(IdpItemStack[] items) {
        for (IdpItemStack item : items) {
            if (item == null || item.getMaterial() == IdpMaterial.AIR) {
                continue;
            }

            return true;
        }

        return false;
    }

    /**
     * Deletes the trash items in the database
     */
    public static void deleteTrashItems() {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("DELETE FROM trash_items;");
            statement.execute();
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to delete trash items!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Loads the trash items from database
     */
    public static void loadTrashItems() {
        InnPlugin.logCustom(ChatColor.GREEN, "Loading trash items...");

        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT * FROM trash_items ORDER BY id ASC;");
            set = statement.executeQuery();

            List<IdpItemStack> allItems = new ArrayList<IdpItemStack>();

            while (set.next()) {
                int typeId = set.getInt("typeid");
                int data = set.getInt("data");
                IdpMaterial mat = IdpMaterial.fromID(typeId, data);
                int amount = set.getInt("amount");
                byte[] bytes = set.getBytes("itemdata");
                ItemData itemData = ItemData.fromByte(bytes);
                IdpItemStack stack = null;

                if (itemData != null) {
                    stack = new IdpItemStack(mat, amount, itemData);
                } else {
                    stack = new IdpItemStack(mat, amount);
                }

                allItems.add(stack);
            }

            // Get the max page size of a trash window
            WindowSettings settings = WindowSettings.TRASH;
            trashContents = new PagedInventory(allItems.toArray(new IdpItemStack[allItems.size()]), settings.getPagedInventorySize());
        } catch (SQLException ex) {
            InnPlugin.logError("Could not load trash items!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
            DBManager.closeResultSet(set);
        }
    }

    /**
     * Saves the trash items to the database
     */
    public static void saveTrashItems() {
        PreparedStatement statement = null;

        try {
            deleteTrashItems();

            for (IdpItemStack stack : trashContents.getContents()) {
                statement = DBManager.prepareStatement("INSERT INTO trash_items (typeid, data, amount, itemdata) VALUES (?, ?, ?, ?);");

                IdpMaterial mat = stack.getMaterial();
                int amount = stack.getAmount();
                ItemData itemData = stack.getItemdata();

                statement.setInt(1, mat.getId());
                statement.setInt(2, mat.getData());
                statement.setInt(3, amount);
                statement.setBytes(4, itemData.toByte());
                statement.execute();
                DBManager.closePreparedStatement(statement);
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to save trash items!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Gets the total time the trash can exist before being
     * marked for wiping
     * @return
     */
    public static long getTrashStartTime() {
        try {
            return Long.parseLong(ConfigValueHandler.getValue("trash_start_time"));
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    /**
     * Sets the total time the trash can exist before being
     * marked for wiping

     */
    public static void setTrashStartTime(Long time) {
        ConfigValueHandler.saveValue("trash_start_time", time.toString());
    }
    
}
