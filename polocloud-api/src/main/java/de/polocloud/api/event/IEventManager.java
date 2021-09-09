package de.polocloud.api.event;

import de.polocloud.api.event.base.CloudEvent;
import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventPriority;
import de.polocloud.api.event.handling.IEventHandler;

import java.util.function.Consumer;

public interface IEventManager {

    /**
     * Registers a class to wait for events
     *
     * @param listener the listener
     */
    void registerListener(IListener listener);

    /**
     * Registers a directly handling method for a given {@link CloudEvent}
     *
     * @param eventClass the class of the event
     * @param priority the priority
     * @param handler the handler
     * @param <E> the generic
     */
    <E extends CloudEvent> void registerEvent(Class<E> eventClass, EventPriority priority, Consumer<E> handler);

    /**
     * Unregisters a class
     *
     * @param listenerClass the listener
     */
    void unregisterListener(Class<? extends IListener> listenerClass);

    /**
     * Registers an {@link IEventHandler}
     *
     * @param handler the handler
     * @param eventClass the event class
     */
    <E extends CloudEvent> void registerHandler(Class<E> eventClass, IEventHandler<E> handler);

    /**
     * Unregisters an {@link IEventHandler}
     *
     * @param handlerClass the handler class
     */
    <E extends CloudEvent> void unregisterHandler(Class<E> eventClass, Class<? extends IEventHandler<E>> handlerClass);

    /**
     * Calls an Event with the driver
     * If this instance is bridge it calls an event
     * and sets this service on blacklist to receive the same event again
     * to prevent double-executing events
     *
     * If this instance is cloud it just sends packets to all
     * clients and sets the cloud on blacklist to receive the same event again
     *
     * @param event the event to call
     */
    void fireEvent(CloudEvent event);

    /**
     * Calls an event and fires the consumer after its called
     *
     * @param event the event
     * @param callback the callback
     * @param <E> the generic
     */
    <E extends CloudEvent> void fireEvent(E event, Consumer<E> callback);
}
