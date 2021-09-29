package de.polocloud.api.network.protocol.packet.base.response.extra;

public interface IPromiseFuture<E> {

    /**
     * Handles the promise
     *
     * @param promise the promise to handle
     */
    void handle(INetworkPromise<E> promise);
}
