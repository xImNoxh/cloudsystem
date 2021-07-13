package de.polocloud.api.network.protocol.packet.gameserver.proxy;

import de.polocloud.api.gameserver.motd.ICloudMotd;
import de.polocloud.api.network.protocol.packet.IPacket;

public class ProxyMotdUpdatePacket implements IPacket {

    private ICloudMotd cloudMotd;

    public ProxyMotdUpdatePacket() {
    }

    public ProxyMotdUpdatePacket(ICloudMotd cloudMotd) {
        this.cloudMotd = cloudMotd;
    }

    public ICloudMotd getCloudMotd() {
        return cloudMotd;
    }
}
