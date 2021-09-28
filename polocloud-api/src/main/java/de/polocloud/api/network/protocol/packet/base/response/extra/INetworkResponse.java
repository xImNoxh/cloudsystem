package de.polocloud.api.network.protocol.packet.base.response.extra;

import de.polocloud.api.network.protocol.packet.base.response.ResponseState;
import org.jetbrains.annotations.NotNull;

public interface INetworkResponse {

    /**
     * Puts the object of this response
     *
     * @param element the element
     */
    void setElement(@NotNull Object element);

    /**
     * Sets the status of this response
     *
     * @param state the status
     */
    void setStatus(ResponseState state);

    /**
     * Sets the success state of this response
     *
     * @param success the state
     */
    void setSuccess(boolean success);

    /**
     * Provides the error of this response
     *
     * @param throwable the throwable
     */
    void setError(Throwable throwable);
}
