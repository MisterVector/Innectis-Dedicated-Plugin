package net.innectis.innplugin.listeners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Hret
 *
 * Annotation that will mark an event for the secondairy listener.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface InnEventMarker {

    /**
     * The type of event that is given to this.
     */
    InnEventType type();

    /**
     * The priority of the event.
     * Default set to NORMAL
     */
    InnEventPriority priority() default InnEventPriority.NORMAL;

}
