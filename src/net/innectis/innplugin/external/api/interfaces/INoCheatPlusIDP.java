package net.innectis.innplugin.external.api.interfaces;

import net.innectis.innplugin.external.IExternalLibrary;

/**
 * An interface describing base functionality for the NoCheatPlus API
 *
 * @author AlphaBlend
 */
public interface INoCheatPlusIDP extends IExternalLibrary {

    /**
     * Gets a list of all banned players in NoCheatPlus
     * @return
     */
    public String[] getBannedPlayers();

    /**
     * Clears the list of banned players in NoCheatPlus
     */
    public void clearBannedPlayers();

}
