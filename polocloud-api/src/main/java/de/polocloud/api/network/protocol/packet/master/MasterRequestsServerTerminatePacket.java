package de.polocloud.api.network.protocol.packet.master;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class MasterRequestsServerTerminatePacket extends Packet {

    private long snowflake;

    public MasterRequestsServerTerminatePacket() {
    }

    public MasterRequestsServerTerminatePacket(long snowflake) {
        this.snowflake = snowflake;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeLong(snowflake);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        snowflake = buf.readLong();
    }

    public long getSnowflake() {
        return snowflake;
    }
}
