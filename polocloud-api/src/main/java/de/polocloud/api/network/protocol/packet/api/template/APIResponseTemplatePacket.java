package de.polocloud.api.network.protocol.packet.api.template;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.template.ITemplate;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class APIResponseTemplatePacket extends Packet {

    private UUID requestId;
    private Collection<ITemplate> response;
    private Type type;

    public APIResponseTemplatePacket() {

    }

    public APIResponseTemplatePacket(UUID requestId, Collection<ITemplate> response, Type type) {
        this.requestId = requestId;
        this.response = response;
        this.type = type;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(requestId.toString());

        int size = response.size();
        buf.writeInt(size);

        for (ITemplate template : response) {
            buf.writeTemplate(template);
        }
        buf.writeString(type.toString());
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {

        requestId = UUID.fromString(buf.readString());
        response = new ArrayList<>();
        int size = buf.readInt();

        for (int i = 0; i < size; i++) {
            ITemplate tmpGameServer = buf.readTemplate();
            response.add(tmpGameServer);
        }

        type = Type.valueOf(buf.readString());
    }

    public UUID getRequestId() {
        return requestId;
    }

    public Type getType() {
        return type;
    }

    public Collection<ITemplate> getResponse() {
        return response;
    }

    public enum Type {
        SINGLE,
        LIST,
        BOOLEAN;
    }

}
