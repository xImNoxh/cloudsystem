package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.packet.IPacket;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class GameServerUnregisterPacket extends IPacket {

    private String name;
    private long snowflake;

    public GameServerUnregisterPacket() {

    }

    public GameServerUnregisterPacket(long snowflake, String name) {
        this.snowflake = snowflake;
        this.name = name;
    }


    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, name);

        byteBuf.writeLong(snowflake);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        name = readString(byteBuf);
        snowflake = byteBuf.readLong();
    }

    public String getName() {
        return name;
    }

    public long getSnowflake() {
        return snowflake;
    }

}
