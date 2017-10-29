package net.innectis.innplugin.player.tinywe.blockcounters;

import java.util.ArrayList;
import java.util.List;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.IdpRegion;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 *
 * @author Hret
 *
 * Cuboid pyramid counter
 */
public final class PyramidBlockCounter implements BlockCounter {

    /**
     * Only access this from the BlockCounterFactory
     */
    protected PyramidBlockCounter() {
    }

    private Vector startPos;
    private int radius;

    private void setRegion(IdpRegion region) {
        startPos = new Vector(region.getCenterX() - 0.5, region.getLowestY(), region.getCenterZ() - 0.5);

        // Get radius
        radius = (region.getXLength() / 2);
        radius = Math.min(region.getYLength() - 1, radius);
        radius = Math.min((region.getZLength() / 2), radius);
    }

    /**
     * @inherit
     **/
    @Override
    public List<Block> getBlockList(IdpRegion region, World world, MaterialSelector selector) {
        setRegion(region);

        List<Block> blocks = new ArrayList<Block>();
        int size = radius;

        for (int y = 0; y <= radius; ++y) {
            size--;
            for (int x = -size ; x <= size; ++x) {
                for (int z = -size; z <= size; ++z) {
                    if (z <= size && x <= size) {
                        int sx = (startPos.getBlockX() + x), sy = (startPos.getBlockY() + y), sz = (startPos.getBlockZ() + z);
                        Block tmpBlk = world.getBlockAt(sx, sy, sz);
                        IdpMaterial mat = IdpMaterial.fromBlock(tmpBlk);

                        if (selector == null || selector.materialSelected(mat)) {
                            blocks.add(tmpBlk);
                        }
                    }
                }
            }
        }
        return blocks;
    }

    /**
     * @inherit
     **/
    @Override
    public List<Block> getHollowBlockList(IdpRegion region, World world, MaterialSelector selector) {
        setRegion(region);

        List<Block> blocks = new ArrayList<Block>();
        int size = radius;

        for (int y = 0; y <= radius; ++y) {
            size--;
            for (int x = -size; x <= size; ++x) {
                for (int z = -size; z <= size; ++z) {
                    if (z == size || x == size || z == (-size) || x == (-size) || y == 0) {
                        int sx = (startPos.getBlockX() + x), sy = (startPos.getBlockY() + y), sz = (startPos.getBlockZ() + z);
                        Block tmpBlk = world.getBlockAt(sx, sy, sz);
                        IdpMaterial mat = IdpMaterial.fromBlock(tmpBlk);

                        if (selector == null || selector.materialSelected(mat)) {
                            blocks.add(tmpBlk);
                        }
                    }
                }
            }
        }

        return blocks;
    }

    /**
     * @inherit
     **/
    @Override
    public List<Block> getWallBlockList(IdpRegion region, World world, MaterialSelector selector) {
        setRegion(region);

        List<Block> blocks = new ArrayList<Block>();
        int size = radius;

        for (int y = 0; y <= radius; ++y) {
            size--;
            for (int x = -size; x <= size; ++x) {
                for (int z = -size; z <= size; ++z) {
                    if (z == size || x == size || z == -size || x == -size) {
                        int sx = (startPos.getBlockX() + x), sy = (startPos.getBlockY() + y), sz = (startPos.getBlockZ() + z);
                        Block tmpBlk = world.getBlockAt(sx, sy, sz);
                        IdpMaterial mat = IdpMaterial.fromBlock(tmpBlk);

                        if (selector == null || selector.materialSelected(mat)) {
                            blocks.add(tmpBlk);
                        }
                    }
                }
            }
        }

        return blocks;
    }

}