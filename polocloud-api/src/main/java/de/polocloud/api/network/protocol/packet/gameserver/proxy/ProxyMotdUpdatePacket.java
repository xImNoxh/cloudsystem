package de.polocloud.api.network.protocol.packet.gameserver.proxy;

import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.List;

public class ProxyMotdUpdatePacket extends Packet {

    private List<String> motd;

    public ProxyMotdUpdatePacket(List<String> motd) {
        this.motd = motd;
    }

    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        for (String inputs : motd) {
            writeString(byteBuf, inputs);
        }
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        motd.add(readString(byteBuf));
    }

    public List<String> getMotd() {
        return motd;
    }
}
