package de.polocloud.api.network.protocol.packet.api;

import de.polocloud.api.network.protocol.packet.IPacket;

import java.util.UUID;

public class APIResponseGameServerPacket implements IPacket {

    private UUID requestId;
    private Object response;

    public APIResponseGameServerPacket() {

    }

    public APIResponseGameServerPacket(UUID requestId, Object response) {
        this.requestId = requestId;
        this.response = response;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public Object getResponse() {
        return response;
    }
}
