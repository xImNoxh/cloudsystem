package de.polocloud.api.network.packets.master;

import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.common.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;

@AutoRegistry//(id = 0x37)
public class MasterRequestsServerTerminatePacket extends Packet {

    private long snowflake;
    private String name;


    public MasterRequestsServerTerminatePacket() {
    }

    public MasterRequestsServerTerminatePacket(IGameServer server) {
        this(server.getSnowflake());
        this.name = server.getName();
    }

    public MasterRequestsServerTerminatePacket(long snowflake) {
        this.snowflake = snowflake;
        this.name = "no name";
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeLong(snowflake);
        buf.writeString(name);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        snowflake = buf.readLong();
        name = buf.readString();
    }

    public String getName() {
        return name;
    }

    public long getSnowflake() {
        return snowflake;
    }
}
