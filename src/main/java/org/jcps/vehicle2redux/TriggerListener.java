package org.jcps.vehicle2redux;

import java.util.EventListener;

/**
 * The TriggerListener interface represents an event listener for trigger events.
 * Classes implementing this interface can respond to trigger events by defining
 * the onEventOccurred method.
 */
public interface TriggerListener extends EventListener {

    /**
     * Invoked upon the occurrence of a trigger event.
     *
     * @param event The TriggerEvent associated with the event in question.
     */
    void onEventOccurred(TriggerEvent event);
}
