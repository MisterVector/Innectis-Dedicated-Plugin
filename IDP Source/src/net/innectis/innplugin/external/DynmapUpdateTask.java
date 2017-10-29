package net.innectis.innplugin.external;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.tasks.DefaultTaskDelays;
import net.innectis.innplugin.tasks.RepeatingTask;
import net.innectis.innplugin.tasks.RunBehaviour;

/**
 *
 * Updates dynmap
 */
class DynmapUpdateTask extends RepeatingTask {

    private InnPlugin parent;
    private DynmapIDP dynmapAPI;

    public DynmapUpdateTask(InnPlugin parent, DynmapIDP dynmapAPI) {
        super(RunBehaviour.ASYNC, DefaultTaskDelays.DynmapUpdate);
        this.parent = parent;
        this.dynmapAPI = dynmapAPI;
    }

    @Override
    public void run() {
        // Make sure the dynmap plugin is loaded. This may not be true when
        // the server is shutting down and dynmap unloads before IDP
        if (parent.getServer().getPluginManager().isPluginEnabled("dynmap")) {
            dynmapAPI.update();
        }
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }
    
}
