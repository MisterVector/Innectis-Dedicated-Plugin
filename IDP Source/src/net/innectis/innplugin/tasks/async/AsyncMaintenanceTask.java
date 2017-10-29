package net.innectis.innplugin.tasks.async;

import java.util.Collection;
import java.util.Iterator;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.location.data.ChunkDatamanager;
import net.innectis.innplugin.loggers.BlockLogger;
import net.innectis.innplugin.loggers.LogType;
import net.innectis.innplugin.objects.owned.handlers.ChestHandler;
import net.innectis.innplugin.objects.owned.handlers.DoorHandler;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.tasks.DefaultTaskDelays;
import net.innectis.innplugin.tasks.RepeatingTask;
import net.innectis.innplugin.tasks.RunBehaviour;

/**
 *
 * Here maintenance tasks are done that can be done Async
 *
 * Basic maintenancetasks done here.
 * Like saving players/worlds and the garbage collector.
 *
 * The extra messages will only be send if the plugin is in logDebug mode.
 * Otherwise pretty useless and it will only spam the staff.
 */
public class AsyncMaintenanceTask extends RepeatingTask {

    private InnPlugin plugin;

    public AsyncMaintenanceTask(InnPlugin plugin) {
        super(RunBehaviour.ASYNC, DefaultTaskDelays.AsyncMaintenance);
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (InnPlugin.isDebugEnabled()) {
            InnPlugin.logDebug("Starting Async Maintenance...");
        }

        LotHandler.saveLots();

        if (InnPlugin.isDebugEnabled()) {
            InnPlugin.logDebug("Lots saved...");
        }

        ChestHandler.saveChests();

        if (InnPlugin.isDebugEnabled()) {
            InnPlugin.logDebug("Chests saved...");
        }

        DoorHandler.saveDoors();

        if (InnPlugin.isDebugEnabled()) {
            InnPlugin.logDebug("Doors saved...");
        }

        BlockLogger blockLogger = (BlockLogger) LogType.getLoggerFromType(LogType.BLOCK);
        blockLogger.saveCache();

        if (InnPlugin.isDebugEnabled()) {
            InnPlugin.logDebug("Blocklog saved...");
        }

        ChunkDatamanager.reclaimUnusedChunks();

        if (InnPlugin.isDebugEnabled()) {
            InnPlugin.logDebug("ChunkData reclaimed...");
        }

        if (InnPlugin.isDebugEnabled()) {
            InnPlugin.logDebug("Finished Async Maintenance...");
        }
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

}
