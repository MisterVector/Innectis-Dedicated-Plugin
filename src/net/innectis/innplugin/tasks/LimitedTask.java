package net.innectis.innplugin.tasks;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.tasks.thread.ThreadDispatcher;

/**
 *
 * @author Hret
 *
 * A task that runs with a specific delay, a certain number of times
 */
public abstract class LimitedTask extends AbstractTask {

    protected int executecount;

    /**
     * Creates a new limited task with the specified delay and execution count
     * @param delay
     * delay in milliseconds
     */
    protected LimitedTask(RunBehaviour behaviour, long delay, int executecount) {
        super(behaviour, delay);
        this.executecount = executecount;
    }

    /**
     * This returns true once the task has finished executing the amount
     * of times it is scheduled to run
     * @return
     */
    public boolean finished() {
        return executecount == 0;
    }

    @Override
    public void terminate() {
        executecount = 0;
    }

    /**
     * Checks if the task should be run again Looking if the delay is already over
     * @param currentTime
     * @return
     */
    @Override
    public boolean mustRunAgain(long currentTime) {
        return super.mustRunAgain(currentTime) && executecount > 0;
    }

    @Override
    public void idpRun(InnPlugin plugin, ThreadDispatcher dispatch, Long currentTime) {
        executed();
        super.idpRun(plugin, dispatch, currentTime);
    }

    /**
     * Decrements the execute counter
     */
    protected void executed() {
        executecount--;
    }

}
