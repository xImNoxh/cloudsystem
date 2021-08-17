package de.polocloud.api.event;

public interface EventHandler<E extends CloudEvent> {

    /**
     * Handles the given {@link CloudEvent}
     *
     * @param event the event to handle
     */
    void handleEvent(E event);

}
