package net.innectis.innplugin.tasks.async;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.tasks.DefaultTaskDelays;
import net.innectis.innplugin.tasks.RepeatingTask;
import net.innectis.innplugin.tasks.RunBehaviour;

/**
 *
 * This cleans up the pvp kills.
 */
public class PvpCleanup extends RepeatingTask {

    public PvpCleanup(InnPlugin plugin) {
        super(RunBehaviour.ASYNC, DefaultTaskDelays.PVPCleanup);
    }

    @Override
    public void run() {
        for (PlayerSession session : PlayerSession.getSessions()) {
            session.cleanupPvpKills();
        }
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }
    
}
