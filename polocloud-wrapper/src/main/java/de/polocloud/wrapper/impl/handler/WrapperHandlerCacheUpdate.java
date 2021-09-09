package de.polocloud.wrapper.impl.handler;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.master.MasterConfig;
import de.polocloud.api.network.packets.api.GlobalCachePacket;
import de.polocloud.api.network.packets.api.MasterCache;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.api.util.gson.PoloHelper;
import io.netty.channel.ChannelHandlerContext;

public class WrapperHandlerCacheUpdate implements IPacketHandler<GlobalCachePacket> {

    @Override
    public void handlePacket(ChannelHandlerContext ctx, GlobalCachePacket packet) {
        MasterCache masterCache = packet.getMasterCache();
        PoloCloudAPI.getInstance().setCache(masterCache);
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return GlobalCachePacket.class;
    }
}
