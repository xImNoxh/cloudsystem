package de.polocloud.wrapper.impl.handler;

import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.packets.wrapper.WrapperRequestShutdownPacket;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.wrapper.Wrapper;
import io.netty.channel.ChannelHandlerContext;

public class WrapperHandlerShutdownRequest implements IPacketHandler<WrapperRequestShutdownPacket> {
    @Override
    public void handlePacket(ChannelHandlerContext ctx, WrapperRequestShutdownPacket packet) {
        PoloLogger.print(LogLevel.INFO, "The §bMaster §7requested the §cshutdown §7of this §3Wrapper§7...");
        Wrapper.getInstance().terminate();

    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return WrapperRequestShutdownPacket.class;
    }
}
