package de.polocloud.api.network.protocol.packet.base.response.base;

public interface ResponseFutureListener {

    /**
     * Handles an incoming {@link IResponse}
     *
     * @param response the response
     */
    void handle(IResponse response);
}
