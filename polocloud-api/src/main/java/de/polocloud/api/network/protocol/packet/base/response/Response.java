package de.polocloud.api.network.protocol.packet.base.response;

import de.polocloud.api.config.JsonData;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.util.AutoRegistry;

import java.io.IOException;

@AutoRegistry
public class Response extends Packet {

    private JsonData document;
    private ResponseState status;

    public Response(JsonData document, ResponseState status) {
        this.document = document;
        this.status = status;
    }

    public JsonData getDocument() {
        return document;
    }

    public ResponseState getStatus() {
        return status;
    }

    public boolean isTimedOut() {
        return status == ResponseState.TIMED_OUT;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(document.toString());
        buf.writeEnum(status);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {

        String json = buf.readString();
        ResponseState state = buf.readEnum();
        this.document = new JsonData(json);
        this.status = state;
    }
}
