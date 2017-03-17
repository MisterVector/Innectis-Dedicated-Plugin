package net.innectis.innplugin.listeners.bukkit;

import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.listeners.InnBukkitListener;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.listeners.idp.InnHangingBreakEvent;
import net.innectis.innplugin.loggers.BlockLogger;
import net.innectis.innplugin.loggers.LogType;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.LotFlagType;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;

/**
 *
 * @author AlphaBlend
 */
public class BukkitHangingListener implements InnBukkitListener {

    private final InnPlugin plugin;

    public BukkitHangingListener(InnPlugin instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onHangingBreak(HangingBreakEvent event) {
        Location loc = event.getEntity().getLocation();
        InnectisLot lot = LotHandler.getLot(loc, true);

        if (event instanceof HangingBreakByEntityEvent) {
            HangingBreakByEntityEvent event2 = (HangingBreakByEntityEvent) event;

            if (event2.getRemover() instanceof Player) {
                IdpPlayer player = plugin.getPlayer((Player) event2.getRemover());

                // Not if player is not logged in
                if (!player.getSession().isLoggedIn()) {
                    event.setCancelled(true);
                    return;
                }

                // Secondairy listener
                if (plugin.getListenerManager().hasListeners(InnEventType.HANGING_BREAK)) {
                    InnHangingBreakEvent idpevent = new InnHangingBreakEvent(player, event.getEntity());
                    plugin.getListenerManager().fireEvent(idpevent);
                    if (idpevent.isCancelled()) {
                        event.setCancelled(true);
                    }
                    if (idpevent.shouldTerminate()) {
                        return;
                    }
                }

                if (!BlockHandler.canBuildInArea(player, loc, 2, false)) {
                    player.printError("You cannot destroy this!");
                    event.setCancelled(true);
                    return;
                }

                BlockLogger blockLogger = (BlockLogger) LogType.getLoggerFromType(LogType.BLOCK);
                blockLogger.logEntityAction(player.getUniqueId(), event.getEntity(), BlockLogger.BlockAction.ENTITY_BREAK);
                return;
            }
        }

        if (lot != null && !lot.isFlagSet(LotFlagType.DESTRUCTION)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onHangingPlace(HangingPlaceEvent event) {
        Location loc = event.getEntity().getLocation();
        IdpPlayer player = plugin.getPlayer(event.getPlayer());

        // Not if player is not logged in
        if (!player.getSession().isLoggedIn()) {
            event.setCancelled(true);
            return;
        }

        if (!BlockHandler.canBuildInArea(player, loc, BlockHandler.ACTION_BLOCK_PLACED, false)) {
            player.printError("You cannot place this here!");
            event.setCancelled(true);
            return;
        }

        BlockLogger blockLogger = (BlockLogger) LogType.getLoggerFromType(LogType.BLOCK);
        blockLogger.logEntityAction(player.getUniqueId(), event.getEntity(), BlockLogger.BlockAction.ENTITY_PLACE);
    }

}
