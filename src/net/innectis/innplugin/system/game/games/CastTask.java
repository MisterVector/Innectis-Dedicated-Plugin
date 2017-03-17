package net.innectis.innplugin.system.game.games;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import org.bukkit.entity.Player;

public class CastTask extends LimitedTask {

    private float progress;
    private boolean running;
    private String playerName;
    private Runnable intervalAction, action, cancelAction;

    /**
     * This task uses a players experience bar to create the illusion of casting an effect.
     * This should be used in conjunction with the CastAbility wrapper.
     * This is a SYNCED task, so do not pause or run long code!
     * @param player The player running the task.
     * @param intervalAction The action that occurs every 1/10th of the process. (Can be null)
     * @param action The action that occurs when the task is complete (Cannot be null)
     * @param cancelAction The action that occurs when the task is cancelled (Can be null)
     * @param speed The total time (in millis) that it takes to execute the task.
     */
    public CastTask(IdpPlayer player, Runnable intervalAction, Runnable action, Runnable cancelAction, int speed) {
        super(RunBehaviour.SYNCED, speed, 11);

        this.progress = 0f;
        this.running = true;

        this.playerName = player.getName();
        this.intervalAction = intervalAction;
        this.action = action;
        this.cancelAction = cancelAction;

        Player bukkitPlayer = player.getHandle();
        bukkitPlayer.setLevel(0);
        bukkitPlayer.setExp(0);
    }

    public void run() {
        if (!running) {
            return;
        }

        IdpPlayer player = InnPlugin.getPlugin().getPlayer(playerName, true);
        if (player == null) {
            running = false;
            if (cancelAction != null) {
                cancelAction.run();
            }
        } else if (progress < 10) {
            progress++;
            Player bukkitPlayer = player.getHandle();
            bukkitPlayer.setLevel(0);
            bukkitPlayer.setExp(progress / 10f);
            if (intervalAction != null) {
                intervalAction.run();
            }
        } else {
            action.run();
        }
    }

}
