package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class GameServerRegisterPacket extends Packet {

    private long snowflake;
    private int port;

    public GameServerRegisterPacket() {

    }

    public GameServerRegisterPacket(long snowflake, int port) {
        this.snowflake = snowflake;
        this.port = port;
    }


    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        byteBuf.writeLong(snowflake);
        byteBuf.writeInt(port);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        this.snowflake = byteBuf.readLong();
        this.port = byteBuf.readInt();
    }

    public int getPort() {
        return port;
    }

    public long getSnowflake() {
        return snowflake;
    }

}
