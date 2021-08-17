package de.polocloud.api.network.request.base.future;

public interface PoloFutureListener<T> {

    /**
     * Handles the given {@link PoloFuture}
     *
     * @param future the future
     */
    void handle(PoloFuture<T> future);
}
