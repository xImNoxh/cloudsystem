package de.polocloud.api.network.protocol.packet.base.response.def;

import de.polocloud.api.config.JsonData;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.base.response.ResponseState;
import de.polocloud.api.network.protocol.packet.base.response.base.IResponse;
import de.polocloud.api.network.protocol.packet.base.response.base.IResponseElement;
import de.polocloud.api.network.protocol.packet.base.response.base.elements.*;
import de.polocloud.api.common.AutoRegistry;

import java.io.IOException;

@AutoRegistry
public class Response extends Packet implements IResponse {

    private JsonData document;
    private ResponseState status;

    public Response(JsonData document) {
        this(document, ResponseState.SUCCESS);
    }

    public Response(ResponseState state) {
        this(new JsonData(), state);
    }

    public Response(String key, Object obj) {
        this(new JsonData(key, obj));
    }

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

    @Override
    public IResponseElement get(String key) {
        if (this.document.has(key)) {
            Object object = this.document.getObject(key);

            if (object instanceof String) {
                return new StringElement(this, key);
            } else if (object instanceof Number) {
                return new NumberElement(this, key, (Number) object);
            } else if (object instanceof Boolean) {
                return new BooleanElement(this, key);
            }
        }
        return new DefaultElement(this, key);
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
