package net.innectis.innplugin.external.api.interfaces;

import net.innectis.innplugin.external.IExternalLibrary;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.external.MissingDependencyException;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.Location;

/**
 * An interface describing base functionality for the WorldEdit API
 *
 * @author AlphaBlend
 */
public interface IWorldEditIDP extends IExternalLibrary {

    /**
     * Gets WE selection.
     * @param player
     * @return the region or null
     */
    public IdpWorldRegion getSelection(IdpPlayer player);

    /**
     * Sets the selection in WorldEdit
     * @param player
     * @param region
     */
    public void setSelection(IdpPlayer player, IdpWorldRegion region);

    /**
     * Regenerates a region of the specified player
     * @param player
     * @param chunkRegion
     * @throws MissingDependencyException
     */
    public void regenPartially(IdpPlayer player, IdpWorldRegion chunkRegion) throws MissingDependencyException;

    /**
     * Gets the blocktrace from WorldEdit and converts it to a location
     * @param player
     * @param range
     * @return
     */
    public Location getBlockTrace(IdpPlayer player, int range);
    
}
