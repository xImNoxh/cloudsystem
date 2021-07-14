package de.polocloud.api.event;

public interface EventHandler<E extends CloudEvent> {

    void handleEvent(E event);

}
