package net.innectis.innplugin.player.externalpermissions;

import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Hret
 */
public class MobDisguisePermissionManager extends PluginPermissionManager {

    @Override
    public String getPluginName() {
        return "MobDisguise";
    }

    @Override
    public boolean givePermissions(IdpPlayer player) {
        Plugin plug = getPlugin();

        if (plug == null) {
            return false;
        }

        if (player.hasPermission(Permission.external_mobdisguise_mobs)) {
            setPerm(player, plug, "mobdisguise.*", true);
        }
        if (player.hasPermission(Permission.external_mobdisguise_players)) {
            setPerm(player, plug, "mobdisguise.player", true);
        }

        return true;
    }
    
}
