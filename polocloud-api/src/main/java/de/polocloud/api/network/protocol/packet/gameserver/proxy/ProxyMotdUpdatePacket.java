package de.polocloud.api.network.protocol.packet.gameserver.proxy;

import de.polocloud.api.gameserver.motd.ICloudMotd;
import de.polocloud.api.network.protocol.packet.IPacket;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class ProxyMotdUpdatePacket extends IPacket {
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
