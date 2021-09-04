package de.polocloud.api.event.handling;

import de.polocloud.api.event.base.CloudEvent;

public interface IEventHandler<E extends CloudEvent> {

    /**
     * Handles the given {@link CloudEvent}
     *
     * @param event the event to handle
     */
    void handleEvent(E event);

}
