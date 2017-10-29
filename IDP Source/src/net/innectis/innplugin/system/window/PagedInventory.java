package net.innectis.innplugin.system.window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.innectis.innplugin.inventory.IdpContainer;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;

/**
 * A class that has an inventory with multiple
 * pages in it
 *
 * @author AlphaBlend
 */
public class PagedInventory {

    private Map<Integer, IdpItemStack[]> pages = new HashMap<Integer, IdpItemStack[]>();
    private int maxItemsPerPage = 0;

    public PagedInventory(IdpItemStack[] items, int maxItemsPerPage) {
        this.maxItemsPerPage = maxItemsPerPage;
        addNewItems(items);
    }

    /**
     * Gets an item at the specified page and index
     * @param page
     * @param idx
     * @return
     */
    public IdpItemStack getItemAt(int page, int idx) {
        IdpItemStack[] items = pages.get(page - 1);
        return items[idx];
    }

    /**
     * Sets the item at the specified page and index
     * @param page
     * @param idx
     * @param item
     */
    public void setItemAt(int page, int idx, IdpItemStack item) {
        IdpItemStack[] items = pages.get(page - 1);
        items[idx] = item;
    }

    /**
     * Clears an item at the specified page and index
     * @param page
     * @param idx
     */
    public void removeItemAt(int page, int idx) {
        IdpItemStack[] items = pages.get(page - 1);
        items[idx] = null;
    }

    /**
     * Gets an inventory at the specified page
     * @param page
     * @return
     */
    public IdpItemStack[] getInventoryAtPage(int page) {
        return pages.get(page - 1);
    }

    /**
     * Checks if this inventory has a next page based on the
     * page passed in
     * @param page
     * @return
     */
    public boolean hasNextPage(int page) {
        return (pages.size() > page);
    }

    /**
     * Checks if this inventory has a previous page than the
     * one passed in
     * @param page
     * @return
     */
    public boolean hasPreviousPage(int page) {
        return (page > 1);
    }

    /**
     * Adds the new material to this paged inventory
     * @param newItems
     */
    public void addNewItems(IdpItemStack[] newItems) {
        IdpItemStack[] pageItems = null;

        // If there are no pages make sure to create at least one
        if (pages.isEmpty()) {
            pageItems = new IdpItemStack[maxItemsPerPage];
            pages.put(0, pageItems);
        } else {
            pageItems = pages.get(0);
        }

        int startIdx = 0;
        int page = 1;
        int newItemsIdx = 0;
        IdpItemStack currentItem = null;

        while (newItemsIdx < newItems.length) {
            if (currentItem == null) {
                currentItem = newItems[newItemsIdx];

                if (currentItem == null || currentItem.getMaterial() == IdpMaterial.AIR) {
                    // Make sure the item is null;
                    currentItem = null;

                    newItemsIdx++;
                    continue;
                }
            }

            int freeIdx = getFreeSlotForPage(page, startIdx);

            if (freeIdx > -1) {
                startIdx = freeIdx;
                pageItems[freeIdx] = currentItem;
                currentItem = null;

                newItemsIdx++;
            } else {
                startIdx = 0;
                page++;

                if (!pages.containsKey(page - 1)) {
                    pageItems = new IdpItemStack[maxItemsPerPage];
                    pages.put(page - 1, pageItems);
                } else {
                    pageItems = pages.get(page - 1);
                }
            }
        }
     }

    /**
     * Sorts the contents of this paged inventory
     */
    public void sort() {
        IdpContainer container = new IdpContainer(0);

        for (IdpItemStack[] pageContent : pages.values()) {
            container.addMaterial(pageContent);
        }

        container.sort();
        pages.clear();

        IdpItemStack[] pageItems = null;
        int page = 1;
        int idx = 0;

        for (IdpItemStack item : container.getItems()) {
            if (item == null || item.getMaterial() == IdpMaterial.AIR) {
                continue;
            }

            if (!pages.containsKey(page - 1)) {
                pageItems = new IdpItemStack[maxItemsPerPage];
                pages.put(page - 1, pageItems);
            }

            pageItems[idx++] = item;

            if (idx == maxItemsPerPage) {
                idx = 0;
                page++;
            }
        }
    }

    /**
     * Clears the pages
     */
    public void clear() {
        pages.clear();
    }

    /**
     * Counts the amount of individual items in this paged inventory
     * @return
     */
    public int size() {
        int count = 0;

        for (IdpItemStack[] page : pages.values()) {
            for (IdpItemStack stack : page) {
                if (stack == null || stack.getMaterial() == IdpMaterial.AIR) {
                    continue;
                }

                count++;
            }
        }

        return count;
    }

    /**
     * Gets the contents of this paged inventory. Does not
     * include null or air items
     * @return
     */
    public IdpItemStack[] getContents() {
        List<IdpItemStack> contents = new ArrayList<IdpItemStack>();

        for (IdpItemStack[] pageItems : pages.values()) {
            for (IdpItemStack item : pageItems) {
                if (item == null || item.getMaterial() == IdpMaterial.AIR) {
                    continue;
                }

                contents.add(item);
            }
        }

        return contents.toArray(new IdpItemStack[contents.size()]);
    }

    /**
     * Gets a free slot from the specified page or -1 if none found
     *
     * @param page
     * @param startIdx The starting index for this page
     * @return
     */
    private int getFreeSlotForPage(int page, int startIdx) {
        IdpItemStack[] items = pages.get(page - 1);

        for (int i = startIdx; i < maxItemsPerPage; i++) {
            IdpItemStack item = items[i];

            if (item == null || item.getMaterial() == IdpMaterial.AIR) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Gets the page count of this paged inventory
     * @return
     */
    public int getPageCount() {
        return pages.size();
    }

}
