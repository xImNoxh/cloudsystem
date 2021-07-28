package de.polocloud.api.network.protocol.packet.api.cloudplayer;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.player.ICloudPlayer;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public void write(ByteBuf byteBuf) throws IOException {

        writeString(byteBuf, requestId.toString());

        int size = response.size();
        byteBuf.writeInt(size);

        for (ICloudPlayer cloudPlayer : response) {
            writeCloudPlayer(byteBuf, cloudPlayer);
        }

        writeString(byteBuf, type.toString());

    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {

        requestId = UUID.fromString(readString(byteBuf));
        response = new ArrayList<>();
        int size= byteBuf.readInt();

        for (int i = 0; i < size; i++) {
            ICloudPlayer cloudPlayer = readCloudPlayer(byteBuf);
            response.add(cloudPlayer);
        }

        type = Type.valueOf(readString(byteBuf));

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
        BOOLEAN;
    }

}
