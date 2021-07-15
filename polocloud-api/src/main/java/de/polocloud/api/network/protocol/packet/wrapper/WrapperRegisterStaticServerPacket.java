package de.polocloud.api.network.protocol.packet.wrapper;

import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class WrapperRegisterStaticServerPacket extends Packet {

    private String serverName;

    public WrapperRegisterStaticServerPacket() {

    }

    public WrapperRegisterStaticServerPacket(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, serverName);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        serverName = readString(byteBuf);
    }

    public String getServerName() {
        return serverName;
    }

    public String getTemplateName(){
        return serverName.split("-")[0];
    }
    public long getSnowflake(){
        return Long.parseLong(serverName.split("-")[1].split("#")[0]);
    }

}
