package net.innectis.innplugin.system.warps;

import org.bukkit.Location;

/**
 * @author Lynxy
 *
 * The location of a warp and the comment
 */
public class IdpWarp {

    /** Even though its a location, dont extend it -> you shouldn't be able to modify things */
    private Location location;
    private String name;
    private String comment;
    private long settings;

    /**
     * Makes a new warp objectS
     * @param location
     * @param comment
     */
    @Deprecated
    public IdpWarp(Location location, String comment) {
        this.name = "";
        this.location = location;
        this.comment = comment;
        this.settings = 0;
    }

    /**
     * Makes a new warp object
     * @param name
     * @param location
     * @param comment
     */
    public IdpWarp(String name, Location location, String comment, long settings) {
        this.name = name;
        this.location = location;
        this.comment = comment;
        this.settings = settings;
    }

    /**
     * The name of the warp
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Location of a warp
     * @return
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * Checks if this warp is valid. A warp is not
     * valid if the world it's in is unavailable
     * @return
     */
    public boolean isValid() {
        return (location.getWorld() != null);
    }

    /**
     * The comment of the warp
     * @return
     */
    public String getComment() {
        return this.comment;
    }

    /**
     * Gets the settings of this warp
     * @return
     */
    public long getSettings() {
        return settings;
    }

    /**
     * Sets the specified setting of this warp
     * @param setting
     * @param enable
     */
    public void setSetting(WarpSettings setting, boolean enable) {
        if (enable) {
            settings |= setting.getSettingsBit();
        } else {
            settings &= setting.getSettingsBit();
        }
    }

    /**
     * Checks if this warp has the specified setting
     * @param settings
     * @return
     */
    public boolean hasSetting(WarpSettings settings) {
        return ((this.settings & settings.getSettingsBit()) == settings.getSettingsBit());
    }
    
}
