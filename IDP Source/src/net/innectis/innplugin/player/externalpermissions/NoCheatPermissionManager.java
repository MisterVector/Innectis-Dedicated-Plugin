package net.innectis.innplugin.player.externalpermissions;

import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Hret
 */
public class NoCheatPermissionManager extends PluginPermissionManager {

    @Override
    public String getPluginName() {
        return "NoCheat";
    }

    @Override
    public boolean givePermissions(IdpPlayer player) {
        Plugin plug = getPlugin();

        if (plug == null) {
            return false;
        }

        if (player.hasPermission(Permission.external_nocheat_nomessagelimit)) {
            setPerm(player, plug, "nocheatplus.checks.chat.nopwnage", true);
        }
        if (player.hasPermission(Permission.external_nocheat_spamfilter_override)) {
            setPerm(player, plug, "nocheatplus.checks.chat.globalchat", true);
        }

        return true;
    }
    
}
