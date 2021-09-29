package de.polocloud.api.network.protocol.packet.base.response.extra;

import de.polocloud.api.network.protocol.packet.base.response.ResponseState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface INetworkPromise<E> {

    /**
     * Sets the dummy object
     *
     * @param obj the object
     * @return current element
     */
    INetworkPromise<E> dummy(E obj);

    /**
     * Marks that this element makes
     * the main thread blocked and waits for response
     */
    INetworkPromise<E> blocking();

    /**
     * Handles this element non-blocking
     * using a consumer (async)
     *
     * @param future the handler
     */
    void nonBlocking(IPromiseFuture<E> future);

    /**
     * If this request is successful
     */
    boolean isSuccess();

    /**
     * If this request is completed
     */
    boolean isCompleted();

    /**
     * The error of this element if provided
     */
    @Nullable Throwable cause();

    /**
     * The alternative value to get if the result is null
     *
     * @param e the other value
     * @return element or other element if nulled
     */
    E orElse(E e);

    /**
     * Gets this element (Might be null)
     */
    @Nullable E get();

}
