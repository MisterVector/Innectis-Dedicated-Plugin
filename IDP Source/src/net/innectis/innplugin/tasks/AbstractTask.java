package net.innectis.innplugin.tasks;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.tasks.thread.ThreadDispatcher;

/**
 *
 * @author Hret
 *
 * Abstract implementation of a task
 */
abstract class AbstractTask implements Task {

    private final RunBehaviour runbehaviour;
    /** The delay */
    private final long delay;
    /** The last execution */
    private Long lastEx;

    /**
     * @param behaviour sync or asynx behaviour
     * @param delay in MS
     */
    protected AbstractTask(RunBehaviour runbehaviour, long delay) {
        this.delay = delay;
        this.lastEx = System.currentTimeMillis();
        this.runbehaviour = runbehaviour;
    }

    /**
     * Returns the delay in miliseconds
     * The delay is the amount of time this task must wait before executing again
     * @return delay
     */
    public long getDelay() {
        return delay;
    }

    /**
     * Returns the time it was last executed
     * @return delay
     */
    @Override
    public long getLastExecution() {
        return getLastEx();
    }

    /**
     * Sets the time it was last executed. <br/>
     * This will also decrement the executecount
     * @return
     */
    @Override
    public void setLastExecution(Long time) {
        this.setLastEx(time);
    }

    /**
     * Checks if the task should be run again Looking if the delay is already over
     * @param currentTime
     * @return
     */
    @Override
    public boolean mustRunAgain(long currentTime) {
        return (currentTime - getLastExecution()) > getDelay();
    }

    /**
     * The runbehaviour of this task.
     * @return
     */
    public RunBehaviour getRunbehaviour() {
        return runbehaviour;
    }

    @Override
    public void idpRun(InnPlugin plugin, ThreadDispatcher dispatch, Long currentTime) {
        setLastEx(currentTime);

        // Change behaviour depending on runtype.
        switch (getRunbehaviour()) {
            case ASYNC:
                try {
                    dispatch.dispatch(this);
                } catch (Exception ex) {
                    plugin.logError("Task " + getName() + " made an uncaught exception! ", ex);
                }
                break;

            case SYNCED:
            default:
                try {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, 0L);
                } catch (org.bukkit.plugin.IllegalPluginAccessException ipae) {
                    plugin.logError("Coult not run task: " + getName() + ", server is shutting down.");
                }
                break;
        }
    }

    /**
     * @return the lastEx
     */
    public Long getLastEx() {
        return lastEx;
    }

    /**
     * @param lastEx the lastEx to set
     */
    public void setLastEx(Long lastEx) {
        this.lastEx = lastEx;
    }

    @Override
    public String getName() {
        return "UNKNOWN";
    }

    /**
     * A method to prematurely terminate a task
     */
    public abstract void terminate();

}
