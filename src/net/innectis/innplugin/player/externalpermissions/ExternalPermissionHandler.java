package net.innectis.innplugin.player.externalpermissions;

import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;

/**
 * @author Hret
 *
 * Manages the permissionmanagers
 */
public class ExternalPermissionHandler {

    private ExternalPermissionHandler() {
    }

    /**
     * Clears the permissions the player has and then recalculates them.
     */
    public static void resetPlayerPermissions(IdpPlayer player) {
        // First clear
        clearPermissions(player);

        // Set op (is has perm)
        if (player.hasPermission(Permission.admin_serveroperator)) {
            player.getHandle().setOp(true);
        }

        // Check for all managers and check the plugins
        for (PluginPermissionManager manager : getManagers()) {
            if (manager != null) {
                manager.givePermissions(player);
            }
        }

    }

    /**
     * Returns an array of plugin managers
     * @return
     */
    private static PluginPermissionManager[] getManagers() {
        return new PluginPermissionManager[]{
                    new MobDisguisePermissionManager(),
                    new WorldEditPermissionManager(),
                    new NoCheatPermissionManager()};
    }

    /**
     * Clears the external permissions the player has
     */
    public static void clearPermissions(IdpPlayer player) {
        player.getHandle().setOp(false);
        for (PermissionAttachmentInfo info : player.getHandle().getEffectivePermissions()) {
            if (info.getAttachment() != null) {
                player.getHandle().removeAttachment(info.getAttachment());
            }
        }
    }
    
}
