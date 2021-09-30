package de.polocloud.api.network.packets.master;

import de.polocloud.api.common.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;

@AutoRegistry//(id = 0x35)
public class MasterRequestServerListUpdatePacket extends Packet {

    private String name;
    private String host;
    private int port;
    private long snowflake;

    public MasterRequestServerListUpdatePacket() {

    }

    public MasterRequestServerListUpdatePacket(String name, String host, int port, long snowflake) {
        this.host = host;
        this.port = port;
        this.snowflake = snowflake;
        this.name = name;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(name);
        buf.writeString(host);
        buf.writeInt(port);
        buf.writeLong(snowflake);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        name = buf.readString();
        host = buf.readString();

        port = buf.readInt();
        snowflake = buf.readLong();
    }

    public String getName() {
        return name;
    }

    public long getSnowflake() {
        return snowflake;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

}
