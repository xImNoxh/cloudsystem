package de.polocloud.api.network.protocol.packet.gameserver.proxy;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
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
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(uuid.toString());
        buf.writeString(header);
        buf.writeString(footer);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        uuid = UUID.fromString(buf.readString());
        header = buf.readString();
        footer = buf.readString();
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
