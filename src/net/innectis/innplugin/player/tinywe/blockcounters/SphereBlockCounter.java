package net.innectis.innplugin.player.tinywe.blockcounters;

import java.util.ArrayList;
import java.util.Collections;
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
 * Sphere block counter
 */
public final class SphereBlockCounter implements BlockCounter {

    /**
     * Only access this from the BlockCounterFactory
     */
    protected SphereBlockCounter() {
    }
    private Vector center;
    private int radius;

    private void setRegion(IdpRegion region) {
        center = new Vector(region.getCenterX(), region.getCenterY(), region.getCenterZ());

        // Get radius
        radius = region.getXLength();
        radius = Math.min(region.getYLength(), radius);
        radius = Math.min(region.getZLength(), radius);
        radius /= 2;
    }

    /**
     * @inherit
     **/
    @Override
    public List<Block> getBlockList(IdpRegion region, World world, MaterialSelector selector) {
        setRegion(region);

        List<Block> blocks = new ArrayList<Block>();

        for (int x = region.getLowestX(); x <= region.getHighestX(); x++) {
            for (int z = region.getLowestZ(); z <= region.getHighestZ(); z++) {
                for (int y = region.getLowestY(); y <= region.getHighestY(); y++) {
                    Block tmpBlk = world.getBlockAt(x, y, z);
                    IdpMaterial mat = IdpMaterial.fromBlock(tmpBlk);

                    if (tmpBlk.getLocation().toVector().distance(center) <= radius + 0.5) {
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

        for (int x = region.getLowestX(); x <= region.getHighestX(); x++) {
            for (int z = region.getLowestZ(); z <= region.getHighestZ(); z++) {
                for (int y = region.getLowestY(); y <= region.getHighestY(); y++) {
                    Block tmpBlk = world.getBlockAt(x, y, z);
                    IdpMaterial mat = IdpMaterial.fromBlock(tmpBlk);

                    if (selector == null || selector.materialSelected(mat)) {
                        double d = tmpBlk.getLocation().toVector().distance(center);

                        if (d <= radius + 0.5 && d >= radius - 0.5) {
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
        return Collections.emptyList();
    }

}