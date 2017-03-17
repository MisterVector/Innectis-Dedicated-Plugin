package net.innectis.innplugin.player.tinywe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.items.ItemData;
import net.innectis.innplugin.location.data.IdpBlockData;
import net.innectis.innplugin.location.IdpRegion;
import net.innectis.innplugin.location.IdpWorld;
import net.innectis.innplugin.location.IdpWorldFactory;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounter;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounterFactory;
import net.innectis.innplugin.player.tinywe.blockcounters.MaterialSelector;
import net.innectis.innplugin.util.LocationUtil;
import net.innectis.innplugin.util.MagicValueUtil;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

/**
 *
 * @author Hret
 * <p/>
 *
 * Class to handle blocks that are copied in a region.
 *
 * // @todo: this needs to be improved in regards to rotating some blocks
 *
 */
public class RegionClipboard {

    private String owner;
    private List<ClipboardBlock> blocks;
    private IdpRegion region;
    private Location oldMarkLocation;
    private IdpWorld world = null;

    public RegionClipboard(String owner, List<ClipboardBlock> blocks, IdpRegion region, Location marklocation) {
        this.owner = owner;
        this.blocks = blocks;
        this.region = region;
        this.oldMarkLocation = LocationUtil.getCenterLocation(marklocation);
    }

    /**
     * @return the clipboard blocks
     */
    public List<ClipboardBlock> getClipboardBlocks() {
        return blocks;
    }

    /**
     * @param blocks sets the clipboard blocks
     */
    public void setClipboardBlocks(List<ClipboardBlock> blocks) {
        this.blocks = blocks;
    }

    /**
     * The amount of blocks that are stored in this clipboard.
     *
     * @return
     */
    public int getBlockAmount() {
        return blocks.size();
    }

    /**
     * @return the region
     */
    public IdpRegion getRegion() {
        return region;
    }

    /**
     * @param region the region to set
     */
    public void setRegion(IdpRegion region) {
        this.region = region;
    }

    /**
     * @return the location
     */
    public Location getLocation() {
        return oldMarkLocation;
    }

    /**
     * Gets the world in which this clipboard was created
     * @return
     */
    public IdpWorld getWorld() {
        if (world == null) {
            world = IdpWorldFactory.getWorld(oldMarkLocation.getWorld().getName());
        }

        return world;
    }

    /**
     * This will set the clipboard based on the newPosition variable.
     * <p/>
     * Only blocks that are marked (or excluded) from the materialselector will
     * be changed.
     * <p/>
     * Note: This does not check access restrictions.
     *
     * @param newPosition
     * @param materialselector
     * @return the RegionClipboardResult of the operation
     */
    public RegionClipboardResult setBlocks(Location newMarkLocation, MaterialSelector materialselector) {
        IdpPlayer player = InnPlugin.getPlugin().getPlayer(owner);
        IdpEditSession session = player.getSession().getEditSession();

        Collections.sort(blocks, new BlockPrioritizer());

        boolean worldChanged = !newMarkLocation.getWorld().equals(getLocation().getWorld());
        int counter = 0;
        int ignoreCounter = 0;

        for (ClipboardBlock cb : getClipboardBlocks()) {
            BlockState oldState = cb.getState();
            MaterialData md = oldState.getData();

            int data = MagicValueUtil.getDataFromMaterialData(md);
            IdpMaterial mat = IdpMaterial.fromMaterialData(md);

            if (!materialselector.materialSelected(mat)) {
                continue;
            }

            Location newPosition = null;

            Location centerLocation = LocationUtil.getCenterLocation(newMarkLocation);
            Vector change = LocationUtil.subtractLocationFromLocationToVector(oldMarkLocation, centerLocation);

            if (worldChanged) {
                Location newLoc = LocationUtil.subtractLocationFromVectorToLocation(cb.getLocation(), change);
                newPosition = new Location(newMarkLocation.getWorld(), newLoc.getBlockX(), newLoc.getBlockY(), newLoc.getBlockZ(), newLoc.getYaw(), newLoc.getPitch());
            } else {
                newPosition = LocationUtil.subtractLocationFromVectorToLocation(cb.getLocation(), change);
            }

            if (!session.canOverwriteBlock(newPosition.getBlock())
                    || !session.canPlaceMaterial(mat)) {
                continue;
            }

            if (session.useInventory()) {
                Block block = newPosition.getBlock();

                if (session.canProcessBlock(block)) {
                    if (session.removeItemFromPlayer(oldState)) {
                        IdpItemStack returnStack = session.getReturnStack(block.getState());

                        if (returnStack != null) {
                            int remain = session.addItemToPlayer(returnStack);

                            // If unable to add the block to inventory fully, give back the item
                            // we took from the player
                            if (remain > 0) {
                                IdpItemStack originalStack = new IdpItemStack(mat, 1);

                                if (oldState instanceof Skull) {
                                    Skull skull = (Skull) oldState;

                                    if (mat == IdpMaterial.PLAYER_SKULL) {
                                        ItemData itemdata = originalStack.getItemdata();
                                        OfflinePlayer ownerPlayer = skull.getOwningPlayer();
                                        String owner = (ownerPlayer != null ? ownerPlayer.getName() : "");

                                        itemdata.setMobheadName(owner);
                                        originalStack.setItemdata(itemdata);
                                    }
                                } else if (oldState instanceof Banner) {
                                    Banner oldBanner = (Banner) oldState;
                                    ItemData itemdata = returnStack.getItemdata();
                                    itemdata.setBannerBaseColor(oldBanner.getBaseColor());
                                    itemdata.setBannerPatterns(oldBanner.getPatterns());
                                }

                                player.addItemToInventory(originalStack);
                                ignoreCounter++;
                                continue;
                            }
                        }
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            }

            Block newBlock = newPosition.getBlock();
            BlockHandler.setBlock(newBlock, mat, (byte) data);
            BlockState newState = newBlock.getState();

            // Make sure to preserve sign text
            if (newState instanceof Sign) {
                Sign oldSign = (Sign) oldState;
                Sign newSign = (Sign) newState;

                for (int i = 0; i < 4; i++) {
                    newSign.setLine(i, oldSign.getLine(i));
                }

                newSign.update();
            } else if (newState instanceof Skull) {
                Skull oldSkull = (Skull) oldState;
                Skull newSkull = (Skull) newState;

                newSkull.setSkullType(oldSkull.getSkullType());
                newSkull.setRotation(oldSkull.getRotation());

                if (oldSkull.getSkullType() == SkullType.PLAYER) {
                    OfflinePlayer owner = oldSkull.getOwningPlayer();
                    newSkull.setOwningPlayer(owner);
                }

                newSkull.update();
            } else if (newState instanceof Banner) {
                Banner oldBanner = (Banner) oldState;
                Banner newBanner = (Banner) newState;

                newBanner.setBaseColor(oldBanner.getBaseColor());
                newBanner.setPatterns(oldBanner.getPatterns());
                newBanner.update();
            }

            IdpBlockData oldData = BlockHandler.getIdpBlockData(oldState.getBlock().getLocation());
            IdpBlockData newData = BlockHandler.getIdpBlockData(newPosition);

            if (newData.hasData()) {
                newData.clear();
            }

            Map<String, String> values = oldData.getValues();

            if (values != null) {
                for (Entry<String, String> entry : oldData.getValues().entrySet()) {
                    newData.setValue(entry.getKey(), entry.getValue());
                }

                newData.save();
            }

            counter++;
        }

        return new RegionClipboardResult(counter, ignoreCounter);
    }

    /**
     * Rotates the clipboard for the given amount of degrees
     *
     * @param degrees The amount of degrees (this must be devidable by 90).
     */
    public boolean rotate(int degrees) {
        // No more than 360 degrees
        if (degrees > 360) {
            degrees = 360;
        }

         if (degrees % 90 != 0) {
             return false;
         }

        int times = (degrees / 90);

        int centerX = getLocation().getBlockX();
        int centerZ = getLocation().getBlockZ();
        int newX, newZ, changex, changez;

        double radians = Math.toRadians(degrees);
        double sinradians = Math.sin(radians);
        double cosradians = Math.cos(radians);

        for (ClipboardBlock cb : getClipboardBlocks()) {
            Location oldLocation = cb.getLocation();
            BlockState state = cb.getState();

            changex = oldLocation.getBlockX() - centerX;
            changez = oldLocation.getBlockZ() - centerZ;

            newX = (int) (centerX + (cosradians * changex - sinradians * changez));
            newZ = (int) (centerZ + (sinradians * changex + cosradians * changez));

            oldLocation.setX(newX);
            oldLocation.setZ(newZ);

            BlockFace face = BlockHandler.getBlockFace(state);
            boolean extended = false;

            // Don't rotate up or down faces
            if (face == BlockFace.UP || face == BlockFace.DOWN) {
                continue;
            }

            // Block face is not north, south, east or west, so this is using
            // an extended blockface
            if (face.ordinal() > 3) {
                extended = true;
            }

            BlockFace rotatedFace = BlockHandler.getRotatedBlockFace(face, times, extended);
            BlockHandler.rotateBlock(state, rotatedFace);
        }

        return true;
    }

    /**
     * Class that prioritizes strong blocks. Note: weak blocks will be set to
     * the end.
     */
    private static class BlockPrioritizer implements Comparator<ClipboardBlock> {
        public BlockPrioritizer() {
        }

        public int compare(ClipboardBlock cb1, ClipboardBlock cb2) {
            BlockState obj1 = cb1.getState();
            BlockState obj2 = cb2.getState();

            IdpMaterial mat1 = IdpMaterial.fromMaterialData(obj1.getData());
            IdpMaterial mat2 = IdpMaterial.fromMaterialData(obj2.getData());

            boolean is1Weak = mat1.isWeakBlock();
            boolean is2Weak = mat2.isWeakBlock();

            if (is1Weak && !is2Weak) {
                return 1;
            }
            if (!is1Weak && is2Weak) {
                return -1;
            }

            return 0;
        }
    }

    /**
     * This will make a regionclipboard object of the given region.
     * <p/>
     * It will take a snapshot of all blocks inside this region
     *
     * @param region
     * @param marklocation
     * @return the clipboard or null if there are no blocks in the region.
     */
    public static RegionClipboard getClipboard(String owner, IdpRegion region, Location marklocation) {
        BlockCounter counter = BlockCounterFactory.getCounter(BlockCounterFactory.CountType.CUBOID);
        List<Block> blocks = counter.getBlockList(region, marklocation.getWorld(), null);

        // If empty, nothing to be done.
        if (blocks.isEmpty()) {
            return null;
        }

        // Make a list to hold the blockstates
        List<ClipboardBlock> clipboardBlocks = new ArrayList<ClipboardBlock>(blocks.size());

        // Get the states;
        for (Block block : blocks) {
            clipboardBlocks.add(new ClipboardBlock(block.getLocation(), block.getState()));
        }

        return new RegionClipboard(owner, clipboardBlocks, region, marklocation);
    }

    /**
     * A class to hold statistics of a region operation
     */
    public class RegionClipboardResult {
        private int changed;
        private int ignored;

        public RegionClipboardResult(int changed, int ignored) {
            this.changed = changed;
            this.ignored = ignored;
        }

        /**
         * Gets the number of blocks changed
         * @return
         */
        public int getChanged() {
            return changed;
        }


        public int getIgnored() {
            return ignored;
        }
    }
}

class ClipboardBlock {
    private Location loc;
    private BlockState state;

    public ClipboardBlock(Location loc, BlockState state) {
        this.state = state;
        this.loc = loc;
    }

    public Location getLocation() {
        return loc;
    }

    public void setLocation(Location loc) {
        this.loc = loc;
    }

    public BlockState getState() {
        return state;
    }

}
