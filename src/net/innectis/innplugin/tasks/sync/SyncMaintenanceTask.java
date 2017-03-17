package net.innectis.innplugin.tasks.sync;

import java.util.List;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.tasks.DefaultTaskDelays;
import net.innectis.innplugin.tasks.RepeatingTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import org.bukkit.World;

/**
 *
 * Here maintenance tasks are done that need to be done synced!
 *
 * Basic maintenancetasks done here.
 * Like saving players/worlds and the garbage collector.
 *
 * The extra messages will only be send if the plugin is in logDebug mode.
 * Otherwise pretty useless and it will only spam the staff.
 */
public class SyncMaintenanceTask extends RepeatingTask {

    private InnPlugin plugin;

    public SyncMaintenanceTask(InnPlugin plugin) {
        super(RunBehaviour.SYNCED, DefaultTaskDelays.SyncMaintenance);
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (InnPlugin.isDebugEnabled()) {
            InnPlugin.logDebug("Starting Sync Maintenance...");
        }

        if (!InnPlugin.isShuttingDown()) {

        }

        if (InnPlugin.isDebugEnabled()) {
            InnPlugin.logDebug("Finished Sync Maintenance...");
        }
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

}
