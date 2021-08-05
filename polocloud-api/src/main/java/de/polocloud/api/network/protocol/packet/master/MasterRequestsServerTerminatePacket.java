package de.polocloud.api.network.protocol.packet.master;

import de.polocloud.api.common.INamable;
import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import jdk.jfr.internal.Logger;

import java.io.IOException;

public class MasterRequestsServerTerminatePacket extends Packet {

    private long snowflake;

    public MasterRequestsServerTerminatePacket() {
    }

    public MasterRequestsServerTerminatePacket(long snowflake) {
        this.snowflake = snowflake;
    }

    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        byteBuf.writeLong(snowflake);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        snowflake = byteBuf.readLong();
    }

    public long getSnowflake() {
        return snowflake;
    }
}
