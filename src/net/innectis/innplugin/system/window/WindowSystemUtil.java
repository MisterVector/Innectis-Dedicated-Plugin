package net.innectis.innplugin.system.window;

import java.util.ArrayList;
import java.util.List;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.items.ItemData;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.PlayerGroup;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.player.PlayerSettings;
import net.innectis.innplugin.player.tools.miningstick.MiningStickData;
import net.innectis.innplugin.player.tools.miningstick.MiningStickSettings;
import net.innectis.innplugin.util.StringUtil;


/**
 * A util class that converts various pieces of information
 * into paged inventories
 *
 * @author AlphaBlend
 */
public class WindowSystemUtil {

    public static final String SETTING_IDENTIFIER = "settingId";

    public static PagedInventory getInventoryFromPlayerSettings(IdpPlayer player, PlayerSession targetSession) {
        List<IdpItemStack> settingItems = new ArrayList<IdpItemStack>();
        PlayerGroup group = player.getGroup();

        for (PlayerSettings setting : PlayerSettings.getSortedSettings()) {
            // Don't include settings this player is not allowed to use
            if (!group.equalsOrInherits(setting.getRequiredGroup())) {
                continue;
            }

            IdpMaterial statusMaterial = null;

            if (targetSession.hasSetting(setting)) {
                statusMaterial = (setting.isStaffSetting() ? WindowConstants.SETTING_STAFF_ON : WindowConstants.SETTING_ON);
            } else {
                statusMaterial = (setting.isStaffSetting() ? WindowConstants.SETTING_STAFF_OFF : WindowConstants.SETTING_OFF);
            }

            IdpItemStack toggleItem = createSettingItem(statusMaterial, setting.getName(), setting.getDescription(), setting.ordinal());
            settingItems.add(toggleItem);
        }

        WindowSettings windowSetting = WindowSettings.TOGGLE_SETTINGS;
        return new PagedInventory(settingItems.toArray(new IdpItemStack[settingItems.size()]), windowSetting.getPagedInventorySize());
    }

    public static PagedInventory getInventoryFromMiningStickSettings(IdpPlayer player) {
        MiningStickData miningStickData = player.getSession().getMiningStickData();
        List<IdpItemStack> settings = new ArrayList<IdpItemStack>();

        for (MiningStickSettings setting : MiningStickSettings.getSortedSettings()) {
            boolean enabled = miningStickData.hasSetting(setting);
            IdpMaterial statusMaterial = (enabled ? WindowConstants.SETTING_ON : WindowConstants.SETTING_OFF);
            IdpItemStack stack = createSettingItem(statusMaterial, setting.getName(), setting.getDescription(), setting.ordinal());
            settings.add(stack);
        }

        WindowSettings windowSettings = WindowSettings.MINING_STICK_SETTINGS;
        return new PagedInventory(settings.toArray(new IdpItemStack[settings.size()]), windowSettings.getPagedInventorySize());
    }

    /**
     * Creates a setting item from a material, the item's name
     * it's lore, and its setting ID
     */
    private static IdpItemStack createSettingItem(IdpMaterial material, String itemName, String itemLore, int settingId) {
        IdpItemStack settingItem = new IdpItemStack(material, 1);

        ItemData itemData = settingItem.getItemdata();
        itemData.setItemName(itemName);
        String[] stringArray = StringUtil.stringToArray(itemLore, 30);
        itemData.setLore(stringArray);
        itemData.setValue(SETTING_IDENTIFIER, String.valueOf(settingId));

        return settingItem;
    }

}
