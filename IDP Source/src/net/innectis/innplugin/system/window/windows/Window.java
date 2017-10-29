package net.innectis.innplugin.system.window.windows;

import net.innectis.innplugin.inventory.IdpInventory;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.items.ItemData;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.system.window.ButtonSettings;
import net.innectis.innplugin.system.window.ButtonType;
import net.innectis.innplugin.system.window.PagedInventory;
import net.innectis.innplugin.system.window.WindowSettings;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

/**
 * A default window class that is used to handle events
 in derived window classes that provide their windowSettings
 *
 * @author AlphaBlend
 */
public abstract class Window {

    private WindowSettings windowSettings = null;
    private PagedInventory pagedInventory = null;

    public Window(WindowSettings windowSettings, PagedInventory pagedInventory) {
        this.windowSettings = windowSettings;
        this.pagedInventory = pagedInventory;
    }

    /**
     * Gets the windowSettings of this window
     * @return
     */
    protected WindowSettings getSettings() {
        return windowSettings;
    }

    /**
     * A method called when this window is first created, will
     * create an inventory that is handed to the player
     * @param player
     * @param title
     * @return
     */
    public IdpInventory createInventory(IdpPlayer player, String title) {
        player.setItemAcquisitionSize(1);
        player.setWindowPage(1);

        IdpInventory inv = new IdpInventory(title, windowSettings.getWindowSize());
        IdpItemStack[] windowItems = buildInventory(1, player);

        for (int i = 0; i < windowItems.length; i++) {
            IdpItemStack stack = windowItems[i];

            if (stack != null) {
                inv.setItem(i, stack);
            }
        }

        return inv;
    }

    /**
     * Handles an inventory click event in this window
     * @param inv
     * @param clickType
     * @param slot
     * @param player
     */
    public void onInventoryClick(Inventory inv, ClickType clickType, int slot, IdpPlayer player) {
        if (slot < windowSettings.getInventoryStartIndex()) {
            if (windowSettings.hasButtonInSlot(slot, player)) {
                ButtonSettings buttonSettings = windowSettings.getButtonSettingsInSlot(slot);
                ButtonType buttonType = buttonSettings.getButtonType();
                IdpItemStack stack = IdpItemStack.fromBukkitItemStack(inv.getItem(slot));

                switch (buttonType) {
                    case PAGE_BACK:
                        // Don't process if no item is here
                        if (stack == null || stack.getMaterial() == IdpMaterial.AIR) {
                            return;
                        }

                        handlePageBack(inv, player);
                        break;
                    case PAGE_FORWARD:
                        // Don't process if no item is here
                        if (stack == null || stack.getMaterial() == IdpMaterial.AIR) {
                            return;
                        }

                        handlePageForward(inv, player);
                        break;
                    case ITEM_ACQUISITION:
                        handleItemAcquisitionChange(inv, clickType, slot, player);
                        break;
                    case CUSTOM:
                        if (buttonSettings.getWindowId() > -1) {
                            handleCustomButtonClick(pagedInventory, inv, buttonSettings, player);
                        }

                        break;
                }
            }
        } else if (slot < windowSettings.getWindowSize()) {
            handlePagedInventoryClick(pagedInventory, inv, slot, player);
        }
    }

    public void handlePagedInventoryClick(PagedInventory pagedInventory, Inventory inv, int slot, IdpPlayer player) {

    }

    public void handleCustomButtonClick(PagedInventory pagedInventory, Inventory inv, ButtonSettings buttonSettings, IdpPlayer player) {

    }

    /**
     * Sets up an item to be placed in an inventory
     * @param setting
     * @param player
     * @return
     */
    private IdpItemStack setItemQualities(ButtonSettings setting, IdpPlayer player) {
        ButtonType type = setting.getButtonType();
        IdpMaterial buttonMaterial = (type.getMaterial() != null ? type.getMaterial() : setting.getMaterial());
        IdpItemStack stack = null;

        if (type == ButtonType.ITEM_ACQUISITION) {
            int itemAcquisitionAmount = player.getItemAcquisitionSize();
            stack = new IdpItemStack(buttonMaterial, itemAcquisitionAmount);
        } else {
            stack = new IdpItemStack(buttonMaterial, 1);
        }

        ItemData itemData = stack.getItemdata();
        itemData.setItemName(setting.getTitle());
        itemData.setLore(setting.getDescription());

        return stack;
    }

    /**
     * Builds an inventory for the specified player
     * @param inventory
     * @param page
     * @param player
     * @return
     */
    private IdpItemStack[] buildInventory(int page, IdpPlayer player) {
        IdpItemStack[] windowItems = new IdpItemStack[windowSettings.getWindowSize()];

        for (ButtonSettings buttonSetting : windowSettings.getButtonSettings()) {
            ButtonType type = buttonSetting.getButtonType();

            // Don't handle these types now
            if (type == ButtonType.PAGE_BACK || type == ButtonType.PAGE_FORWARD) {
                continue;
            }

            // Don't do anything with this button as the player cannot see it
            if (!buttonSetting.canSeeButton(player)) {
                continue;
            }

            IdpItemStack stack = setItemQualities(buttonSetting, player);
            windowItems[buttonSetting.getSlot()] = stack;
        }

        IdpItemStack[] inventoryPageItems = pagedInventory.getInventoryAtPage(page);
        int idx = 0;

        for (int i = windowSettings.getInventoryStartIndex(); i < windowSettings.getWindowSize(); i++) {
            windowItems[i] = inventoryPageItems[idx++];
        }

        if (pagedInventory.hasPreviousPage(page)) {
            if (windowSettings.hasButton(ButtonType.PAGE_BACK)) {
                ButtonSettings buttonSetting = windowSettings.getSettingsByButtonType(ButtonType.PAGE_BACK);
                IdpItemStack stack = setItemQualities(buttonSetting, player);

                // This button uses dynamic lore, so set it here
                ItemData itemData = stack.getItemdata();
                itemData.addLore(ChatColor.YELLOW + "To Page " + (page - 1));

                windowItems[buttonSetting.getSlot()] = stack;
            }
        }

        if (pagedInventory.hasNextPage(page)) {
            if (windowSettings.hasButton(ButtonType.PAGE_FORWARD)) {
                ButtonSettings buttonSetting = windowSettings.getSettingsByButtonType(ButtonType.PAGE_FORWARD);
                IdpItemStack stack = setItemQualities(buttonSetting, player);

                // This button uses dynamic lore, so set it here
                ItemData itemData = stack.getItemdata();
                itemData.addLore(ChatColor.YELLOW + "To Page " + (page + 1));

                windowItems[buttonSetting.getSlot()] = stack;
            }
        }

        return windowItems;
    }

    private void handlePageBack(Inventory inv, IdpPlayer player) {
        int windowPage = player.getWindowPage();
        windowPage--;
        player.setWindowPage(windowPage);

        IdpItemStack[] windowItems = buildInventory(windowPage, player);
        inv.clear();

        for (int i = 0; i < windowItems.length; i++) {
            IdpItemStack stack = windowItems[i];

            if (stack != null) {
                inv.setItem(i, stack.toBukkitItemstack());
            }
        }
    }

    private void handlePageForward(Inventory inv, IdpPlayer player) {
        int windowPage = player.getWindowPage();
        windowPage++;
        player.setWindowPage(windowPage);

        IdpItemStack[] windowItems = buildInventory(windowPage, player);
        inv.clear();

        for (int i = 0; i < windowItems.length; i++) {
            IdpItemStack stack = windowItems[i];

            if (stack != null) {
                inv.setItem(i, stack.toBukkitItemstack());
            }
        }
    }

    private void handleItemAcquisitionChange(Inventory inv, ClickType clickType, int slot, IdpPlayer player) {
        int itemAcquisitionSize = player.getItemAcquisitionSize();
        int boundUpper = (clickType == ClickType.LEFT ? 64 : 1);
        int boundLower = (clickType == ClickType.LEFT ? 1 : 64);

        if (itemAcquisitionSize == boundUpper ) {
            itemAcquisitionSize = boundLower;
        } else {
            if (clickType == ClickType.LEFT) {
                itemAcquisitionSize *= 2;
            } else {
                itemAcquisitionSize /= 2;
            }
        }

        player.setItemAcquisitionSize(itemAcquisitionSize);
        IdpItemStack stack = IdpItemStack.fromBukkitItemStack(inv.getItem(slot));
        stack.setAmount(itemAcquisitionSize);
        inv.setItem(slot, stack.toBukkitItemstack());
    }

}
