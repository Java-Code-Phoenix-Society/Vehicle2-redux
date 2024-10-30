package dev.jcps.vehicle2redux;

import java.util.EventObject;

/**
 * The TriggerEvent class represents an event object associated with trigger events.
 * This class extends EventObject and  includes a message indicating the nature of the event.
 *
 * @author neoFuzz
 * @see java.util.EventObject
 * @since 1.0
 */
public class TriggerEvent extends EventObject {

    /**
     * {@code String} containing a message about the event.
     */
    private final String message;

    /**
     * Constructs a new TriggerEvent instance with a specific source and an associated message.
     *
     * @param source  The object that originated the event.
     * @param message A string detailing the event specifics.
     */
    public TriggerEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    /**
     * Retrieves the message associated with the trigger event.
     *
     * @return The descriptive message of the event.
     */
    public String getMessage() {
        return message;
    }
}
