package org.jcps.vehicle2redux;

import java.util.EventObject;

/**
 * The TriggerEvent class represents an event object associated with trigger events.
 * It extends EventObject and includes a message indicating the nature of the event.
 */
public class TriggerEvent extends EventObject {

    /**
     * {@code String} containing a message for the event.
     */
    private final String message;

    /**
     * Constructs a new instance of the TriggerEvent class with the specified source
     * and a descriptive message for the event.
     *
     * @param source  The source object of the event.
     * @param message A string providing additional information about the event.
     */
    public TriggerEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    /**
     * Gets the message associated with the trigger event.
     *
     * @return The descriptive message of the event.
     */
    public String getMessage() {
        return message;
    }
}
