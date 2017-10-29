package net.innectis.innplugin.listeners;

/**
 *
 * @author Hret
 *
 * Interface for events.
 */
public interface InnEvent {

    /**
     * The type of event this InnEvent belongs to
     * @return
     */
    public InnEventType getType();

    /**
     * This value sets if the event should trigger the normal checks after this event.
     * When set to true, it will immediatly skip to bukkit, and not do the basic checks.
     * @param terminate
     */
    void setTerminate(boolean terminate);

    /**
     * When true, the listener will not do its own events, and will let bukkit do the rest.
     * @return
     */
    boolean shouldTerminate();

    /**
     * Reference to the InnPlugin object
     * @return
     */
    //InnPlugin getPlugin();

}
