package de.polocloud.api.network.protocol.packet.gameserver.proxy;

import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.UUID;

public class ProxyTablistUpdatePacket extends Packet {

    private UUID uuid;
    private String header, footer;

    public ProxyTablistUpdatePacket() {
        
    }

    public ProxyTablistUpdatePacket(UUID uuid, String header, String footer) {
        this.uuid = uuid;
        this.header = header;
        this.footer = footer;
    }

    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, uuid.toString());
        writeString(byteBuf, header);
        writeString(byteBuf, footer);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        uuid = UUID.fromString(readString(byteBuf));
        header = readString(byteBuf);
        footer = readString(byteBuf);
    }


    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

}
