package de.polocloud.api.network.packets.api.gameserver;

import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AutoRegistry
public class APIResponseGameServerPacket extends Packet {

    private UUID requestId;
    private List<IGameServer> response;
    private Type type;

    public APIResponseGameServerPacket() {

    }

    public APIResponseGameServerPacket(UUID requestId, List<IGameServer> response, Type type) {
        this.requestId = requestId;
        this.response = response;
        this.type = type;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(requestId.toString());

        int size = response.size();
        buf.writeInt(size);

        for (IGameServer gameServer : response) {
            buf.writeGameServer(gameServer);
        }
        buf.writeString(type.toString());
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {

        requestId = UUID.fromString(buf.readString());
        response = new ArrayList<>();
        int size = buf.readInt();

        for (int i = 0; i < size; i++) {
            IGameServer tmpGameServer = buf.readGameServer();
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

    public List<IGameServer> getResponse() {
        return response;
    }

    public enum Type {
        SINGLE,
        LIST,
        BOOLEAN
    }

}
