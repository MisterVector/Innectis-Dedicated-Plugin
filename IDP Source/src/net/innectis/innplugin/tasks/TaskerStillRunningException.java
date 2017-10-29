package net.innectis.innplugin.tasks;

import net.innectis.innplugin.IdpException;

/**
 * @author Hret
 *
 * This exception is thrown when the tasker is started when its already running.
 */
public class TaskerStillRunningException extends IdpException {

    /**
     * Creates a new instance of <code>TaskerStillRunningException</code> without detail message.
     */
    public TaskerStillRunningException() {
    }

    /**
     * Constructs an instance of <code>TaskerStillRunningException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public TaskerStillRunningException(String msg) {
        super(msg);
    }
    
}
