package net.innectis.innplugin.specialitem.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.innectis.innplugin.system.command.commands.MiscCommands;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

/**
 * The effect for creating a winter wonder land
 *
 * @author Hret
 */
public class WinterLandSpecialItem extends AbstractSpecialItem {

    public void addLoreName(IdpItemStack itemstack) {
        itemstack.getItemdata().addLore(ChatColor.GRAY + "Unleash that Christmas Spirir!");
    }

    @Override
    public boolean onInteract(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, Action action, Block block) {

        if (action == Action.PHYSICAL) {
            return false;
        }

        // Check for permission and if the user can use the bonus again
        if (!player.hasPermission(Permission.bonus_christmas_candle) || !super.canUseSpecialItem(player)) {
            return true;
        }

        if (block == null) {
            player.printError("You must click on a block in order to use this!");
            return true;
        }

        Location startLocation = block.getLocation();
        startLocation.setY(startLocation.getY() + 1);

        if (BlockHandler.canBuildInArea(player, startLocation, 2, false)) {
            player.getSession().useBonus();

            if (player.getSession().getDisplayBonusMessage()) {
                InnPlugin.getPlugin().broadCastMessage(ChatColor.AQUA, "[Bonus] " + player.getColoredDisplayName() + ChatColor.AQUA + " released their " + ChatColor.GOLD + "Winter Wonder" + ChatColor.AQUA + "!");
            } else {
                player.print(ChatColor.AQUA, "[Bonus] You" + ChatColor.AQUA + " release your " + ChatColor.GOLD + "Winter Wonder" + ChatColor.AQUA + "!");
            }

            plugin.getTaskManager().addTask(new WinterWonderBehaviour(player, startLocation));
            return true;
        } else {
            player.printError("You cannot unleash this power here!");
        }

        return true;
    }
}

class WinterWonderBehaviour extends LimitedTask {

    private final IdpPlayer player;
    private final Location startLocation;

    public WinterWonderBehaviour(IdpPlayer player, Location startLocation) {
        super(RunBehaviour.ASYNC, 1, 1);
        this.player = player;
        this.startLocation = startLocation;
    }

    public void run() {
        World world = startLocation.getWorld();
        int ox = startLocation.getBlockX();
        int oy = startLocation.getBlockY();
        int oz = startLocation.getBlockZ();

        List<BlockAndLocation> blockAndLocations = new ArrayList<BlockAndLocation>();
        List<String> parsedLocationStrings = new ArrayList<String>();
        Random rand = new Random();

        for (int range = 1; range < 15; range++) {
            for (int cx = -range; cx <= range; cx++) {
                for (int cz = -range; cz <= range; cz++) {
                    String coord = cx + ":" + cz;

                    // Don't process the same coordinates more than once
                    if (parsedLocationStrings.contains(coord)) {
                        continue;
                    }

                    parsedLocationStrings.add(coord);

                    Location temploc = new Location(world, ox + cx, oy, oz + cz);
                    Block block = getCleanBlock(temploc.getBlock(), 3);

                    // No suitable location found, move to next block
                    if (block == null) {
                        continue;
                    }

                    IdpMaterial blockMaterial = IdpMaterial.fromBlock(block);

                    // Attempt candy cane generation on land
                    if (blockMaterial == IdpMaterial.AIR && rand.nextInt(200) == 1) {
                        List<Block> candyCane = new ArrayList<Block>();
                        int height = rand.nextInt(5) + 5;
                        Boolean isColour = false;
                        int xm = 0;
                        int zm = 0;

                        switch (rand.nextInt(4)) {
                            case 0:
                                xm = 1;
                                zm = 0;
                                break;
                            case 1:
                                xm = -1;
                                zm = 0;
                                break;
                            case 2:
                                xm = 0;
                                zm = 1;
                                break;
                            case 3:
                                xm = 0;
                                zm = -1;
                                break;
                        }

                        for (int ii = 0; ii < height; ii++) {
                            candyCane.add(block.getRelative(0, ii, 0));
                        }

                        candyCane.add(block.getRelative(1 * xm, height, 1 * zm));
                        candyCane.add(block.getRelative(2 * xm, height, 2 * zm));
                        candyCane.add(block.getRelative(3 * xm, (height - 1), 3 * zm));

                        for (Block candyBlock : candyCane) {
                            IdpMaterial candyMaterial = IdpMaterial.fromBlock(candyBlock);
                            Location loc = candyBlock.getLocation();

                            if ((candyMaterial == IdpMaterial.AIR || candyMaterial == IdpMaterial.SNOW_LAYER)
                                    && BlockHandler.canBuildInArea(player, loc, 2, false)) {
                                blockAndLocations.add(new BlockAndLocation(candyBlock, candyMaterial));

                                BlockHandler.setBlock(candyBlock, IdpMaterial.WOOL_WHITE, (byte) (isColour ? 14 : 0));
                                BlockHandler.getIdpBlockData(loc).setVirtualBlockStatus(true);
                            }

                            isColour = !isColour;
                        }
                    } else {
                        Location loc = block.getLocation();

                        if (BlockHandler.canBuildInArea(player, loc, 2, false)) {
                            IdpMaterial changeMaterial = (blockMaterial == IdpMaterial.AIR ? IdpMaterial.SNOW_LAYER : IdpMaterial.ICE);
                            blockAndLocations.add(new BlockAndLocation(block, blockMaterial));

                            BlockHandler.setBlock(block, changeMaterial);
                            BlockHandler.getIdpBlockData(loc).setVirtualBlockStatus(true);
                        }
                    }
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MiscCommands.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            Thread.sleep(8000);
        } catch (InterruptedException ex) {
            Logger.getLogger(MiscCommands.class.getName()).log(Level.SEVERE, null, ex);
        }

        int q;
        while (!blockAndLocations.isEmpty()) {
            BlockAndLocation blockAndLocation = null;
            q = (blockAndLocations.size() / 3);

            if (q <= 0) {
                blockAndLocation = blockAndLocations.remove(0);
            } else {
                blockAndLocation = blockAndLocations.remove(rand.nextInt(q));
            }

            Block block = blockAndLocation.getOriginalBlock();
            IdpMaterial material = blockAndLocation.getMaterial();
            block.setType(material.getBukkitMaterial());
            BlockHandler.getIdpBlockData(block.getLocation()).clear();

            try {
                Thread.sleep(75);
            } catch (InterruptedException ex) {
                Logger.getLogger(MiscCommands.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Attempts to get either a water block, or a fresh air block as
     * represented by the block passed in, according to a range
     * @param block
     * @param range
     * @return
     */
    private static Block getCleanBlock(Block block, int range) {
        Block currentBlock = block;
        IdpMaterial currentMaterial = IdpMaterial.fromFilteredBlock(currentBlock);
        BlockFace scanFace = (currentMaterial.isSolid() || currentMaterial.isWater() ? BlockFace.UP : BlockFace.DOWN);

        for (int i = 0; i < range; i++) {
            Block nextBlock = currentBlock.getRelative(scanFace);
            IdpMaterial nextBlockMaterial = IdpMaterial.fromFilteredBlock(nextBlock);

            if (scanFace == BlockFace.UP) {
                if (nextBlockMaterial == IdpMaterial.AIR) {
                    if (currentMaterial.isWater()) {
                        return currentBlock;
                    } else if (currentMaterial.isSolid() && currentMaterial != IdpMaterial.SNOW_LAYER) {
                        return nextBlock;
                    } else {
                        return null;
                    }
                }
            } else {
                if (nextBlockMaterial.isSolid() || nextBlockMaterial.isWater()) {
                    if (nextBlockMaterial.isWater()) {
                        return nextBlock;
                    } else if (nextBlockMaterial.isSolid()) {
                        if (currentMaterial == IdpMaterial.AIR) {
                            return currentBlock;
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                }
            }

            currentBlock = nextBlock;
            currentMaterial = IdpMaterial.fromFilteredBlock(currentBlock);
        }

        return null;
    }

    class BlockAndLocation {
        private Block originalBlock = null;
        private IdpMaterial originalMaterial = null;

        public BlockAndLocation(Block originalBlock, IdpMaterial originalMaterial) {
            this.originalBlock = originalBlock;
            this.originalMaterial = originalMaterial;
        }

        public Block getOriginalBlock() {
            return originalBlock;
        }

        public IdpMaterial getMaterial() {
            return originalMaterial;
        }
    }

}