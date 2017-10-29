package net.innectis.innplugin.system.window.windows;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.items.ItemData;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.player.PlayerSettings;
import net.innectis.innplugin.system.window.ButtonSettings;
import net.innectis.innplugin.system.window.PagedInventory;
import net.innectis.innplugin.system.window.WindowConstants;
import net.innectis.innplugin.system.window.WindowSettings;
import net.innectis.innplugin.system.window.WindowSystemUtil;
import org.bukkit.GameMode;
import org.bukkit.inventory.Inventory;

/**
 * A class to manage the toggle window
 *
 * @author AlphaBlend
 */
public class ToggleWindow extends Window {

    private PlayerSession session = null;

    public ToggleWindow(PagedInventory toggleInventory, PlayerSession session) {
        super(WindowSettings.TOGGLE_SETTINGS, toggleInventory);
        this.session = session;
    }

    @Override
    public void handlePagedInventoryClick(PagedInventory pagedInventory, Inventory inv, int slot, IdpPlayer player) {
        IdpItemStack stack = IdpItemStack.fromBukkitItemStack(inv.getItem(slot));

        if (stack == null || stack.getMaterial() == IdpMaterial.AIR) {
            return;
        }

        ItemData itemData = stack.getItemdata();
        PlayerSettings setting = null;

        try {
            int settingId = Integer.parseInt(itemData.getValue(WindowSystemUtil.SETTING_IDENTIFIER));
            setting = PlayerSettings.values()[settingId];
        } catch (NumberFormatException nfe) {
            player.printError("Unable to process toggle. Notify an admin!");
            InnPlugin.logError("Unable to manipulate toggle window!", nfe);
            return;
        }

        boolean status = session.hasSetting(setting);
        boolean newStatus = !status;

        session.setSetting(setting, newStatus);

        IdpPlayer targetPlayer = InnPlugin.getPlugin().getPlayer(session.getUniqueId());

        // Apply certain conditions if the target player is online
        if (targetPlayer != null && targetPlayer.isOnline()) {
            // Extra settings
            switch (setting) {
                case SADDLE:
                    // Eject any sitting entity
                    if (!newStatus) {
                        targetPlayer.getHandle().eject();
                    }
                    break;
                case FLIGHT:
                    // Only toggle flight if the world has flight enabled
                    if (targetPlayer.getWorld().getSettings().isFlightAllowed()
                            || targetPlayer.hasPermission(Permission.special_noflight_override)) {
                        // Leave creative game mode alone
                        if (targetPlayer.getHandle().getGameMode() != GameMode.CREATIVE) {
                            targetPlayer.setAllowFlight(newStatus);
                        }
                    } else {
                        return;
                    }

                    break;
            }
        }

        IdpMaterial statusMaterial = null;

        if (setting.isStaffSetting()) {
            statusMaterial = (newStatus ? WindowConstants.SETTING_STAFF_ON : WindowConstants.SETTING_STAFF_OFF);
        } else {
            statusMaterial = (newStatus ? WindowConstants.SETTING_ON : WindowConstants.SETTING_OFF);
        }

        stack.setMaterial(statusMaterial);
        inv.setItem(slot, stack.toBukkitItemstack());
    }

    @Override
    public void handleCustomButtonClick(PagedInventory pagedInventory, Inventory inv, ButtonSettings settings, IdpPlayer player) {
        int windowPage = player.getWindowPage();
        int startSlot = super.getSettings().getInventoryStartIndex();

        for (int page = 1; page <= pagedInventory.getPageCount(); page++) {
            IdpItemStack[] contents = pagedInventory.getInventoryAtPage(page);

            for (int idx = 0; idx < contents.length; idx++) {
                IdpItemStack stack = contents[idx];

                if (stack == null || stack.getMaterial() == IdpMaterial.AIR) {
                    continue;
                }

                ItemData itemData = stack.getItemdata();
                PlayerSettings setting = null;

                try {
                    int settingId = Integer.parseInt(itemData.getValue(WindowSystemUtil.SETTING_IDENTIFIER));
                    setting = PlayerSettings.values()[settingId];
                } catch (NumberFormatException nfe) {
                    player.printError("Unable to process toggle. Notify an admin!");
                    return;
                }

                if (setting != null) {
                    boolean staffSetting = setting.isStaffSetting();

                    if (!staffSetting || session.isStaff()) {
                        session.setSetting(setting, setting.isDefaultOn());

                        IdpMaterial statusMaterial = null;

                        if (staffSetting) {
                            statusMaterial = (setting.isDefaultOn() ? WindowConstants.SETTING_STAFF_ON : WindowConstants.SETTING_STAFF_OFF);
                        } else {
                            statusMaterial = (setting.isDefaultOn() ? WindowConstants.SETTING_ON : WindowConstants.SETTING_OFF);
                        }

                        stack.setMaterial(statusMaterial);

                        if (page == windowPage) {
                            inv.setItem(startSlot + idx, stack.toBukkitItemstack());
                        }
                    }
                }
            }
        }
    }

}
