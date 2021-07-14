package de.polocloud.api.network.protocol.packet.api;

import de.polocloud.api.network.protocol.packet.IPacket;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.UUID;

public class APIResponseGameServerPacket extends IPacket {
/*TODO reimplement

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

 */



    @Override
    public void write(ByteBuf byteBuf) throws IOException {

    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {

    }
}
