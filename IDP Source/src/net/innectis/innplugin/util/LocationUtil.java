package net.innectis.innplugin.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.system.warps.WarpHandler;
import net.innectis.innplugin.objects.PortalDestinationResult;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.IdpWorldFactory;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.system.warps.IdpWarp;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.handlers.WaypointHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.InnectisWaypoint;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.system.warps.WarpSettings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

/**
 * Utility functions for locations not found in bukkit
 *
 * @author AlphaBlend
 */
public class LocationUtil {

    /**
     * Gets the center of a location
     * @param loc
     * @return
     */
    public static Location getCenterLocation(World world, int x, int y, int z) {
        return getCenterLocation(world, x, y, z, 0, 0);
    }

    /**
     * Gets the center of a location
     * @param loc
     * @return
     */
    public static Location getCenterLocation(World world, int x, int y, int z, float yaw, float pitch) {
        return getCenterLocation(new Location(world, x, y, z, yaw, pitch));
    }

    /**
     * Gets the center of a location
     * @param loc
     * @return
     */
    public static Location getCenterLocation(Location loc) {
        World bworld = loc.getWorld();
        double x = loc.getBlockX() + 0.5D;
        double y = loc.getBlockY();
        double z = loc.getBlockZ() + 0.5D;

        return new Location(bworld, x, y, z, loc.getYaw(), loc.getPitch());
    }

    /**
     * Subtracts the specified location from the other and returns the
     * result in a vector
     * @param loc
     * @param loc2
     * @return
     */
    public static Vector subtractLocationFromLocationToVector(Location loc, Location loc2) {
        double x1 = loc.getX();
        double y1 = loc.getY();
        double z1 = loc.getZ();

        double x2 = loc2.getX();
        double y2 = loc2.getY();
        double z2 = loc2.getZ();

        double diffX = (x1 - x2);
        double diffY = (y1 - y2);
        double diffZ = (z1 - z2);

        return new Vector(diffX, diffY, diffZ);
    }

    /**
     * Subtracts a vector from a location and returns the result in
     * a location
     * @param loc
     * @param vec
     * @return
     */
    public static Location subtractLocationFromVectorToLocation(Location loc, Vector vec) {
        World world = loc.getWorld();
        double locX = loc.getX();
        double locY = loc.getY();
        double locZ = loc.getZ();

        double vecX = vec.getX();
        double vecY = vec.getY();
        double vecZ = vec.getZ();

        double diffX = (locX - vecX);
        double diffY = (locY - vecY);
        double diffZ = (locZ - vecZ);

        return new Location(world, diffX, diffY, diffZ);
    }

    /**
     * Converts a location to a chunk vector
     * @param loc
     * @return
     */
    public static Vector locationToChunkVector(Location loc) {
        int chunkX = (loc.getBlockX() / 16);
        int chunkY = (loc.getBlockY() / 16);
        int chunkZ = (loc.getBlockZ() / 16);

        return new Vector(chunkX, chunkY, chunkZ);
    }

    /**
     * Converts a location to a chunk coordinate vector
     * @param loc
     * @return
     */
    public static Vector locationToChunkCoordinateVector(Location loc) {
        int coordX = loc.getBlockX();
        int coordY = loc.getBlockY();
        int coordZ = loc.getBlockZ();

        // If not already in chunk form, convert
        if (coordX > 15 || coordY > 15 || coordZ > 15) {
            coordX %= 16;
            coordY %= 16;
            coordZ %= 16;
        }

        return new Vector(coordX, coordY, coordZ);
    }

    /**
     * Translations a sign's location string into a sign object, if it
     * exists at the specified location
     * @param signLocation
     * @return
     */
    public static Sign signLocationStringToSign(String signLocation) {
        // Invalid location string, so return null
        if (!(signLocation.charAt(0) == '(' && signLocation.charAt(signLocation.length() - 1) == ')')) {
            return null;
        }

        String details = signLocation.substring(1, signLocation.length() - 1);

        String[] parts = details.split(",");

        // Invalid sign location string
        if (parts.length != 4) {
            return null;
        }

        String worldName = parts[3].trim();

        World world = Bukkit.getWorld(worldName);

        // World not loaded, so return null
        if (world == null) {
            return null;
        }

        int x = 0;
        int y = 0;
        int z = 0;

        try {
            x = Integer.parseInt(parts[0].trim());
            y = Integer.parseInt(parts[1].trim());
            z = Integer.parseInt(parts[2].trim());
        } catch (NumberFormatException nfe) {
            return null;
        }

        Block block = world.getBlockAt(x, y, z);

        // No block exists here
        if (block == null) {
            return null;
        }

        if (block.getState() instanceof Sign) {
            return (Sign) block.getState();
        } else {
            return null;
        }
    }

    /**
     * Gets a list of all entities at the specified location by range
     * the given range
     * @param location
     * @param type
     * @param x
     * @param y
     * @param z
     * @return
     */

    public static List<Entity> getEntitiesNearLocation(Location location, EntityType type, double x, double y, double z) {
        Collection<Entity> entities = location.getWorld().getNearbyEntities(location, x, y, z);
        List<Entity> foundEntities = new ArrayList<Entity>();

        for (Entity entity : entities) {
            if (entity.getType() == type) {
                foundEntities.add(entity);
            }
        }

        return foundEntities;
    }

    /**
     * Prints out a string representing the location
     * @param loc
     * @return
     */
    public static String locationString(Location loc) {
        World world = loc.getWorld();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        return "(" + x + ", " + y + ", " + z + ", " + world.getName() + ")";
    }

    /**
     * Prints out a string representing the vector
     * @param vec
     * @return
     */
    public static String vectorString(int x, int y, int z) {
        return vectorString(new Vector(x, y, z));
    }

    /**
     * Prints out a string representing the vector
     * @param vec
     * @return
     */
    public static String vectorString(Vector vec) {
        int x = vec.getBlockX();
        int y = vec.getBlockY();
        int z = vec.getBlockZ();

        return "(" + x + ", " + y + ", " + z + ")";
    }

    public static Vector clampY(Vector vec, int min, int max) {
        int x = vec.getBlockX();
        int y = vec.getBlockY();
        int z = vec.getBlockZ();

        return new Vector(x, Math.max(min, Math.min(max, y)), z);
    }

    /**
     * Makes a location int from a IdpVector object.
     * The Vector must be relative in the location.
     * @param vector
     * @return
     */
    public static long vectorToLocation(Vector vector) {
        return ((long) (vector.getBlockY() * 16 + vector.getBlockZ()) * 16 + vector.getBlockX());
    }

    /**
     * Transfers the location offset of the first location onto the other
     * @param original
     * @param destination
     * @return
     */
    public static Location transferOffsetLocation(Location original, Location destination) {
        double offX = (original.getX() - original.getBlockX());
        double offZ = (original.getZ() - original.getBlockZ());

        return new Location(destination.getWorld(), destination.getBlockX() + offX, destination.getBlockY(), destination.getBlockZ() + offZ);
    }

    /**
     * Attempts to get the destination of a portal from
     * where the player entered it
     * @param player
     * @return
     */
    public static PortalDestinationResult getPlayerPortalLocation(IdpPlayer player) {
        Location playerLocation = player.getLocation();
        Block originalBlock = playerLocation.getBlock();
        IdpMaterial originalMaterial = IdpMaterial.fromBlock(originalBlock);

        // If the player is not standing in a portal, then check for a portal around them
        if (originalMaterial != IdpMaterial.PORTAL) {
            Block tempBlock = null;
            double distance = Double.MAX_VALUE;

            for (BlockFace face : BlockHandler.getAllSideFaces()) {
                Block adjacentBlock = originalBlock.getRelative(face);
                IdpMaterial adjacentMaterial = IdpMaterial.fromBlock(adjacentBlock);

                // If this adjacent block is a portal, add it for checking
                if (adjacentMaterial == IdpMaterial.PORTAL) {
                    Location centeredPortalLocation = LocationUtil.getCenterLocation(adjacentBlock.getLocation());
                    double tempDistance = getDistanceToPortal(centeredPortalLocation, face, playerLocation);

                    if (tempDistance < distance) {
                        distance = tempDistance;
                        tempBlock = adjacentBlock;
                    }
                }
            }

            // Minimum distance to be inside a portal
            if (distance <= 0.8D) {
                originalBlock = tempBlock;
            } else {
                // No portal found
                return null;
            }
        }

        BlockFace[] scanFaces = new BlockFace[] {BlockFace.UP, BlockFace.DOWN};
        boolean hasPortalStructure = false;
        boolean portalFound = false;
        Sign destinationSign = null;

        for (BlockFace scanFace : scanFaces) {
            // Portal found, break
            if (portalFound) {
                break;
            }

            Block testBlock = originalBlock;
            IdpMaterial testMaterial = originalMaterial;

            for (int i = 0; i < 30; i++) {
                testBlock = testBlock.getRelative(scanFace);
                testMaterial = IdpMaterial.fromBlock(testBlock);

                if (testMaterial == IdpMaterial.OBSIDIAN) {
                    // We do have a complete portal section
                    hasPortalStructure = true;

                    destinationSign = getDestinationSign(testBlock);

                    if (destinationSign != null) {
                        portalFound = true;
                        break;
                    }
                } else {
                    // There is only a partial portal here
                    if (testMaterial != IdpMaterial.PORTAL) {
                        break;
                    }
                }
            }
        }

        // Portal destination was not found
        if (destinationSign == null) {
            // A partial portal was found with no sign, so return either the
            // location of the nether, or spawn, based on whether the player has
            // access to a lot they're on
            if (hasPortalStructure) {
                InnectisLot testLot = LotHandler.getLot(playerLocation);
                Location destinationLocation = null;

                // Get nether location if player has access to an available lot
                if (testLot != null && testLot.canPlayerAccess(player.getName())) {
                    destinationLocation = IdpWorldFactory.getWorld(IdpWorldType.NETHER).getHandle().getSpawnLocation();
                } else {
                    destinationLocation = WarpHandler.getSpawn(player.getGroup());
                }

                return new PortalDestinationResult(destinationLocation, false);
            } else {
                // We cannot find a destination, so do not continue
                return null;
            }
        } else {
            return getPortalDestination(player, destinationSign);
        }
    }

    /**
     * Calculates the distance from the player to an adjacent portal. Results are not greater
     * than 1.5
     * @param centeredPortalLocation
     * @param portalFace
     * @param playerLocation
     * @return
     */
    private static double getDistanceToPortal(Location centeredPortalLocation, BlockFace portalFace, Location playerLocation) {
        double val1 = 0;
        double val2 = 0;

        switch (portalFace) {
            case NORTH:
            case SOUTH:
                if (portalFace == BlockFace.NORTH) {
                    val1 = playerLocation.getZ();
                    val2 = centeredPortalLocation.getZ();
                } else {
                    val1 = centeredPortalLocation.getZ();
                    val2 = playerLocation.getZ();
                }

                break;
            case EAST:
            case WEST:
                if (portalFace == BlockFace.EAST) {
                    val1 = centeredPortalLocation.getX();
                    val2 = playerLocation.getX();
                } else {
                    val1 = playerLocation.getX();
                    val2 = centeredPortalLocation.getX();
                }
        }

        val1 = Math.abs(val1);
        val2 = Math.abs(val2);

        return val1 - val2;
    }

    /**
     * Attempts to get the sign on any of the four side faces of the
     * block passed in
     *
     * the player to
     * @param player
     * @param block
     * @return
     * @throws SQLException
     */
    private static Sign getDestinationSign(Block block) {
        BlockFace[] faces = BlockHandler.getAllSideFaces();

        for (BlockFace face : faces) {
            Block relativeBlock = block.getRelative(face);
            BlockState state = relativeBlock.getState();

            if (state instanceof Sign) {
                return (Sign) state;
            }
        }

        return null;
    }

    /**
     * Attempts to find the final location based on the current sign
     * @param player
     * @param block
     * @return
     * @throws SQLException
     */
    public static PortalDestinationResult getPortalDestination(IdpPlayer player, Sign sign) {
        String line = sign.getLine(0).toLowerCase();
        boolean isInstant = sign.getLine(2).equalsIgnoreCase("instant");
        Location loc = null;

        // Auto convert signs to the proper format
        if (line.equalsIgnoreCase("warp") || line.equalsIgnoreCase("lot")
                || line.equalsIgnoreCase("waypoint") || line.equalsIgnoreCase("wp")) {
            line = "[" + line + "]";
            sign.setLine(0, line);
            sign.update();
        }


        if (line.equalsIgnoreCase("[warp]")) {
            IdpWarp warp = WarpHandler.getWarp(sign.getLine(1));

            if (warp != null) {
                // Check to make sure the warp is valid
                if (warp.isValid()) {
                    if (!warp.hasSetting(WarpSettings.STAFF_ONLY) || player.getSession().isStaff()) {
                        loc = warp.getLocation();
                    }
                }
            } else {
                // No warp found, is this a lot name?
                InnectisLot lot = LotHandler.getLot(sign.getLine(1));

                if (lot != null) {
                    loc = lot.getSpawn();
                }
            }
        } else if (line.equalsIgnoreCase("[lot]")) {
            InnectisLot lot = null;

            try {
                int lotId = Integer.parseInt(sign.getLine(1));
                lot = LotHandler.getLot(lotId);
            } catch (NumberFormatException ex) {
                String name = sign.getLine(1);
                int lotnr = 1;
                try {
                    lotnr = Integer.parseInt(sign.getLine(2));
                } catch (NumberFormatException nfe) {/* can be ignored */
                }

                lot = LotHandler.getLot(name, lotnr);
            }

            if (lot != null) {
                loc = lot.getSpawn();
            }
        } else if (line.equalsIgnoreCase("[waypoint]")
                || line.equalsIgnoreCase("[wp]")) {
            int getId = 0;

            try {
                getId = Integer.parseInt(sign.getLine(1));
            } catch (NumberFormatException nfe) {
            }

            if (getId > 0) {
                InnectisWaypoint waypoint = WaypointHandler.getWaypoint(getId);

                if (waypoint != null && (waypoint.canPlayerAccess(player.getName())
                        || player.hasPermission(Permission.owned_object_override))) {
                    loc = waypoint.getDestination();
                }
            }
        }

        if (loc == null) {
            loc = WarpHandler.getWarp("spawn").getLocation();
        }

        return new PortalDestinationResult(loc, isInstant);
    }
    
}
