package net.innectis.innplugin.location;

import net.innectis.innplugin.system.warps.IdpWarp;
import java.util.ArrayList;
import java.util.List;
import net.innectis.innplugin.system.warps.WarpHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 *
 * @author Hret
 *
 * @TODO: delegate to util classes
 */
public class IdpWorld {

    /** Bukkit world object */
    private World world;
    /** The settings */
    private IdpWorldSettings settings;
    /** The worldtype */
    private IdpWorldType worldtype;

    public IdpWorld(World world) {
        this.world = world;
    }

    /**
     * This will return the worldtype or the acting worldtype if this world
     * is an dynamic world.
     * <p/>
     * If the worldtype is dynamic the parent will be taken from the settings.
     * @return
     */
    public IdpWorldType getActingWorldType() {
        if (getWorldType() == IdpWorldType.DYNAMIC) {
            return ((IdpDynamicWorldSettings) getSettings()).getSettingsparent();
        }
        return getWorldType();
    }

    /**
     * The IDP worldtype of this world
     * @return
     */
    public IdpWorldType getWorldType() {
        if (worldtype == null) {
            worldtype = IdpWorldType.getIdpWorldtype(this);
        }
        return worldtype;
    }

    /**
     * Get the settings object for this world
     * @return
     */
    public IdpWorldSettings getSettings() {
        if (settings == null) {
            settings = IdpWorldFactory.getSettings(this);
        }
        return settings;
    }

    /**
     * Looks up the spawnlocation of this world.
     * @return The spawnlocation or NULL if none.
     */
    public Location getSpawnlocation() {
        IdpWarp warp = WarpHandler.getWarp("spawn_" + getName());
        return warp != null ? warp.getLocation() : null;
    }

    /**
     * Saves the world
     */
    public void saveWorld() {
        world.save();
    }

    /**
     * Get the world handle.
     *
     * @return
     */
    public World getHandle() {
        return world;
    }

    /**
     * Get the name of the world
     *
     * @return the worldname
     */
    public String getName() {
        return world.getName();
    }

    /**
     * Gets the amount of players in this world
     * @return amount of players
     */
    public int getPlayerCount() {
        return world.getPlayers().size();
    }

    /**
     * Returns the max height of the world
     * @return
     */
    public int getMaxHeight() {
        return world.getMaxHeight();
    }

    /**
     * All the players that are online in this world
     * @return
     */
    public List<IdpPlayer> getPlayers() {
        List<IdpPlayer> players = new ArrayList<IdpPlayer>();
        for (Player p : world.getPlayers()) {
            players.add(InnPlugin.getPlugin().getPlayer(p));
        }
        return players;
    }

    /**
     * Checks if the worlds are the same world
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IdpWorld) {
            return ((IdpWorld) obj).getName().equalsIgnoreCase(getName());
        }

        if (obj instanceof World) {
            return ((World) obj).getName().equalsIgnoreCase(getName());
        }

        return false;
    }

    // <editor-fold defaultstate="collapsed" desc="IdpBlock Actions">
    /**
     * Sets a block's type
     * @param pt
     * @param mat
     * @return
     */
    public void setBlockType(Vector pt, IdpMaterial mat) {
        world.getBlockAt(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ()).setType(mat.getBukkitMaterial());
    }

    /**
     * Get block
     *
     * @param pt
     * @return the block
     */
    public Block getBlockAt(Vector pt) {
        return getBlockAt(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ());
    }

    /**
     * Get block
     *
     * @param x
     * @param y
     * @param z
     * @return the block
     */
    public Block getBlockAt(int x, int y, int z) {
        return world.getBlockAt(x, y, z);
    }
    // </editor-fold>

    /**
     * Sets the biome on the given location
     * <p/>
     * <b>The full Y-ax biome will be changed!</b>
     *
     * @param x
     * @param z
     * @param biome
     */
    public void setBiome(int blockX, int blockZ, IdpBiome biome) {
        if (biome.isWorldAllowed(this)) {
            this.getHandle().setBiome(blockX, blockZ, biome.getBukkitBiome());
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Weather/time settings">
    /**
     * The time value currently for this world
     * @return
     */
    public long getTime() {
        return getHandle().getTime();
    }

    /**
     * Sets the storm status on this world
     * @param storming
     */
    public void setStorm(boolean storming) {
        getHandle().setStorm(storming);
    }

    /**
     * Sets the thunder status on this world
     * @param thundering
     */
    public void setThundering(boolean thundering) {
        getHandle().setStorm(thundering);
    }

    /**
     * Clears the storm or thunder on this world.
     */
    public void clearWeather() {
        setStorm(false);
        setThundering(false);
    }
    //</editor-fold>

    /**
     * Drops the item at the given location.
     * <p/>
     * When the given itemstack is IdpMaterial.AIR nothing will be done
     *
     * @param location
     * @param item
     */
    public void dropItem(Location location, IdpItemStack item) {
        if (item.getMaterial() == IdpMaterial.AIR) {
            return;
        }
        getHandle().dropItem(location, item.toBukkitItemstack());
    }

}
