package net.innectis.innplugin.system.game.games;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.IdpPlayer;

public class CastAbility {

    private final Long taskId;
    private final boolean cancelOnHit, cancelOnMove;
    private Runnable cancelAction;

    /**
     * Creates a new 'CastTask' with the given settings and actions.
     * This method allows the easy managing of tasks, such as events or cancelling.
     * @param player The player running the task.
     * @param intervalAction The action that occurs every 1/10th of the process. (Can be null)
     * @param action The action that occurs when the task is complete (Cannot be null)
     * @param cancelAction The action that occurs when the task is cancelled (Can be null)
     * @param speed The total time (in millis) that it takes to execute the task.
     * @param cancelOnHit If the task is cancelled when the player is hit.
     * @param cancelOnMove If the task is cancelled when the player moves.
     */
    public CastAbility(IdpPlayer player, Runnable intervalAction, Runnable action, Runnable cancelAction, int speed, boolean cancelOnHit, boolean cancelOnMove) {
        this.taskId = InnPlugin.getPlugin().getTaskManager().addTask(new CastTask(player, intervalAction, action, cancelAction, speed));
        this.cancelOnHit = cancelOnHit;
        this.cancelOnMove = cancelOnMove;
        this.cancelAction = cancelAction;
    }

    /**
     * The ID of the task (Assigned by task manager) created.
     * @return
     */
    public Long getTaskId() {
        return taskId;
    }

    /**
     * If the task should be cancelled when the player is hit.
     * This does not cancel the task automatically!!
     * @return
     */
    public boolean isCancelOnHit() {
        return cancelOnHit;
    }

    /**
     * If the task should be cancelled when the player moves.
     * This does not cancel the task automatically!
     * @return
     */
    public boolean isCancelOnMove() {
        return cancelOnMove;
    }

    /**
     * Cancels the task (if not already ended).
     * This will also run the cancelAction runnable if not null.
     */
    public void cancel() {
        if (InnPlugin.getPlugin().getTaskManager().getTask(taskId) != null) {
            InnPlugin.getPlugin().getTaskManager().removeTask(taskId);
            if (cancelAction != null) {
                cancelAction.run();
            }
        }
    }

}
