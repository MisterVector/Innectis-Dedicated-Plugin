package net.innectis.innplugin.external.api.alternate;

import java.util.HashMap;
import java.util.Map;
import net.innectis.innplugin.external.api.interfaces.IWorldEditIDP;
import net.innectis.innplugin.external.LibraryInitalizationError;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.external.MissingDependencyException;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.Location;

/**
 * Alternate API for when WorldEdit is not loaded
 *
 * @author AlphaBlend
 */
public class WorldEditIDPAlternative implements IWorldEditIDP {

    private Map<String, IdpWorldRegion> selections = new HashMap<String, IdpWorldRegion>();

    public WorldEditIDPAlternative() {
    }

    @Override
    public void initialize() throws LibraryInitalizationError {
    }

    @Override
    public boolean isAlternative() {
        return true;
    }

    @Override
    public Location getBlockTrace(IdpPlayer player, int range) throws MissingDependencyException {
        throw new MissingDependencyException();
    }

    @Override
    public IdpWorldRegion getSelection(IdpPlayer player) {
        return selections.get(player.getName());
    }

    @Override
    public void setSelection(IdpPlayer player, IdpWorldRegion region) {
        selections.put(player.getName(), region);
    }

    @Override
    public void regenPartially(IdpPlayer handlingPlayer, IdpWorldRegion chunkRegion) throws MissingDependencyException {
        throw new MissingDependencyException();
    }

}
