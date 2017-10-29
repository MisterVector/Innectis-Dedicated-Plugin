package net.innectis.innplugin.system.warps;

/**
 * An enum listing all settings that warps can have
 *
 * @author AlphaBlend
 */
public enum WarpSettings {

    HIDDEN(1),
    STAFF_ONLY(2);

    private int settingBit;

    private WarpSettings(int settingBit) {
        this.settingBit = settingBit;
    }

    /**
     * Gets the settings bit associated with this warp setting
     * @return
     */
    public int getSettingsBit() {
        return settingBit;
    }

    /**
     * Gets a warp settings object from the specified bit
     * @param settingsBit
     * @return
     */
    public static WarpSettings fromSettingsBit(int settingsBit) {
        for (WarpSettings ws : values()) {
            if (ws.getSettingsBit() == settingsBit) {
                return ws;
            }
        }

        return null;
    }

}
