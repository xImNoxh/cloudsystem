package de.polocloud.api.network.protocol.packet.master;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.UUID;

public class MasterPlayerRequestJoinResponsePacket extends Packet {

    private UUID uuid;

    private String serviceName;
    private long snowflake;

    public MasterPlayerRequestJoinResponsePacket() {

    }

    public MasterPlayerRequestJoinResponsePacket(UUID uuid, String serviceName, long snowflake) {
        this.uuid = uuid;
        this.serviceName = serviceName;
        this.snowflake = snowflake;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(uuid.toString());

        buf.writeString(serviceName);

        buf.writeLong(this.snowflake);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        uuid = UUID.fromString(buf.readString());

        serviceName = buf.readString();

        this.snowflake = buf.readLong();
    }

    public String getServiceName() {
        return serviceName;
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getSnowflake() {
        return snowflake;
    }

}
