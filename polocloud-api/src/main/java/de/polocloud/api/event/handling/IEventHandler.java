package de.polocloud.api.event.handling;

import de.polocloud.api.event.base.IEvent;

public interface IEventHandler<E extends IEvent> {

    /**
     * Handles the given {@link IEvent}
     *
     * @param event the event to handle
     */
    void handleEvent(E event);

}
