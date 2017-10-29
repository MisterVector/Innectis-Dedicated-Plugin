package net.innectis.innplugin.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.tasks.thread.ThreadDispatcher;

/**
 *
 * @author Hret
 */
public final class TaskManager {

    private static final Object _syncLock = new Object();
    private TaskExecutor taskThread;
    private List<TaskWrapper> taskList;
    private long idCounter;

    public TaskManager(InnPlugin plugin) {
        initTasker(plugin);
    }

    /**
     * Initialize the tasker
     */
    private void initTasker(InnPlugin plugin) {
        taskList = Collections.synchronizedList(new LinkedList<TaskWrapper>());
        taskThread = new TaskExecutor(this, plugin);
        idCounter = 0;
    }

    /**
     * Checks if the taskter is running
     * @return true if running
     */
    public boolean taskerRunning() {
        return (taskThread != null && taskThread.isAlive());
    }

    /**
     * This forces an execute on all tasks.
     * The lastexecution time of the tasks will be changed to the current time.
     *
     * @deprecated this is deprecated because its an unsafe method.
     * It will lock the taskmanager for a longer period of time.
     */
    @Deprecated
    public void executeAllTasks() {
        stopTasker();
        // Sleep 1 second to let tasker stop
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        synchronized (_syncLock) {
            for (Task task : taskList) {
                try {
                    task.run();
                } catch (Exception t) {
                    InnPlugin.logError("Task " + task.getName() + " made an uncaught exception! ", t);
                }
            }
        }
    }

    /**
     * Starts the tasker
     * @throws TaskerStillRunningException if the taster is already/still running
     */
    public void startTasker() throws TaskerStillRunningException {
        if (InnPlugin.isDebugEnabled()) {
            InnPlugin.logDebug("Starting tasker loop.");
        }

        if (taskerRunning()) {
            throw new TaskerStillRunningException("Tasker is still running!");
        }
        taskThread.start();
    }

    /**
     * Stops the tasker, but let it finish the current loop
     */
    public void stopTasker() {
        taskThread.stopGracefully();
    }

    /**
     * This forces the thread to stop
     * @deprecated this is deprected because its an unsafe method
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public void forceStopTasker() {
        taskThread.stop();
    }

    /**
     * Adds a new task to the taskmanager.
     * <p/>
     * An important thing to keep in mind is that the delay of a task will
     * not be fully accurate.
     * <p/>
     * If the given delay is, forinstance 10ms, its entirely possible that the
     * actual time till execution is larger. However this delay shouldn't be to
     * much.
     * @param task
     * The task that needs to be added to the taskmanager
     *
     * @return id
     * The return value is the ID of the task and can be used to remove the task
     * if needed.
     */
    public long addTask(Task task) {
        synchronized (_syncLock) {
            long id = idCounter++;
            taskList.add(new TaskWrapper(task, id));
            return id;
        }
    }

    /**
     * Runs and if specified, removes a specific task.
     * @param taskid
     * @return True is successful
     */
    public boolean runTask(long taskid, boolean remove) {
        for (TaskWrapper task : getTaskWrappers()) {
            if (task.getId() == taskid) {
                task.run();
                if (remove) {
                    return removeTask(task);
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Removes a task
     * @param task
     * @return True if successful
     */
    public Task getTask(long taskid) {
        for (TaskWrapper task : getTaskWrappers()) {
            if (task.getId() == taskid) {
                return task.getRealtask();
            }
        }
        return null;
    }

    /**
     * Removes a task
     * @param task
     * @return True if successful
     */
    public boolean removeTask(long taskid) {
        for (TaskWrapper task : getTaskWrappers()) {
            if (task.getId() == taskid) {
                return removeTask(task);
            }
        }
        return false;
    }

    /**
     * Removes a task
     * @param task
     * @return True if succes
     */
    public boolean removeTask(Task task) {
        // Not when server is shutting down.
        if (InnPlugin.isShuttingDown()) {
            return false;
        }

        if (task instanceof TaskWrapper) {
            synchronized (_syncLock) {
                return taskList.remove((TaskWrapper) task);
            }
        } else {
            for (TaskWrapper looptask : getTaskWrappers()) {
                if (looptask.getRealtask() == task) {
                    synchronized (_syncLock) {
                        return taskList.remove(looptask);
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns all tasks that are currently listed
     *
     * @return an unmodifiable (copied) list of the tasks
     */
    private List<TaskWrapper> getTaskWrappers() {
        synchronized (_syncLock) {
            return Collections.unmodifiableList(new ArrayList<TaskWrapper>(taskList));
        }
    }

    /**
     * Returns all tasks that are currently listed
     *
     * @return an unmodifiable (copied) list of the tasks
     */
    public List<Task> getTasks() {
        synchronized (_syncLock) {
            return Collections.unmodifiableList(new ArrayList<Task>(taskList));
        }
    }
}

final class TaskExecutor extends Thread {

    private InnPlugin plugin;
    private TaskManager manager;
    private Boolean shutdown = false;
    private ThreadDispatcher dispatcher;

    public TaskExecutor(TaskManager manager, InnPlugin plugin) {
        this.manager = manager;
        this.setName("IDP Executor thread");

        this.plugin = plugin;
        this.dispatcher = new ThreadDispatcher(10);
    }

    /**
     * If this method is called, the thread will finish its loop and then stop
     */
    public void stopGracefully() {
        shutdown = true;
    }

    @Override
    @SuppressWarnings({"deprecation", "SleepWhileInLoop"})
    public void run() {
        List<Task> tasks;
        long currentTime;

        while (!shutdown) {
            // Get tasks
            tasks = manager.getTasks();
            currentTime = System.currentTimeMillis();
            // Run tasks
            for (Task task : tasks) {
                if (task.mustRunAgain(currentTime)) {
                    task.idpRun(plugin, dispatcher, currentTime);
                }

                // When finished remove the task
                if (task.finished()) {
                    manager.removeTask(task);
                }

            }

            // Sleep 0.01 second at the end of every loop
            try {
                if (!shutdown) {
                    Thread.sleep(10);
                }
            } catch (Exception e) {
            }
        }
        // Stop
        stop();
    }
}

/**
 * @author Hret
 *
 * Proxy class that will add ID support to task, and delegate stuff to task
 */
final class TaskWrapper implements Task {

    private final Task realtask;
    private final long id;

    public TaskWrapper(Task realtask, long id) {
        this.realtask = realtask;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public Task getRealtask() {
        return realtask;
    }

    public long getLastExecution() {
        return realtask.getLastExecution();
    }

    public String getName() {
        return realtask.getName();
    }

    public boolean mustRunAgain(long currentTime) {
        return realtask.mustRunAgain(currentTime);
    }

    public void setLastExecution(Long time) {
        realtask.setLastExecution(time);
    }

    public boolean finished() {
        return realtask.finished();
    }

    public void run() {
        realtask.run();
    }

    public void idpRun(InnPlugin plugin, ThreadDispatcher dispatch, Long currentTime) {
        realtask.idpRun(plugin, dispatch, currentTime);
    }
    
}
