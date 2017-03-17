package net.innectis.innplugin.listeners.bukkit;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 *
 * @author Lynxy
 * */
public class ReadOnlyPlayerListener implements Listener {

    private static InnPlugin plugin;

    public ReadOnlyPlayerListener(InnPlugin instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        IdpPlayer player = plugin.getPlayer(event.getPlayer());
        int maxMapSize = player.getWorld().getSettings().getWorldSize();

        if (Math.abs(event.getTo().getBlockX()) > maxMapSize || Math.abs(event.getTo().getBlockZ()) > maxMapSize) {
            player.printError("You have reached the end of the map!");
            player.getHandle().teleport(event.getFrom());
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        IdpPlayer player = plugin.getPlayer(event.getPlayer());
        Block block = event.getBlockClicked();

        event.setCancelled(true);
        player.printError("Server is read-only!");
        player.sendBlockChange(block);
        return;
    }

    /**
     * When a bucket is used (to empty it)
     * @param event
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        IdpPlayer player = plugin.getPlayer(event.getPlayer());
        Block block = event.getBlockClicked();
        event.setCancelled(true);
        player.printError("Server is read-only!");
        player.sendBlockChange(block);
        return;
    }

    /**
     * When the player interacts with an item
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        IdpPlayer player = plugin.getPlayer(event.getPlayer());
        Block block = event.getClickedBlock();
        if (block != null) {
            //Block interaction with ALL blocks
            //never know what might happen. a lever might turn a piston on that moves blocks outside a lot
            event.setCancelled(true);
            player.printError("Server is read-only!");
            return;
        }
    }
    
}
