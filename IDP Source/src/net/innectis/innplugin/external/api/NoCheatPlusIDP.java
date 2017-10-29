package net.innectis.innplugin.external.api;

import fr.neatmonster.nocheatplus.NoCheatPlus;
import net.innectis.innplugin.external.api.interfaces.INoCheatPlusIDP;
import net.innectis.innplugin.external.LibraryInitalizationError;
import org.bukkit.plugin.Plugin;

/**
 * API for when NoCheatPlus is loaded
 *
 * @author AlphaBlend
 */
public class NoCheatPlusIDP implements INoCheatPlusIDP {

    private NoCheatPlus noCheatPlusPlugin;

    public NoCheatPlusIDP(Plugin bukkitPlugin) {
        this.noCheatPlusPlugin = (NoCheatPlus) bukkitPlugin;
    }

    @Override
    public void initialize() throws LibraryInitalizationError {
    }

    @Override
    public boolean isAlternative() {
        return false;
    }

    @Override
    public String[] getBannedPlayers() {
        return noCheatPlusPlugin.getLoginDeniedPlayers();
    }

    public void clearBannedPlayers() {
        noCheatPlusPlugin.allowLoginAll();
    }

}
