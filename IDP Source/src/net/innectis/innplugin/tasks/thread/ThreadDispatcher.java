package net.innectis.innplugin.tasks.thread;

import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.tasks.Task;

/**
 *
 * @author Hret
 *
 * Dispatcher that creates a thread pool and gives the tasks to free threads.
 */
public class ThreadDispatcher implements ThreadStateListener {

    private ThreadGroup group;
    private Stack<Short> workingThreads;
    private Stack<Short> waitingThreads;
    private TaskThread[] threads;
    private boolean isClosing = false;
    private short activeThreads = 0;

    public ThreadDispatcher(int maxthreads) {
        threads = new TaskThread[maxthreads];

        workingThreads = new Stack<Short>();
        waitingThreads = new Stack<Short>();

        group = new ThreadGroup("IDP Taskdispatch Group");
    }

    public void dispatch(Task task) {
        if (isClosing) {
            return;
        }

        while (waitingThreads.isEmpty() && activeThreads == threads.length) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(ThreadDispatcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (waitingThreads.size() > 0) {
            threads[waitingThreads.pop()].assignTask(task);
        } else {
            threads[activeThreads] = new TaskThread(group, "TaskThread_" + activeThreads, activeThreads, this);
            threads[activeThreads].assignTask(task);
            activeThreads++;
        }
    }

    /**
     * Updates the state of the thread.
     * @param thread
     * @param state
     */
    public void updateThreadState(TaskThread thread, ThreadState state) {
        Short threadid = thread.getIdpId();
        switch (state) {
            case CLOSING:
            case STOPPED:
                workingThreads.remove(threadid);
                waitingThreads.remove(threadid);
                break;
            case SLEEPING:
                waitingThreads.add(threadid);
                workingThreads.remove(threadid);
                break;
            case EXECUTING:
                waitingThreads.remove(threadid);
                workingThreads.add(threadid);
                break;
        }
    }

    /**
     * Get uncaught exceptions
     * @param thread
     * @param exception
     */
    @Override
    public void onUncaughtTaskException(TaskThread thread, Exception exception) {
        InnPlugin.logError("Uncaught exception in thread " + thread.getName() + " (" + thread.getInfo() + ")", exception);
    }
    
}
