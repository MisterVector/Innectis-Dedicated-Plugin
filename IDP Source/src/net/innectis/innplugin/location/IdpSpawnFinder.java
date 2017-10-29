package net.innectis.innplugin.location;

import net.innectis.innplugin.items.IdpMaterial;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 *
 * @author Hret
 */
public class IdpSpawnFinder {

    /** The location */
    private Location location;

    /** The difference in height */
    private int heightDifference = 0;

    // Indicates the original location is used
    private boolean originalLocation;

    /**
     * Makes a spotfinder object
     * @param location
     */
    public IdpSpawnFinder(Location location) {
        this.location = location;
        originalLocation = false;
    }

    /**
     * Finds a spawn location that is the closest to the location.<br/>
     * It will only look up and down (if onlyUp is false)
     * @param onlyUp
     * @return Spawn location, or null if none found.
     */
    public Location findClosestSpawn(boolean onlyUp) {
        Location destLocation = null;
        Block block = location.getBlock();
        IdpMaterial mat = IdpMaterial.fromBlock(block);
        IdpMaterial aboveMat = IdpMaterial.fromBlock(block.getRelative(BlockFace.UP));

        // Check upper blocks if the two free blocks are solid, or if forced
        if (onlyUp || mat.isSolid() || aboveMat.isSolid()) {
            destLocation = findSafeBlockUpper(block);
        } else {
            destLocation = findSafeBlockLower(block);
        }

        destLocation.setYaw(location.getYaw());
        destLocation.setPitch(location.getPitch());

        // If this is not the original location, then add the original
        // location offsets to the new location
        if (!originalLocation) {
            double offX = (location.getX() - location.getBlockX());
            double offZ = (location.getZ() - location.getBlockZ());
            destLocation.add(offX, 0, offZ);
        }

        return destLocation;
    }

    /**
     * Finds a safe spawn location by moving up the y coordinate to a safe spot
     * @param includeFluid
     * @return
     */
    private Location findSafeBlockUpper(Block block) {
        World world = block.getWorld();

        Location safeLocation = null;

        int maxY = world.getHighestBlockYAt(block.getLocation());
        int curY = block.getY();

        // Test location has a block at the highest Y coordinate, so place
        // location one above that
        if (curY == maxY) {
            heightDifference = 1;
            return block.getRelative(BlockFace.UP).getLocation();
        }

        int pts = 0;
        IdpMaterial mat = IdpMaterial.fromBlock(block);

        if (mat.isSolid() || mat.isWater()) {
            pts++;
        }

        while (curY < maxY) {
            block = block.getRelative(BlockFace.UP);
            curY++;
            mat = IdpMaterial.fromBlock(block);

            if (pts > 0) {
                if (!mat.isSolid()) {
                    // First point, valid spawn location, second point, confirmed safe spawn
                    pts++;

                    if (pts == 2) {
                        safeLocation = block.getLocation();
                    } else {
                        heightDifference = (safeLocation.getBlockY() - location.getBlockY());
                        return safeLocation;
                    }
                } else {
                    safeLocation = null;
                    pts = 1; // back to first point, solid block is here
                }
            } else {
                if (mat.isSolid() || mat.isWater()) {
                    pts++;
                }
            }

            // If we have a safe location and our search ends prematurely, assume
            // this location is really safe
            if (curY == maxY && safeLocation != null) {
                heightDifference = (safeLocation.getBlockY() - location.getBlockY());
                return safeLocation;
            }
        }

        originalLocation = true;

        // Only return original location if no newer safe spawn found
        return location;
    }

    /**
     * Finds a safe spawn location by moving down the y coordinate to a safe spot
     * @return
     */
    private Location findSafeBlockLower(Block block) {
        // Start 1 block higher for proper detection
        Location safeLocation = null;
        int pts = 0;

        int curY = block.getY();

        // If the location is at bedrock level, return location
        if (curY == 0) {
            return block.getLocation();
        }

        block = block.getRelative(BlockFace.UP);

        curY++;
        IdpMaterial mat = IdpMaterial.fromBlock(block);
        IdpMaterial matAbove = null;

        if (!mat.isSolid()) {
            pts++;
        }

        while (curY > 0) {
            block = block.getRelative(BlockFace.DOWN);
            mat = IdpMaterial.fromBlock(block);
            curY--;

            if (pts > 0) {
                // Check non solid first, then solid or water next
                boolean isSafe = (pts == 1 ? !mat.isSolid() : mat.isSolid() || mat.isWater());

                if (isSafe) {
                    // First point, valid spawn location, second point, confirmed safe spawn
                    pts++;

                    if (pts == 2) {
                        safeLocation = block.getLocation();
                    } else {
                        heightDifference = (location.getBlockY() - safeLocation.getBlockY());
                        return safeLocation;
                    }
                } else {
                    pts = 0;
                    safeLocation = null;

                    // re-check for non-solid
                    if (!mat.isSolid()) {
                        pts++;

                        // Check above material to check if safe zone
                        if (matAbove != null && !matAbove.isSolid()) {
                            // This is now considered the safe location
                            safeLocation = block.getLocation();
                            pts++;
                        }
                    }
                }
            } else {
                if (!mat.isSolid()) {
                    pts++;
                }
            }

            // If we have a safe location and our search ends prematurely, assume
            // this location is really safe
            if (curY == 0 && safeLocation != null) {
                heightDifference = (location.getBlockY() - safeLocation.getBlockY());
                return safeLocation;
            }

            // Keep reference to material above
            matAbove = mat;
        }

        originalLocation = true;

        // Only return original location if no newer safe spawn found
        return location;
    }

    /**
     * Returns the difference in height from the start location to the end location
     * @return
     */
    public int getHeightDifference() {
        return heightDifference;
    }
    
}
