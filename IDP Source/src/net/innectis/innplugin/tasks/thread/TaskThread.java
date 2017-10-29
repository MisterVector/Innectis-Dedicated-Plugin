package net.innectis.innplugin.tasks.thread;

import net.innectis.innplugin.tasks.Task;

/**
 *
 * @author Hret
 *
 */
public class TaskThread extends Thread {

    private final Object _statelock = new Object();
    private ThreadStateListener listener;
    private ThreadState _state;
    private Task assignedTask;
    private final short idpid;

    public TaskThread(ThreadGroup group, String name, short idpid, ThreadStateListener listener) {
        super(group, name);
        this.idpid = idpid;
        this.listener = listener;
        this._state = ThreadState.SLEEPING;
    }

    /**
     * Rrturns the idpid;
     * @return
     */
    public short getIdpId() {
        return idpid;
    }

    /**
     * The IDP state of the task thread.
     * @return
     */
    public ThreadState getThreadState() {
        synchronized (_statelock) {
            return _state;
        }
    }

    /**
     * Set the IDP state of the thread
     * @param newstate
     */
    private void setThreadState(ThreadState newstate) {
        synchronized (_statelock) {
            _state = newstate;
            listener.updateThreadState(this, _state);
        }
    }

    /**
     * Assign a new task to this taskthread. The thread will be resumed from its sleeping state and
     * start executing the given task.
     * @param task
     * @throws IllegalStateException
     * Its only possible to assign a new task if the thread is currently not working.
     */
    @SuppressWarnings("deprecation")
    public void assignTask(Task task) {
        if (getThreadState() == ThreadState.SLEEPING) {
            assignedTask = task;

            if (this.isAlive()) {
                this.resume();
            } else {
                this.start();
            }
        } else {
            throw new IllegalStateException("The thread is not ready to accept a new task");
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void run() {
        while (true) {
            setThreadState(ThreadState.EXECUTING);

            if (assignedTask != null) {
                try {
                    assignedTask.run();
                } catch (Exception exception) {
                    listener.onUncaughtTaskException(this, exception);
                }

                // Release it
                assignedTask = null;
            }

            if (getThreadState() == ThreadState.CLOSING) {
                setThreadState(ThreadState.STOPPED);
                break;
            } else {
                setThreadState(ThreadState.SLEEPING);
                this.suspend();
            }
        }
    }

    /**
     * Returns the name of the task its working on (if any)
     * @return
     */
    public String getInfo() {
        return (assignedTask == null ? "None" : assignedTask.getName());
    }
    
}
