package de.polocloud.api.network.protocol.packet.base.response.extra;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface INetworkElement<E> {

    /**
     * Marks that this element makes
     * the main thread blocked and waits for response
     */
    INetworkElement<E> blocking();

    /**
     * Handles this element non blocking
     * using a consumer (async)
     *
     * @param handler the handler
     */
    void nonBlocking(Consumer<INetworkElement<E>> handler);

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
