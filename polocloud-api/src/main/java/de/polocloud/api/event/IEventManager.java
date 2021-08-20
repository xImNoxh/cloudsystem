package de.polocloud.api.event;

import de.polocloud.api.event.base.IEvent;
import de.polocloud.api.event.base.IListener;
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
     * Unregisters a class
     *
     * @param listener the listener
     */
    void unregisterListener(IListener listener);

    /**
     * Registers an {@link IEventHandler}
     *
     * @param handler the handler
     * @param eventClass the event class
     */
    <E extends IEvent> void registerHandler(Class<E> eventClass, IEventHandler<E> handler);

    /**
     * Unregisters an {@link IEventHandler}
     *
     * @param handler the handler
     * @param eventClass the event class
     */
    <E extends IEvent> void unregisterHandler(Class<E> eventClass, IEventHandler<E> handler);

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
    boolean fireEvent(IEvent event);

    /**
     * Calls an event and fires the consumer after its called
     *
     * @param event the event
     * @param callback the callback
     * @param <E> the generic
     */
    <E extends IEvent> void fireEvent(E event, Consumer<E> callback);
}
