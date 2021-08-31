package de.polocloud.api.network.request.base.future;

import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.request.base.other.PoloCloudQueryTimeoutException;
import de.polocloud.api.network.request.base.component.PoloComponent;

public interface PoloFuture<T> {

    /**
     * The time the future took to complete
     * @return long ms
     */
    long getCompletionTimeMillis();

    /**
     * Adds a listener to this query
     *
     * @param listener the listener
     * @return current future
     */
    PoloFuture<T> addListener(PoloFutureListener<T> listener);

    /**
     * Checks if the {@link PoloComponent} was successful
     * If not completed, yet it will return false
     *
     * @return boolean
     */
    boolean isSuccess();

    /**
     * Checks if the {@link PoloComponent} has been completed
     *
     * @return boolean
     */
    boolean isCompleted();

    /**
     * Sets the timeOut values
     * @param ticks the tick timeout
     * @param timeOutValue the value
     * @return the current future
     */
    PoloFuture<T> timeOut(long ticks, T timeOutValue);

    /**
     * The provided packet if sent
     */
    Packet pullPacket() throws PoloCloudQueryTimeoutException;

    /**
     * Marks this future as dummy
     */
    PoloFuture<T> nonBlocking(T blockingObject);

    /**
     * If there was an {@link Throwable} in the request
     * it will be returned otherwise it will return null
     *
     * @return error
     */
    Throwable getError();

    /**
     * This pulls the response and stops the current thread until
     * a value is returned so the thread can go on and will be started again
     *
     * @return The response to this request
     */
    T pullValue() throws PoloCloudQueryTimeoutException;

    /**
     * Gets the {@link PoloComponent} that belongs
     * to this {@link PoloFuture}
     *
     * @return request
     */
    PoloComponent<T> getRequest();

}
