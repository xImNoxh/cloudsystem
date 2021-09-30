package de.polocloud.api.network.packets.gameserver;

import de.polocloud.api.common.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;

@AutoRegistry
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
