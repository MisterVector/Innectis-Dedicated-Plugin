package net.innectis.innplugin.player.tinywe.blockcounters;

import java.util.List;
import net.innectis.innplugin.location.IdpRegion;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * @author Hret
 *
 * Interface for the blockcounters
 */
public interface BlockCounter {

    /**
     * Will get a blocklist for the given region.
     * The result will differ for each class under the interface
     *
     * <bold>Make sure the region is not to large, its not checked in here.
     * And area to large may freeze the thread that does the counting</bold>
     * @param region
     * @param world
     * @param selector
     * @return List of blocks
     */
    List<Block> getBlockList(IdpRegion region, World world, MaterialSelector selector);

    /**
     * Will get a blocklist for the given region.
     * This will only give the outlining.
     * This is without the top or bottom is they are present
     * The result will differ for each class under the interface
     *
     * <bold>Make sure the region is not to large, its not checked in here.
     * And area to large may freeze the thread that does the counting</bold>
     * @param region
     * @param world
     * @param selector
     * @return List of blocks
     */
    List<Block> getHollowBlockList(IdpRegion region, World world, MaterialSelector selector);

    /**
     * Will get a blocklist for the given region.
     * This will only give the outlining.
     * The result will differ for each class under the interface
     *
     * <bold>Make sure the region is not to large, its not checked in here.
     * And area to large may freeze the thread that does the counting</bold>
     * @param region
     * @param world
     * @param selector
     * @return List of blocks
     */
    List<Block> getWallBlockList(IdpRegion region, World world, MaterialSelector selector);

}