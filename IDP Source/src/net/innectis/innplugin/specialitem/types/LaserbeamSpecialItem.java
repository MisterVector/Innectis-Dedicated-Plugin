package net.innectis.innplugin.specialitem.types;

import net.innectis.innplugin.specialitem.SpecialItemConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.data.IdpBlockData;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.LotFlagType;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import net.innectis.innplugin.util.StringUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

/**
 * A special item that will create fire in front of the player.
 *
 * @author Hret
 */
public class LaserbeamSpecialItem extends AbstractSpecialItem {

    final IdpMaterial laserMaterial = IdpMaterial.WOOL_RED;
    final boolean isHighPowered;

    public LaserbeamSpecialItem(boolean isHighPowered) {
        this.isHighPowered = isHighPowered;
    }

    public void addLoreName(IdpItemStack itemstack) {
        itemstack.getItemdata().addLore(ChatColor.GRAY + "Fires a" + (isHighPowered ? " powerful" : "") + " directed energy beam!");
    }

    @Override
    public boolean onInteract(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, Action action, Block clickedBlock) {
        List<Block> laserBlocks = new ArrayList<Block>();
        HashSet<Material> ignoreBlocks = new HashSet<Material>(256);
        HashSet<IdpMaterial> materialSet = new HashSet<IdpMaterial>(256);
        boolean isFirst = true;

        if (isHighPowered) {
            for (IdpMaterial mat : IdpMaterial.values()) {
                if (mat.isBlock()) {
                    switch (mat) {
                        case BEDROCK:
                        case GOLD_BLOCK:
                        case IRON_BLOCK:
                        case OBSIDIAN:
                        case DIAMOND_BLOCK:
                        case IRON_BARS:
                        case EMERALD_BLOCK:
                            continue;
                        default:
                            materialSet.add(mat);
                    }
                }
            }
        } else {
            materialSet.add(IdpMaterial.AIR);
            materialSet.add(IdpMaterial.WATER);
            materialSet.add(IdpMaterial.STATIONARY_WATER);
            materialSet.add(IdpMaterial.LAVA);
            materialSet.add(IdpMaterial.STATIONARY_LAVA);
            materialSet.add(laserMaterial);
        }

        // Convert materials to IDs
        for (IdpMaterial mat : materialSet) {
            ignoreBlocks.add(mat.getBukkitMaterial());
        }

        for (Block block : player.getHandle().getLineOfSight(ignoreBlocks, (isHighPowered ? 40 : 60))) {
            if (!isFirst) {
                InnectisLot lot = LotHandler.getLot(block.getLocation());
                IdpMaterial mat = IdpMaterial.fromBlock(block);

                if (mat == IdpMaterial.AIR || lot == null || lot.isFlagSet(LotFlagType.DESTRUCTION)) {
                    laserBlocks.add(block);
                }
            } else {
                isFirst = false;
            }
        }

        plugin.getTaskManager().addTask(new LaserBeamTask(laserBlocks, isHighPowered));

        // Display message
        String message = StringUtil.format("{0} {1}fired a {2}laser beam!", player.getColoredDisplayName(), ChatColor.DARK_RED, (isHighPowered ? "high powered " : ""));
        plugin.broadCastMessageToLocation(player.getLocation(), message, SpecialItemConstants.SPELL_SHOUT_RADIUS);

        return true;
    }

    /**
     * Task that will move the laser beam along.
     *
     * @author Nosliw
     */
    private class LaserBeamTask extends LimitedTask {

        final int size = 2;
        final int fireTicks = 1000;
        final double damage = 14.0D;
        final int safeDistance = 3;

        private int i = 0;
        private List<Block> blocks;
        private Map<Location, IdpMaterial> changed;
        private boolean isHighPowered;

        public LaserBeamTask(List<Block> blocks, boolean isHighPowered) {
            super(RunBehaviour.SYNCED, 50, blocks.size());
            this.blocks = blocks;
            changed = new HashMap<Location, IdpMaterial>();
            this.isHighPowered = isHighPowered;

        }

        public void run() {
            if (i + size < blocks.size()) {
                // Create next laser step.
                Block nextBlock = blocks.get(i);
                Location loc = nextBlock.getLocation();
                IdpMaterial nextMaterial = IdpMaterial.fromBlock(nextBlock);

                if (nextMaterial != laserMaterial
                        && !isHighPowered) {
                    changed.put(loc, nextMaterial);
                } else {
                    changed.put(loc, IdpMaterial.FIRE);
                }
                BlockHandler.setBlock(nextBlock, laserMaterial);
                BlockHandler.getIdpBlockData(nextBlock.getLocation()).setVirtualBlockStatus(true);

            } else {
                blocks.get(i).getWorld().createExplosion(blocks.get(i).getLocation(), 3, true);
            }


            // Remove previous laser step.
            if (i - size >= 0 && i - size < blocks.size()) {
                Block removeBlock = blocks.get(i - size);
                Location loc = removeBlock.getLocation();
                IdpMaterial removeMaterial = IdpMaterial.fromBlock(removeBlock);
                IdpBlockData blockData = BlockHandler.getIdpBlockData(loc);

                if (blockData.isVirtualBlock()
                        && removeMaterial == laserMaterial) {
                    BlockHandler.setBlock(removeBlock, changed.get(loc));
                    blockData.setVirtualBlockStatus(false);
                }
            }

            // Deal appropriate damage
            if (i > safeDistance && i - size / 2 >= 0 && i - size / 2 < blocks.size()) {
                Location middleLocation = blocks.get(i - size / 2).getLocation();
                for (Entity e : middleLocation.getWorld().getEntities()) {
                    if (e.getLocation().distance(middleLocation) < size) {
                        e.setFireTicks(e.getFireTicks() + i);
                        if (e instanceof LivingEntity) {
                            // TODO: Remove when Armor Stands are no longer living
                            if (e instanceof ArmorStand) {
                                continue;
                            }

                            ((LivingEntity) e).damage(damage);
                        }
                    }
                }
            }

            i++;
        }
    }

}
