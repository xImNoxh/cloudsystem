package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class GameServerUnregisterPacket extends Packet {

    private String name;
    private long snowflake;

    public GameServerUnregisterPacket() {

    }

    public GameServerUnregisterPacket(long snowflake, String name) {
        this.snowflake = snowflake;
        this.name = name;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(name);

        buf.writeLong(snowflake);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        name = buf.readString();
        snowflake = buf.readLong();
    }

    public String getName() {
        return name;
    }

    public long getSnowflake() {
        return snowflake;
    }

}
