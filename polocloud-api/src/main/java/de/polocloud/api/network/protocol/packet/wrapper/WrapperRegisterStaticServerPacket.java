package de.polocloud.api.network.protocol.packet.wrapper;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class WrapperRegisterStaticServerPacket extends Packet {

    private String serverName;
    private long snowflake;

    public WrapperRegisterStaticServerPacket() {

    }

    public WrapperRegisterStaticServerPacket(String serverName, long snowflake) {
        this.serverName = serverName;
        this.snowflake = snowflake;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(serverName);
        buf.writeLong(snowflake);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        serverName = buf.readString();
        snowflake = buf.readLong();
    }

    public String getServerName() {
        return serverName;
    }

    public String getTemplateName() {
        return serverName.split("-")[0];
    }

    public long getSnowflake() {
        return snowflake;
    }

}
