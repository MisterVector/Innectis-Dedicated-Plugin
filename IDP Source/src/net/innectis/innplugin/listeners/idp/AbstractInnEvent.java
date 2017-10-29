package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.listeners.InnEvent;
import net.innectis.innplugin.listeners.InnEventType;

/**
 *
 * @author Hret
 *
 * The abstract base of all events in the IDP.
 */
abstract class AbstractInnEvent implements InnEvent {

    private final InnEventType type;
    private boolean terminate;

    public AbstractInnEvent(final InnEventType type) {
        this.type = type;
        this.terminate = false;
    }

    /**
     * Returns the type of event that this object is.
     * @return
     */
    @Override
    public final InnEventType getType() {
        return type;
    }

    /**
     * Returns weather the event system should stop cycling through events.
     * @return
     */
    @Override
    public boolean shouldTerminate() {
        return terminate;
    }

    /**
     * Sets weather the event system should stop cycling through events.
     * @param terminate
     */
    @Override
    public void setTerminate(boolean terminate) {
        this.terminate = terminate;
    }
    
}
