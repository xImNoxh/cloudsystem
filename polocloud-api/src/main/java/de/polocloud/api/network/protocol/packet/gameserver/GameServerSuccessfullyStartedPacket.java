package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class GameServerSuccessfullyStartedPacket extends Packet {

    private String serverName;
    private long snowflake;

    public GameServerSuccessfullyStartedPacket() {}

    public GameServerSuccessfullyStartedPacket(String serverName, long snowflake) {
        this.serverName = serverName;
        this.snowflake = snowflake;
    }

    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, serverName);
        byteBuf.writeLong(snowflake);

    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        serverName = readString(byteBuf);
        snowflake = byteBuf.readLong();
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public long getSnowflake() {
        return snowflake;
    }

    public void setSnowflake(long snowflake) {
        this.snowflake = snowflake;
    }
}
