package de.polocloud.api.network.protocol.packet.base.response.extra;

import de.polocloud.api.network.protocol.packet.base.response.ResponseState;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter @Setter
public class SimpleNetworkResponse implements INetworkResponse{

    /**
     * The element of the response
     */
    private Object element;

    /**
     * The status how it went
     */
    private ResponseState status;

    /**
     * If the request was successful
     */
    private boolean success;

    /**
     * The error (if occurred)
     */
    private Throwable error;

    public SimpleNetworkResponse() {
        this.element = null;
        this.status = ResponseState.NULL;
        this.success = false;
        this.error = null;
    }

    @Override
    public void setElement(@NotNull Object element) {
        this.element = element;
        this.success = true;
    }
}
