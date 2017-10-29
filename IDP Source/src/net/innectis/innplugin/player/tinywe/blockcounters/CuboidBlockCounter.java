package net.innectis.innplugin.player.tinywe.blockcounters;

import java.util.ArrayList;
import java.util.List;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.IdpRegion;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 *
 * @author Hret
 *
 * Cuboid block counter
 */
public final class CuboidBlockCounter implements BlockCounter {

    /**
     * Only access this from the BlockCounterFactory
     */
    protected CuboidBlockCounter() {
    }

    /**
     * @inherit
     * */
    @Override
    public List<Block> getBlockList(IdpRegion region, World world, MaterialSelector selector) {
        List<Block> blocks = new ArrayList<Block>();
        for (int x = region.getLowestX(); x <= region.getHighestX(); x++) {
            for (int z = region.getLowestZ(); z <= region.getHighestZ(); z++) {
                for (int y = region.getLowestY(); y <= region.getHighestY(); y++) {
                    Block tmpBlk = world.getBlockAt(x, y, z);
                    IdpMaterial mat = IdpMaterial.fromBlock(tmpBlk);

                    if (selector == null || selector.materialSelected(mat)) {
                        blocks.add(tmpBlk);
                    }
                }
            }
        }
        return blocks;
    }

    /**
     * @inherit
     * */
    @Override
    public List<Block> getHollowBlockList(IdpRegion region, World world, MaterialSelector selector) {
        List<Block> blocks = new ArrayList<Block>();
        for (int x = region.getLowestX(); x <= region.getHighestX(); x++) {
            for (int z = region.getLowestZ(); z <= region.getHighestZ(); z++) {
                for (int y = region.getLowestY(); y <= region.getHighestY(); y++) {
                    if (x == region.getHighestX() || x == region.getLowestX()
                            || y == region.getHighestY() || y == region.getLowestY()
                            || z == region.getLowestZ() || z == region.getHighestZ()) {
                        Block tmpBlk = world.getBlockAt(x, y, z);
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
     * */
    @Override
    public List<Block> getWallBlockList(IdpRegion region, World world, MaterialSelector selector) {
        List<Block> blocks = new ArrayList<Block>();
        for (int x = region.getLowestX(); x <= region.getHighestX(); x++) {
            for (int z = region.getLowestZ(); z <= region.getHighestZ(); z++) {
                for (int y = region.getLowestY(); y <= region.getHighestY(); y++) {
                    if (x == region.getHighestX() || x == region.getLowestX()
                            || z == region.getLowestZ() || z == region.getHighestZ()) {
                        Block tmpBlk = world.getBlockAt(x, y, z);
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