package net.innectis.innplugin.system.window.windows;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.items.ItemData;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.tools.miningstick.MiningStickData;
import net.innectis.innplugin.player.tools.miningstick.MiningStickSettings;
import net.innectis.innplugin.system.window.PagedInventory;
import net.innectis.innplugin.system.window.WindowConstants;
import net.innectis.innplugin.system.window.WindowSettings;
import net.innectis.innplugin.system.window.WindowSystemUtil;
import org.bukkit.inventory.Inventory;

/**
 * A window that manages the mining stick settings
 *
 * @author AlphaBlend
 */
public class MiningStickSettingsWindow extends Window {

    public MiningStickSettingsWindow(PagedInventory miningStickInventory) {
        super(WindowSettings.MINING_STICK_SETTINGS, miningStickInventory);
    }

    @Override
    public void handlePagedInventoryClick(PagedInventory pagedInventory, Inventory inv, int slot, IdpPlayer player) {
        int windowPage = player.getWindowPage();
        int inventorySlot = (slot - super.getSettings().getInventoryStartIndex());

        IdpItemStack stack = pagedInventory.getItemAt(windowPage, inventorySlot);
        ItemData itemdata = stack.getItemdata();
        MiningStickSettings setting = null;

        try {
            int settingId = Integer.parseInt(itemdata.getValue(WindowSystemUtil.SETTING_IDENTIFIER));
            setting = MiningStickSettings.values()[settingId];
        } catch (NumberFormatException nfe) {
            player.printError("Internal server error. Notify an admin!");
            InnPlugin.logError("Error in mining stick settings window!", nfe);
        }

        MiningStickData miningStick = player.getSession().getMiningStickData();
        boolean status = miningStick.hasSetting(setting);
        boolean newStatus = !status;
        miningStick.setSetting(setting, newStatus);
        miningStick.update();

        IdpMaterial statusMaterial = (newStatus ? WindowConstants.SETTING_ON : WindowConstants.SETTING_OFF);
        stack.setMaterial(statusMaterial);

        pagedInventory.setItemAt(windowPage, inventorySlot, stack);
        inv.setItem(slot, stack.toBukkitItemstack());
    }

}
