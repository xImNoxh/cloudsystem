package de.polocloud.api.network.packets.api.cloudplayer;

import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.player.ICloudPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AutoRegistry
public class APIResponseCloudPlayerPacket extends Packet {

    private UUID requestId;
    private List<ICloudPlayer> response;
    private Type type;

    public APIResponseCloudPlayerPacket() {

    }

    public APIResponseCloudPlayerPacket(UUID requestId, List<ICloudPlayer> response, Type type) {
        this.requestId = requestId;
        this.response = response;
        this.type = type;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {

        buf.writeUUID(requestId);

        int size = response.size();
        buf.writeInt(size);

        for (ICloudPlayer cloudPlayer : response) {
            buf.writeCloudPlayer(cloudPlayer);
        }

        buf.writeString(type.toString());
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {

        requestId = buf.readUUID();
        response = new ArrayList<>();
        int size = buf.readInt();

        for (int i = 0; i < size; i++) {
            ICloudPlayer cloudPlayer = buf.readCloudPlayer();
            response.add(cloudPlayer);
        }

        type = Type.valueOf(buf.readString());

    }

    public UUID getRequestId() {
        return requestId;
    }

    public Type getType() {
        return type;
    }

    public List<ICloudPlayer> getResponse() {
        return response;
    }

    public enum Type {
        SINGLE,
        LIST,
        BOOLEAN
    }

}
