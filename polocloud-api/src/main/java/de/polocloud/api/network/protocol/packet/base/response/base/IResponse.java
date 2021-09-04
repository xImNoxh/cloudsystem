package de.polocloud.api.network.protocol.packet.base.response.base;

import de.polocloud.api.config.JsonData;
import de.polocloud.api.network.protocol.packet.base.response.ResponseState;

public interface IResponse {

    /**
     * The document of this {@link IResponse}
     *
     * @return document which stores response data
     */
    JsonData getDocument();

    /**
     * The state of this {@link IResponse}
     *
     * @return state enum
     */
    ResponseState getStatus();

    /**
     * Gets an {@link IResponseElement} if stored under a given key
     * If there is nothing stored null will be returned
     *
     * @param key the key of the element
     * @return element or null
     */
    IResponseElement get(String key);

    /**
     * Checks if this {@link IResponse} has timed out yet
     */
    boolean isTimedOut();

}
