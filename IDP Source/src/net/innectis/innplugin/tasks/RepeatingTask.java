package net.innectis.innplugin.tasks;

/**
 *
 * @author Hret
 *
 * A task subclass that will act as the superclass for tasks that should never
 * be removed from the taskmanager.
 * <p/>
 * Once started these tasks will keep repeating when the taskmanager is active.
 */
public abstract class RepeatingTask extends AbstractTask {

    boolean forcedTermination = false;

    /**
     * @param behaviour sync or asynx behaviour
     * @param delay in MS
     */
    protected RepeatingTask(RunBehaviour behaviour, long delay) {
        super(behaviour, delay);
    }

    /**
     * @param behaviour sync or asynx behaviour
     * @param delay in MS
     */
    protected RepeatingTask(RunBehaviour behaviour, DefaultTaskDelays delay) {
        super(behaviour, delay.getDelay());
    }

    /**
     * This will always return false, unless the task is terminated
     * @return
     */
    @Override
    public boolean finished() {
        return forcedTermination;
    }

    @Override
    public void terminate() {
        forcedTermination = true;
    }
}
