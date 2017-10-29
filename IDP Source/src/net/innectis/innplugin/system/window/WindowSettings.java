package net.innectis.innplugin.system.window;

import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.PlayerGroup;
import net.innectis.innplugin.system.window.windows.ToggleWindow;
import org.bukkit.ChatColor;

/**
 * An enum with settings specific to a window
 *
 * @author AlphaBlend
 */
public enum WindowSettings {

    TRASH(54, 18,           new ButtonSettings(0, ButtonType.PAGE_BACK, ChatColor.BLUE + "Page Back"),
                            new ButtonSettings(4, ButtonType.ITEM_ACQUISITION, ChatColor.ITALIC + "Item Acquisition", ChatColor.GREEN + "Left-click to increase", ChatColor.GREEN + "Right-click to decrease"),
                            new ButtonSettings(8, ButtonType.PAGE_FORWARD, ChatColor.BLUE + "Page Forward")),

    TOGGLE_SETTINGS(45, 18, new ButtonSettings(0, ButtonType.PAGE_BACK, ChatColor.BLUE + "Page Back"),
                            new ButtonSettings(2, ButtonType.CUSTOM, WindowConstants.SETTING_ON, ChatColor.GREEN + "Enabled Setting", "Indicates that a setting is enabled"),
                            new ButtonSettings(3, ButtonType.CUSTOM, WindowConstants.SETTING_OFF, ChatColor.RED + "Disabled Setting", "Indicates that a setting is disabled"),
                            new ButtonSettings(4, ButtonType.CUSTOM, IdpMaterial.PAPER, 0, ChatColor.AQUA + "Reset Settings", "Restores the settings to", "their default state"),
                            new ButtonSettings(5, PlayerGroup.MODERATOR, ButtonType.CUSTOM, WindowConstants.SETTING_STAFF_ON, ChatColor.GREEN + "Enabled Staff Setting", "Indicates that a staff setting is enabled"),
                            new ButtonSettings(6, PlayerGroup.MODERATOR, ButtonType.CUSTOM, WindowConstants.SETTING_STAFF_OFF, ChatColor.RED + "Disabled Staff Setting", "Indicates that a staff setting is disabled"),
                            new ButtonSettings(8, ButtonType.PAGE_FORWARD, ChatColor.BLUE + "Page Forward")),
    MINING_STICK_SETTINGS(27, 18, new ButtonSettings(2, ButtonType.CUSTOM, WindowConstants.SETTING_ON, ChatColor.GREEN + "Enabled Setting", "Indicates that a setting is enabled"),
                                  new ButtonSettings(3, ButtonType.CUSTOM, WindowConstants.SETTING_OFF, ChatColor.RED + "Disabled Setting", "Indicates that a setting is disabled"));

    private int windowSize = 0;
    private int inventoryStartIndex = 0;
    private ButtonSettings[] settings = null;

    private WindowSettings(int windowSize, int inventoryStartIndex, ButtonSettings... settings) {
        this.windowSize = windowSize;
        this.inventoryStartIndex = inventoryStartIndex;
        this.settings = settings;
    }

    /**
     * Gets the size of the window
     * @return
     */
    public int getWindowSize() {
        return windowSize;
    }

    /**
     * Gets the part of the window that will contain the inventory
     * @return
     */
    public int getInventoryStartIndex() {
        return inventoryStartIndex;
    }

    /**
     * Gets the size of the paged inventory
     * @return
     */
    public int getPagedInventorySize() {
        return (windowSize - inventoryStartIndex);
    }

    /**
     * Gets the button settings for this window
     * @return
     */
    public ButtonSettings[] getButtonSettings() {
        return settings;
    }

    /**
     * Checks if a button exists in the specified slot
     * @param slot
     * @param player
     * @return
     */
    public boolean hasButtonInSlot(int slot, IdpPlayer player) {
        for (ButtonSettings setting : settings) {
            if (setting.getSlot() == slot) {
                return setting.canSeeButton(player);
            }
        }

        return false;
    }

    /**
     * Gets the button settings in the specified slot
     * @param slot
     * @return
     */
    public ButtonSettings getButtonSettingsInSlot(int slot) {
        for (ButtonSettings setting : settings) {
            if (setting.getSlot() == slot) {
                return setting;
            }
        }

        return null;
    }

    /**
     * Checks if the specified button type exists
     * @param type
     * @return
     */
    public boolean hasButton(ButtonType type) {
        for (ButtonSettings setting : settings) {
            if (setting.getButtonType() == type) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets a button setting by its type
     * @param type
     * @return
     */
    public ButtonSettings getSettingsByButtonType(ButtonType type) {
        for (ButtonSettings setting : settings) {
            if (setting.getButtonType() == type) {
                return setting;
            }
        }

        return null;
    }

}