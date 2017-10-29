package net.innectis.innplugin.listeners.bukkit;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.LeavesDecayEvent;

/**
 *
 * @author Lynxy
 */
public class ReadOnlyBlockListener implements Listener {

    public ReadOnlyBlockListener() {
    }

    /** Water flow */
    @EventHandler(priority = EventPriority.LOW)
    public void onBlockFromTo(BlockFromToEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockIgnite(BlockIgniteEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockFade(BlockFadeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockForm(BlockFormEvent event) {
        event.setCancelled(true);
    }

    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    public void onEntityBlockForm(EntityBlockFormEvent event) {
        event.setCancelled(true);
    }

    public void onLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent event) {
        IdpPlayer player = new IdpPlayer(InnPlugin.getPlugin(), event.getPlayer());
        Block block = event.getBlock();

        event.setBuild(false);
        event.setCancelled(true);

        player.printError("Server is read-only");
        player.sendBlockChange(block);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
        IdpPlayer player = new IdpPlayer(InnPlugin.getPlugin(), event.getPlayer());
        Block block = event.getBlock();

        event.setCancelled(true);

        player.printError("Server is read-only");
        player.sendBlockChange(block);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        event.setCancelled(true);
    }
    
}
