package de.polocloud.bootstrap.network.handler.other;

import de.polocloud.api.config.master.MasterConfig;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.network.packets.master.MasterUpdateConfigPacket;
import de.polocloud.api.network.packets.other.TextPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import io.netty.channel.ChannelHandlerContext;


public class ConfigPacketHandler implements IPacketHandler<MasterUpdateConfigPacket> {


    @Override
    public void handlePacket(ChannelHandlerContext ctx, MasterUpdateConfigPacket packet) {
        MasterConfig masterConfig = packet.getMasterConfig();
        masterConfig.update();
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return MasterUpdateConfigPacket.class;
    }
}
