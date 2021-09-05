package de.polocloud.wrapper.impl.handler;

import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.network.packets.wrapper.WrapperTransferModulesPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import io.netty.channel.ChannelHandlerContext;

public class WrapperHandlerFileTransfer implements IPacketHandler<Packet> {

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet packet) {
        WrapperTransferModulesPacket fileTransferPacket = (WrapperTransferModulesPacket) packet;
        PoloLogger.print(LogLevel.INFO, "§7Received §3" + fileTransferPacket.getModulesWithInfo().size() + " §bModules§7!");
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return WrapperTransferModulesPacket.class;
    }
}
