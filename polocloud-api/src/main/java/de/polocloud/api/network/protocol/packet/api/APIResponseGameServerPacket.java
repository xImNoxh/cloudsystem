package de.polocloud.api.network.protocol.packet.api;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, requestId.toString());

        int size = response.size();
        byteBuf.writeInt(size);

        for (IGameServer gameServer : response) {
            writeGameServer(byteBuf, gameServer);
            /*
            writeString(byteBuf, gameServer.getName());
            byteBuf.writeLong(gameServer.getSnowflake());
            writeString(byteBuf, gameServer.getStatus().toString());
             */

        }

        writeString(byteBuf, type.toString());

    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {

        requestId = UUID.fromString(readString(byteBuf));
        response = new ArrayList<>();
        int size = byteBuf.readInt();

        for (int i = 0; i < size; i++) {
            IGameServer tmpGameServer = readGameServer(byteBuf);
            response.add(tmpGameServer);
            /*
            String name = readString(byteBuf);
            long snowflake = byteBuf.readLong();
            GameServerStatus status = GameServerStatus.valueOf(readString(byteBuf));

            IGameServer tmpGameServer = new IGameServer() {
                @Override
                public String getName() {
                    return name;
                }

                @Override
                public GameServerStatus getStatus() {
                    return status;
                }

                @Override
                public long getSnowflake() {
                    return snowflake;
                }

                @Override
                public ITemplate getTemplate() {
                    return null;
                }

                @Override
                public List<ICloudPlayer> getCloudPlayers() {
                    return null;
                }

                @Override
                public void setStatus(GameServerStatus status) {

                }

                @Override
                public long getTotalMemory() {
                    return 0;
                }

                @Override
                public int getOnlinePlayers() {
                    return 0;
                }

                @Override
                public int getPort() {
                    return 0;
                }

                @Override
                public long getPing() {
                    return 0;
                }

                @Override
                public long getStartTime() {
                    return 0;
                }

                @Override
                public void stop() {

                }

                @Override
                public void sendPacket(IPacket packet) {

                }
            };
            response.add(tmpGameServer);

 */

        }

        type = Type.valueOf(readString(byteBuf));

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
        BOOLEAN;
    }

}
