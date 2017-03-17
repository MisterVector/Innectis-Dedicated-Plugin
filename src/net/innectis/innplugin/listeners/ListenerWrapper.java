package net.innectis.innplugin.listeners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.innectis.innplugin.InnPlugin;

/**
 * @author Hret
 *
 * A wrapper class that will handle data for a secondairy listener.
 * This class will keep track of the events and their priority.
 */
final class ListenerWrapper {

    private final UUID id;
    private final ISecondairyListener listener;
    private Map<InnEventType, MethodMarker> events;

    public ListenerWrapper(ISecondairyListener listener) {
        this.id = UUID.randomUUID();
        this.listener = listener;

        initalizeEvents();
    }

    /**
     * The ID for the listener.
     * @return an unique identifier for the listener.
     */
    public UUID getId() {
        return id;
    }

    /**
     * The listener the wrapper wraps.
     * @return
     */
    public ISecondairyListener getListener() {
        return listener;
    }

    /**
     * This will initialize the events into the marker.
     * It will only register valid events, that are events with the InnEventMarker annotation
     * and with or without an IdpEvent object as parameter.
     */
    private void initalizeEvents() {
        // Clear the event list
        events = new HashMap<InnEventType, MethodMarker>();

        InnEventMarker annot;
        // Loop through the methods
        for (Method method : listener.getClass().getDeclaredMethods()) {
            annot = method.getAnnotation(InnEventMarker.class);

            // Check for the correct annotation, and if not an ignored event type.
            if (annot != null && annot.type() != InnEventType.NONE) {

                // Skip events with NONE as priority
                if (annot.priority() != InnEventPriority.NONE) {
                    boolean hasEventParams = false;

                    // Check if there is a parameter, and if its valid.
                    if (method.getParameterTypes().length > 0) {
                        if (InnEvent.class.isAssignableFrom(method.getParameterTypes()[0])) {
                            hasEventParams = true;
                        } else {
                            // Illegal event! skip
                            InnPlugin.logError("Event with unknown parameter! " + listener.getName() + "::" + method.getName());
                            continue;
                        }
                    }

                    // This is important, otherwise the method cannot be called!
                    method.setAccessible(true);
                    // Add a marker of the event.
                    events.put(annot.type(), new MethodMarker(method, annot.priority(), hasEventParams));
                }
            }
        }
    }

    /**
     * Gets the event priority of the given eventtype.
     * If the listener does not suppot this event priority NONE will be given.
     * @param type
     * @return The priority or NONE
     */
    public InnEventPriority getEventPriority(InnEventType type) {
        if (events.containsKey(type)) {
            return events.get(type).getPriority();
        }
        return InnEventPriority.NONE;
    }

    /**
     * Fires the given event to the corresponding listener.
     * @param event
     */
    public void fireEvent(InnEvent event) throws ListenerException {
        if (events.containsKey(event.getType()) && listener != null) {
            events.get(event.getType()).invoke(event, listener);
        }
    }

    /**
     * Class to keep track of the information about the events in the listeners
     */
    private class MethodMarker {

        private final Method method;
        private final InnEventPriority priority;
        private final boolean hasEventParameter;

        public MethodMarker(Method method, InnEventPriority priority, boolean hasEventParameter) {
            this.method = method;
            this.priority = priority;
            this.hasEventParameter = hasEventParameter;
        }

        /**
         * The priority of the event
         * @return
         */
        public InnEventPriority getPriority() {
            return priority;
        }

        /**
         * The method of this marker
         * @return
         */
        public Method getMethod() {
            return method;
        }

        /**
         * Invoke the given event on the given listener
         * @param event
         * @param listener
         * @throws ListenerException
         */
        public void invoke(InnEvent event, ISecondairyListener listener) throws ListenerException {
            try {
                if (hasEventParameter) {
                    method.invoke(listener, method.getParameterTypes()[0].cast(event));
                } else {
                    method.invoke(listener);
                }
            } catch (IllegalAccessException ex) {
                throw new ListenerException("IllegalAccessException", ex);
            } catch (IllegalArgumentException ex) {
                throw new ListenerException("IllegalArgumentException", ex);
            } catch (InvocationTargetException ex) {
                throw new ListenerException("InvocationTargetException in " + method, ex.getCause());
            }
        }
    }

}
