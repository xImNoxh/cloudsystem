package de.polocloud.api.event.base;


import lombok.Getter;
import lombok.Setter;


/**
 * The root class for all events, which you want to fire with the EventManager implementations. It is also meant to make
 * it possible for listeners to have access to every object of every subclass.
 *
 * @see de.polocloud.api.event.IEventManager
 */
@Setter @Getter
public abstract class CloudEvent {

    /**
     * If this packet was just sent via netty
     */
    private boolean nettyFired;

}
