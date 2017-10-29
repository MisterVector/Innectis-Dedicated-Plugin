package net.innectis.innplugin.location;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.util.LocationUtil;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.util.Vector;

/**
 *
 * @author Hret
 *
 * An region in a specific world
 */
public class IdpWorldRegion extends IdpRegion {

    private World world;

    public IdpWorldRegion(World world, Location loc) {
        this(world, loc.toVector());
    }

    public IdpWorldRegion(World world, Location loc1, Location loc2) {
        this(world, loc1.toVector(), loc2.toVector());
    }

    public IdpWorldRegion(World world, Vector pos) {
        super(pos);
        this.world = world;
    }

    public IdpWorldRegion(World world, Vector pos1, Vector pos2) {
        super(pos1, pos2);
        this.world = world;
    }

    public IdpWorldRegion(World world, IdpRegion region) {
        super(region);
        this.world = world;
    }

    /**
     * This gets the location of the first position of this object.
     * @return
     */
    public Location getPos1Location() {
        Vector pos1 = getPos1();

        int x = pos1.getBlockX();
        int y = pos1.getBlockY();
        int z = pos1.getBlockZ();

        return new Location(world, x, y, z);
    }

    /**
     * This gets the location of the second position of this object.
     * @return
     */
    public Location getPos2Location() {
        Vector pos2 = getPos2();

        int x = pos2.getBlockX();
        int y = pos2.getBlockY();
        int z = pos2.getBlockZ();

        return new Location(world, x, y, z);
    }

    public Location getCenterSafe() {
        Location center = getCenter();
        boolean lastWasAir = false, found = false;
        for (int y = center.getBlockY(); y < world.getMaxHeight(); y++) {
            IdpMaterial mat = IdpMaterial.fromBlock(world.getBlockAt(center.getBlockX(), y, center.getBlockZ()));

            if (InnPlugin.getPlugin().getIgnoreBlocksLoS().contains(mat)) {
                if (lastWasAir) {
                    center.setY(y - 1);
                    found = true;
                    break;
                } else {
                    lastWasAir = true;
                }
            }
        }
        if (!found) {
            center.setY(center.getWorld().getHighestBlockYAt(center) + 2);
        }
        return center;
    }

    /**
     * Returns true if this region contains the specified region
     * @param region
     */
    public boolean contains(IdpWorldRegion region) {
        if (!getWorld().equals(region.getWorld())) {
            return false;
        }

        return super.contains(region);
    }

    /**
     * Makes a string of the location
     * @return
     */
    @Override
    public String getPos1String() {
        return LocationUtil.vectorString(getPos1());
    }

    /**
     * Makes a string of the location
     * @return
     */
    @Override
    public String getPos2String() {
        return LocationUtil.vectorString(getPos2());
    }

    /**
     * Sends a message to all players inside the region
     * @param message
     * The message to send.
     * @param distanceFromRegion
     * Distance from the region this method can ignore.
     */
    public void sendMessageToNearbyPlayers(String message, int distanceFromRegion) {
        for (IdpPlayer player : getPlayersInsideRegion(distanceFromRegion)) {
            player.printRaw(message);
        }
    }

    /**
     * Returns a list of all player that are in the region. <br/>
     * Note: This does the same thing as getPlayersInsideRegion(0);
     * @param distanceFromRegion
     * Here you can specify the distance outside the region that also need to be included.
     * @return
     * List of players inside the region
     */
    public List<IdpPlayer> getPlayersInsideRegion() {
        return getPlayersInsideRegion(0);
    }

    /**
     * Returns a list of all player that are in (or near) the region
     * @param distanceFromRegion
     * Here you can specify the distance outside the region that also need to be included.
     * @return
     * List of players inside the region
     */
    public List<IdpPlayer> getPlayersInsideRegion(int distanceFromRegion) {
        List<IdpPlayer> regionPlayers = new ArrayList<IdpPlayer>();

        for (Iterator<Player> it = getWorld().getPlayers().iterator(); it.hasNext();) {
            Player play = it.next();
            // Check if player is inside the fake region
            if (super.contains(play.getLocation(), distanceFromRegion)) {
                regionPlayers.add(new IdpPlayer(InnPlugin.getPlugin(), play));
            }
        }
        return regionPlayers;
    }

    /**
     * Returns the world
     * @return
     */
    public World getWorld() {
        return world;
    }

    /**
     * Sets the world
     * @param world
     */
    public void setWorld(World world) {
        this.world = world;
    }

    public Location getCenter() {
        return new Location(world, getCenterX(), getCenterY(), getCenterZ());
    }

    /**
     * Sets the biome in the selected region.
     * <p/>
     * <b>The full Y-ax biome will be changed!</b>
     *
     * @param biome
     */
    public void setBiome(IdpBiome biome) {
        for (int x = getLowestX(); x <= getHighestX(); x++) {
            for (int z = getLowestZ(); z <= getHighestZ(); z++) {
                world.setBiome(x, z, biome.getBukkitBiome());
            }
        }
    }

    /**
     * Returns a list of all entities inside this region based on
     * the following filter. Players are explicitly not included
     * @param filterClazz
     * @return
     */
    public List<Entity> getEntities(Class<? extends Entity> filter) {
        return getEntities(filter, 0);
    }

    /**
     * Returns a list of all entities inside this region based on
     * the following filter. Players are explicitly not included
     * @param filterClazz
     * @param leaveAmount specifies how many entities to leave
     * @return
     */
    public List<Entity> getEntities(Class<? extends Entity> filter, int leaveAmount) {
        return getEntities(filter, false, leaveAmount);
    }

    /**
     * Returns a list of entities inside this region based on
     * the following filter. Players are explicitly not included
     * @param filterClazz
     * @param allowTamed allows tamed animals to be included
     * @return
     */
    public List<Entity> getEntities(Class<? extends Entity> filterClazz, boolean allowTamed, int leaveAmount) {
        List<Entity> entities = new ArrayList<Entity>();

        for (IdpVector2D chunk : getChunks()) {
            Chunk bukkitChunk = world.getChunkAt(chunk.getBlockX(), chunk.getBlockZ());

            for (Entity entity : bukkitChunk.getEntities()) {
                // Never process players
                if (entity instanceof Player) {
                    continue;
                }

                // Make sure the entity exists in this region (may be a chunk
                // whose area is outside the region)
                if (!contains(entity.getLocation())) {
                    continue;
                }

                boolean valid = false;

                if (filterClazz != null) {
                    // Special case monster, since slimes and ghasts don't extend the monster class
                    boolean isMonster = (filterClazz == Monster.class && (entity instanceof Monster
                            || entity instanceof Ghast || entity instanceof Slime
                            || entity instanceof Shulker));

                    if (isMonster || filterClazz.isAssignableFrom(entity.getClass())) {
                        if (entity instanceof Tameable) {
                            Tameable tameable = (Tameable) entity;

                            if (!tameable.isTamed() || allowTamed) {
                                valid = true;
                            }
                        } else {
                            valid = true;
                        }
                    }
                } else {
                    // No filter, so grab all entities
                    valid = true;
                }

                if (valid) {
                    if (leaveAmount > 0) {
                        leaveAmount--;
                        continue;
                    }

                    entities.add(entity);
                }
            }
        }

        return entities;
    }

    @Override
    protected void recalculate() {
        setPos1(LocationUtil.clampY(getPos1(), 0, 255));
        setPos2(LocationUtil.clampY(getPos2(), 0, 255));
    }

    /**
     * This will return a snapshot of the region that can be restored later.
     * @return
     */
    public RegionSnapshot getSnapshot() {
        return new RegionSnapshot(this);
    }
    
}
