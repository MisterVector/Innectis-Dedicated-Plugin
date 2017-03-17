package net.innectis.innplugin.system.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.innectis.innplugin.IdpRuntimeException;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.tasks.RepeatingTask;
import net.innectis.innplugin.tasks.RunBehaviour;

/**
 *
 * @author Hret
 *
 * A timer that can tick in intervals for a game.
 */
public class GameTimerTask extends RepeatingTask {

    private List<GameTimer> timers;

    public GameTimerTask(long interval, GameTimer... gamelistener) {
        super(RunBehaviour.ASYNC, interval);
        this.timers = new ArrayList<GameTimer>(Arrays.asList(gamelistener));
    }

    public void addTimer(GameTimer timer) {
        if (!timers.isEmpty()) {
            if (timers.get(0).getId() != timer.getId()) {
                throw new IdpRuntimeException("Game mismatch!");
            }
        }
        this.timers.add(timer);
    }

    public void run() {
        if (!timers.isEmpty()) {

            if (IdpGameManager.getInstance().getGame(timers.get(0).getId()) != null) {

                for (Iterator<GameTimer> it = timers.iterator(); it.hasNext();) {
                    GameTimer gameTimer = it.next();
                    gameTimer.onInterval();
                }

                return;
            }
        }

        InnPlugin.getPlugin().getTaskManager().removeTask(this);
    }

    public String getName() {
        if (timers.isEmpty()) {
            return "Gametimer for ended game";
        } else {
            return "Gametimer for game #" + timers.get(0).getId();
        }
    }

}
