package de.polocloud.api.network.packets.gameserver;

import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;

@AutoRegistry//(id = 0x27)
public class GameServerSuccessfullyStartedPacket extends Packet {

    private String serverName;
    private long snowflake;
    private int port;

    public GameServerSuccessfullyStartedPacket() {
    }

    public GameServerSuccessfullyStartedPacket(String serverName, long snowflake, int port) {
        this.serverName = serverName;
        this.snowflake = snowflake;
        this.port = port;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(serverName);
        buf.writeLong(snowflake);
        buf.writeInt(port);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        serverName = buf.readString();
        snowflake = buf.readLong();
        port = buf.readInt();
    }

    public int getPort() {
        return port;
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
