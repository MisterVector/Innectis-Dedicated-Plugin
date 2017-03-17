
package net.innectis.innplugin.tasks.thread;

/**
 *
 * @author Hret
 *
 * This enum defines the different states a thread can be in.
 * Depending on the state of the thread it might behave differently and gives info about what the
 * thread currently is doing.
 *
 */
public enum ThreadState {

    /** The thread is executing a task. */
    EXECUTING,
    /** The thread is sleeping and waiting for its next order. */
    SLEEPING,
    /** The thread is finishing its executing but is not yet ready. */
    CLOSING,
    /** The thread has finished its executing and released its resources. */
    STOPPED

}
