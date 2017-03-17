package net.innectis.innplugin.tasks.async;

import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.tasks.DefaultTaskDelays;
import net.innectis.innplugin.tasks.RepeatingTask;
import net.innectis.innplugin.tasks.RunBehaviour;

/**
 *
 * This method cleans up expired sessions of players.
 */
public class SessionCleanup extends RepeatingTask {

    public SessionCleanup() {
        super(RunBehaviour.ASYNC, DefaultTaskDelays.SessionCleanup);
    }

    @Override
    public void run() {
        PlayerSession.cleanup(getLastExecution());
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }
    
}
