package net.innectis.innplugin.external.api;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.world.World;
import net.innectis.innplugin.external.api.interfaces.IWorldEditIDP;
import net.innectis.innplugin.external.LibraryInitalizationError;
import net.innectis.innplugin.external.MissingDependencyException;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

/**
 * API for when WorldEdit is loaded
 *
 * @author AlphaBlend
 */
public class WorldEditIDP implements IWorldEditIDP {

    private WorldEditPlugin worldEditPlugin;

    public WorldEditIDP(Plugin bukkitPlugin) {
        this.worldEditPlugin = (WorldEditPlugin) bukkitPlugin;
    }

    @Override
    public void initialize() throws LibraryInitalizationError {
    }

    @Override
    public boolean isAlternative() {
        return false;
    }

    @Override
    public IdpWorldRegion getSelection(IdpPlayer player) {
        Selection select = worldEditPlugin.getSelection(player.getHandle());
        if (select == null) {
            return null;
        }

        try {
            CuboidRegion region = ((CuboidRegionSelector) select.getRegionSelector()).getRegion();
            return new IdpWorldRegion(player.getWorld().getHandle(), bukkitFromWEVector(region.getPos1()), bukkitFromWEVector(region.getPos2()));
        } catch (IncompleteRegionException ire) {
            return null;
        }
    }

    @Override
    public void setSelection(IdpPlayer player, IdpWorldRegion region) {
        worldEditPlugin.setSelection(player.getHandle(),
                new CuboidSelection(region.getWorld(),
                bukkitToWEVector(region.getPos1()),
                bukkitToWEVector(region.getPos2())));
    }

    @Override
    public void regenPartially(IdpPlayer player, IdpWorldRegion chunkRegion) throws MissingDependencyException {
        if (player == null || !player.isOnline()) {
            return;
        }

        EditSession eSession = createEditSession(player);
        eSession.getWorld().regenerate(idpRegionToWE(chunkRegion), eSession);
    }

    @Override
    public Location getBlockTrace(IdpPlayer player, int range) throws MissingDependencyException {
        Vector vector = getWorldEditPlayer(player).getBlockTrace(100);
        org.bukkit.util.Vector bukkitVector = bukkitFromWEVector(vector);
        return new Location(player.getLocation().getWorld(), bukkitVector.getBlockX(), bukkitVector.getBlockY(), bukkitVector.getBlockZ());
    }

    /// ----------------------------------------------------------------------------------
    /// Convertions
    /// ----------------------------------------------------------------------------------
    /**
     * Gets the worldedit player object
     * @param player
     * @return
     */
    private Player getWorldEditPlayer(IdpPlayer player) {
        return worldEditPlugin.wrapPlayer(player.getHandle());
    }

    /**
     * Creates an editsession for the given player
     * @param player
     * @return
     */
    private EditSession createEditSession(IdpPlayer player) {
        return worldEditPlugin.getSession(player.getHandle()).createEditSession(getWorldEditPlayer(player));
    }

    /**
     * Converts the IDP region to a worldedit region
     * @param region
     * @return
     */
    private Region idpRegionToWE(IdpWorldRegion region) {
        World world = new BukkitWorld(region.getWorld());
        CuboidRegion cubreg = new CuboidRegion(world, bukkitToWEVector(region.getPos1()), bukkitToWEVector(region.getPos2()));
        return cubreg;
    }

    /**
     * Makes an WE vector from a bukkit vector
     * @return
     */
    private Vector bukkitToWEVector(org.bukkit.util.Vector vec) {
        return new Vector(vec.getX(), vec.getY(), vec.getZ());
    }

    /**
     * Makes a bukkit vector from a WE vector
     * @param vec
     * @return
     */
    private org.bukkit.util.Vector bukkitFromWEVector(Vector vec) {
        return new org.bukkit.util.Vector(vec.getX(), vec.getY(), vec.getZ());
    }

}
