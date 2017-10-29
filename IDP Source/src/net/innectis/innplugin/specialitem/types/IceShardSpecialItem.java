package net.innectis.innplugin.specialitem.types;

import net.innectis.innplugin.specialitem.SpecialItemConstants;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.handlers.PvpHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.LotFlagType;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import net.innectis.innplugin.util.LocationUtil;
import net.innectis.innplugin.util.StringUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

/**
 * A special item that performs an effect similar
 * to the pokemon attack ice shard
 *
 * @author Hret
 */
public class IceShardSpecialItem extends AbstractSpecialItem {

    @Override
    public void addLoreName(IdpItemStack itemstack) {
        itemstack.getItemdata().addLore(ChatColor.GRAY + "Send a deathly chill to your enemies!");
    }

    @Override
    public boolean onInteractEntity(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, LivingEntity entity) {
        Location entityLoc = entity.getLocation();
        InnectisLot entitylot = LotHandler.getLot(entityLoc);

        if (entitylot != null) {
            if (entitylot.isFlagSet(LotFlagType.NODAMAGE)) {
                return true;
            }

            if (entity.getType() != EntityType.PLAYER && entitylot.isFlagSet(LotFlagType.FARM)) {
                return true;
            }
        }

        // if the entity is a player get the IdpPlayer object
        IdpPlayer entityPlayer = null;
        if (entity.getType() == EntityType.PLAYER) {
            entityPlayer = plugin.getPlayer((Player) entity);

            // Check pvp
            if (PvpHandler.playerCanHit(player, player, false, false)) {
                return true;
            }

            entityPlayer.getSession().setFrozen(true);
        }

        // Set entity to center of block
        entity.teleport(LocationUtil.getCenterLocation(entityLoc));
        entity.damage(1.0D);

        // Set ice
        Block block = entityLoc.getBlock();
        IdpMaterial mat = IdpMaterial.fromBlock(block);

        if (mat == IdpMaterial.AIR) {
            BlockHandler.setBlock(block, IdpMaterial.ICE);
        }

        block = block.getRelative(BlockFace.UP);
        mat = IdpMaterial.fromBlock(block);

        if (mat == IdpMaterial.AIR) {
            BlockHandler.setBlock(block, IdpMaterial.ICE);
        }

        // Display message
        String message = StringUtil.format("{0} {1}uses Ice Shard! It's super effective!", player.getColoredDisplayName(), ChatColor.GREEN);
        plugin.broadCastMessageToLocation(player.getLocation(), message, SpecialItemConstants.SPELL_SHOUT_RADIUS);

        // Spawn cleanup
        plugin.getTaskManager().addTask(new IceShardCleanupTask(entityLoc, entityPlayer));
        return true;
    }

    /**
     * @author Hret
     *
     * Simple task that will cleanup the ice block made and unfreezes the player.
     */
    private class IceShardCleanupTask extends LimitedTask {

        private IdpPlayer player;
        private Location location;

        public IceShardCleanupTask(Location location, IdpPlayer player) {
            super(RunBehaviour.SYNCED, 5000, 1);
            this.location = location;
            this.player = player;
        }

        public void run() {
            if (player != null) {
                player.getSession().setFrozen(false);
            }

            Block block = location.getBlock();
            IdpMaterial mat = IdpMaterial.fromBlock(block);

            if (mat == IdpMaterial.ICE) {
                BlockHandler.setBlock(block, IdpMaterial.AIR);
            }

            block = block.getRelative(BlockFace.UP);
            mat = IdpMaterial.fromBlock(block);

            if (mat == IdpMaterial.ICE) {
                BlockHandler.setBlock(block, IdpMaterial.AIR);
            }
        }
    }

}
