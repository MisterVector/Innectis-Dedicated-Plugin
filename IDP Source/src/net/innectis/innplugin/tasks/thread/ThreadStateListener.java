package net.innectis.innplugin.tasks.thread;

/**
 *
 * @author Hret
 *
 * Interface for a listener that listens to updates on a thread.
 */
public interface ThreadStateListener {

    /**
     * Event that gets called when the thread is updated
     * @param thread
     * The thread that has its state updated
     * @param state
     * The new state of the thread
     */
    void updateThreadState(TaskThread thread, ThreadState state);

    /**
     * This will get called when a task causes an exception that isn't caught.
     * @param aThis
     * @param exception
     */
    void onUncaughtTaskException(TaskThread thread, Exception exception);
    
}
