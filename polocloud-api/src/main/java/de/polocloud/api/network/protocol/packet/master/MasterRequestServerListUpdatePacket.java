package de.polocloud.api.network.protocol.packet.master;

import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

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
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, name);
        writeString(byteBuf, host);
        byteBuf.writeInt(port);
        byteBuf.writeLong(snowflake);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        name = readString(byteBuf);
        host = readString(byteBuf);

        port = byteBuf.readInt();
        snowflake = byteBuf.readLong();
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
