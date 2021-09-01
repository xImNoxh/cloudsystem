package de.polocloud.api.network.packets.wrapper;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.util.AutoRegistry;

import java.io.IOException;

@AutoRegistry
public class WrapperServerStoppedPacket extends Packet {

    private String name;
    private long snowflake;

    public WrapperServerStoppedPacket(String name, long snowflake) {
        this.name = name;
        this.snowflake = snowflake;
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

    @Override
    public long getSnowflake() {
        return snowflake;
    }
}
