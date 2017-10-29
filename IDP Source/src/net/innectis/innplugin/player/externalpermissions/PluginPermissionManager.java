package net.innectis.innplugin.player.externalpermissions;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Hret
 *
 * Abstract class to give permissions to players
 */
public abstract class PluginPermissionManager {

    /**
     * Returns an array of the plugins that are on the server
     * @return
     */
    protected static Plugin[] getPlugins() {
        return InnPlugin.getPlugin().getServer().getPluginManager().getPlugins();
    }

    /**
     * Returns the plugin instance of this plugin this manager manages
     * @return Null if not found.
     */
    protected Plugin getPlugin() {
        for (Plugin plugin : getPlugins()) {
            if (plugin.getName().equalsIgnoreCase(getPluginName())) {
                return plugin;
            }
        }
        return null;
    }

    /**
     * The name of the plugin this manager works for
     * @return
     */
    public abstract String getPluginName();

    /**
     * Gives the permissions the player has access to.
     * @param player
     * @return true if succeed.
     */
    public abstract boolean givePermissions(IdpPlayer player);

    /**
     * Sets the permission for the given player.
     * @param player - The player itself
     * @param plugin - The plugin the permission belongs to
     * @param permission - Name of the permission
     * @param allow - If the permission is allowed
     */
    protected static void setPerm(IdpPlayer player, Plugin plugin, String permission, boolean allow) {
        player.getHandle().addAttachment(plugin, permission, allow);
    }
    
}
