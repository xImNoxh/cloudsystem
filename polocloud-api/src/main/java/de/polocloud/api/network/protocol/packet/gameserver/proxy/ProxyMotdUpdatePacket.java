package de.polocloud.api.network.protocol.packet.gameserver.proxy;

import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class ProxyMotdUpdatePacket extends Packet {
    //TODO reimplement
    /*
        private ICloudMotd cloudMotd;

        private boolean inUse;

        public ProxyMotdUpdatePacket() {
        }

        public ProxyMotdUpdatePacket(ICloudMotd cloudMotd) {
            this.cloudMotd = cloudMotd;
        }

        public ICloudMotd getCloudMotd() {
            return cloudMotd;
        }



     */
    @Override
    public void write(ByteBuf byteBuf) throws IOException {

    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {

    }
}
