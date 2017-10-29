package net.innectis.innplugin.tasks.sync;

import java.util.Iterator;
import java.util.Map.Entry;
import net.innectis.innplugin.handlers.PvpHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.tasks.DefaultTaskDelays;
import net.innectis.innplugin.tasks.RepeatingTask;
import net.innectis.innplugin.tasks.RunBehaviour;

/**
 *
 * This task cleans up the hashmaps in the innplugin
 */
public class MapCleanup extends RepeatingTask {

    private InnPlugin plugin;

    public MapCleanup(InnPlugin plugin) {
        super(RunBehaviour.SYNCED, DefaultTaskDelays.MapCleanup);
        this.plugin = plugin;
    }

    @Override
    public void run() {
        synchronized (plugin.getAdminMsgSpam()) {
            for (Iterator<Entry<String, Long>> it = plugin.getAdminMsgSpam().entrySet().iterator(); it.hasNext();) {
                Entry<String, Long> e = it.next();
                if (getLastExecution() - e.getValue() > 2000) {
                    it.remove();
                }
            }
        }
        synchronized (PvpHandler.getLotPvpToggle()) {
            for (Iterator<Entry<Integer, Long>> it = PvpHandler.getLotPvpToggle().entrySet().iterator(); it.hasNext();) {
                Entry<Integer, Long> e = it.next();
                if (getLastExecution() - e.getValue() > 30000) {
                    it.remove();
                }
            }
        }
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }
    
}
