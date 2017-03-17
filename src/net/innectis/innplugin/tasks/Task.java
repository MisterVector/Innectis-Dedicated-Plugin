package net.innectis.innplugin.tasks;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.tasks.thread.ThreadDispatcher;

/**
 * @author Hret
 *
 * The interface for a task.
 * Tasks implementing this interface can be used by the taskmanager.
 */
public interface Task extends Runnable {

    /**
     * Returns the time it was last executed
     * @return delay
     */
    long getLastExecution();

    /**
     * Returns the name of the task, used for debugging
     * @return taskname
     */
    String getName();

    /**
     * Checks if the task should be run again Looking if the delay is already over
     * @param currentTime
     * @return
     */
    boolean mustRunAgain(long currentTime);

    /**
     * Sets the time it was last executed
     * @return
     */
    void setLastExecution(Long time);

    /**
     * This will check if the task is finished running and can be removed from the pending tasks.
     * <p/>
     * That means if this method will return <b>true</b> the task is fully done
     * with it's exection and shouldn't be executed anymore.
     * @return
     */
    boolean finished();

    /**
     * Run the task in the IDP
     * @param plugin
     * @param dispatch
     * @param currentTime
     */
    void idpRun(InnPlugin plugin, ThreadDispatcher dispatch, Long currentTime);
    
}
