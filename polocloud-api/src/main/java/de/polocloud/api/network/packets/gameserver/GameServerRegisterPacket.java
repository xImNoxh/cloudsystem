package de.polocloud.api.network.packets.gameserver;

import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;

@AutoRegistry
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
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeLong(snowflake);
        buf.writeInt(port);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        this.snowflake = buf.readLong();
        this.port = buf.readInt();
    }

    public int getPort() {
        return port;
    }

    public long getSnowflake() {
        return snowflake;
    }

}
