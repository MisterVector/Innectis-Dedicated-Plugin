package net.innectis.innplugin.specialitem.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.data.IdpBlockData;
import net.innectis.innplugin.location.IdpRegion;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounter;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounterFactory;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounterFactory.CountType;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

/**
 * A special item that spreads flowers around
 * the player's location
 *
 * @author Nosliw
 */
public class MagicFlowerSpecialItem extends AbstractSpecialItem {

    @Override
    public void addLoreName(IdpItemStack itemstack) {
        itemstack.getItemdata().addLore(ChatColor.GRAY + "A flower with magical powers.");
    }

    @Override
    public boolean canApplyTo(IdpItemStack itemstack) {
        return (itemstack.getMaterial() == IdpMaterial.POPPY);
    }

    @Override
    public boolean onInteract(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, Action action, Block block) {
        if (action == Action.PHYSICAL) {
            return false;
        }

        // Check for permission and if the user can use the bonus again
        if (!player.hasPermission(Permission.bonus_misc_flower) || !super.canUseSpecialItem(player)) {
            return true;
        }

        player.getSession().useBonus();

        if (player.getSession().getDisplayBonusMessage()) {
            InnPlugin.getPlugin().broadCastMessage(ChatColor.AQUA, "[Bonus] " + player.getColoredDisplayName() + ChatColor.AQUA + " used " + ChatColor.GOLD + "Flower Burst" + ChatColor.AQUA + "!");
        } else {
            player.print(ChatColor.AQUA, "[Bonus] You used " + ChatColor.GOLD + "Flower Burst" + ChatColor.AQUA + "!");
        }

        plugin.getTaskManager().addTask(new MagicalFlowerTask(player.getLocation()));
        return true;

    }

    /**
     * Task that will create the magical flower burst effect.
     *
     * @author Nosliw
     */
    private class MagicalFlowerTask extends LimitedTask {
        private final int MAX_RANGE = 10;
        private List<Block> updatedBlocks;
        private Location location;
        private Random random;
        private int range;

        public MagicalFlowerTask(Location location) {
            super(RunBehaviour.SYNCED, 500, 10);
            updatedBlocks = new ArrayList<Block>();
            this.random = new Random();
            this.location = location;
            this.range = 1;
        }

        @Override
        public void run() {
            if (range >= MAX_RANGE) {
                for (Block block : updatedBlocks) {
                    IdpBlockData blockData = BlockHandler.getIdpBlockData(block.getLocation());

                    if (blockData.isVirtualBlock()) {
                        BlockHandler.setBlock(block, IdpMaterial.AIR);

                        blockData.setVirtualBlockStatus(false);
                    }
                }
                return;
            }

            World world = location.getWorld();
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();

            Vector vec1 = new Vector(x + range, y + range, z + range);
            Vector vec2 = new Vector(x - range, y - range, z - range);

            IdpRegion region = new IdpRegion(vec1, vec2);
            BlockCounter counter = BlockCounterFactory.getCounter(CountType.SPHERE);

            for (Block block : counter.getHollowBlockList(region, world, null)) {
                IdpMaterial mat = IdpMaterial.fromBlock(block);
                IdpMaterial matBelow = IdpMaterial.fromBlock(block.getRelative(BlockFace.DOWN));

                if (mat == IdpMaterial.AIR && matBelow == IdpMaterial.GRASS && random.nextBoolean()) {
                    List<IdpMaterial> possibleMaterials = new ArrayList<IdpMaterial>(
                            Arrays.asList(IdpMaterial.DANDELION, IdpMaterial.POPPY,
                                          IdpMaterial.BLUE_ORCHID, IdpMaterial.ALLIUM,
                                          IdpMaterial.AZURE_BLUET, IdpMaterial.RED_TULIP,
                                          IdpMaterial.ORANGE_TULIP, IdpMaterial.WHITE_TULIP,
                                          IdpMaterial.PINK_TULIP)
                            );

                    IdpMaterial randMaterial = possibleMaterials.get(random.nextInt(possibleMaterials.size()));
                    BlockHandler.setBlock(block, randMaterial);

                    BlockHandler.getIdpBlockData(block.getLocation()).setVirtualBlockStatus(true);
                    updatedBlocks.add(block);
                }
            }
            range++;
        }
    }

}
