package net.innectis.innplugin.location;

import net.innectis.innplugin.objects.EnderChestContents.EnderContentsType;
import net.innectis.innplugin.location.worldgenerators.MapType;
import net.innectis.innplugin.player.InventoryType;

/**
 * @author Hret
 * <p/>
 *
 * Worldsettings object that keeps track of the settings for a dynamicly loaded world.
 */
public class IdpDynamicWorldSettings extends IdpWorldSettingsImpl {

    private IdpWorldType settingsparent;
    private Boolean allowCommands;
    private Boolean allowWorldedit;
    private Boolean hardcoreMode;
    private Boolean canBuild;
    private Boolean hasHunger;
    private Boolean tntAllowed;

    /**
     *
     * This will create a new worldsettingsobject for a dynamic world.
     *
     * @param worldid
     * @param worldName
     * @param maptype
     * @param inventory
     * @param endertype
     * @param worldsize
     * @param unloadTime
     * @param settingsparent
     * @param allowCommands
     * @param allowWorldedit
     * @param hardcoreMode
     * @param canBuild
     * @param hasHunger
     * @param tntAllowed
     */
    public IdpDynamicWorldSettings(String worldName, MapType maptype, InventoryType inventory, EnderContentsType endertype, int worldsize, long unloadTime,
            IdpWorldType settingsparent, Boolean allowCommands, Boolean allowWorldedit, Boolean hardcoreMode, Boolean canBuild, Boolean hasHunger, Boolean tntAllowed) {
        super(worldName, maptype, inventory, endertype, worldsize, unloadTime);
        this.settingsparent = settingsparent;
        this.allowCommands = allowCommands;
        this.allowWorldedit = allowWorldedit;
        this.hardcoreMode = hardcoreMode;
        this.canBuild = canBuild;
        this.hasHunger = hasHunger;
        this.tntAllowed = tntAllowed;
    }

    /**
     * Sets the worldid if it wasn't already set.
     * Note: this can only be done once.
     * @param worldid
     */
    void setWorldid(int worldid) {
        if (this.worldid < 0) {
            this.worldid = worldid;
        }
    }

    /**
     * Shows if commands are allowed to be used in this world.
     * @return the allowCommands
     */
    public Boolean hasCommandsAllowed() {
        return allowCommands;
    }

    /**
     * Shows if worldedit can be used in this world.
     * @return the allowWorldedit
     */
    public Boolean isWorldeditAllowed() {
        return allowWorldedit;
    }

    /**
     * If the world is in hardcore mode. <br/>
     * Players can only join once in a hardcore mode world.
     * @return the isHardcore
     */
    public Boolean isHardcore() {
        return hardcoreMode;
    }

    /**
     * The settings if players can build on this world
     * @return
     */
    public boolean isBuildable() {
        return canBuild;
    }

    /**
     * The settings if players can build on this world
     * @return
     */
    public boolean isTntAllowed() {
        return tntAllowed;
    }

    /**
     * The setting if hunger is enabled on this world.
     * @return
     */
    public boolean hasHunger() {
        return hasHunger;
    }

    /**
     * The settings it need to take over in the listeners.
     * If DYNAMIC then it wont do anything.
     * @return the settingsparent
     */
    public IdpWorldType getSettingsparent() {
        return settingsparent;
    }
    
}
