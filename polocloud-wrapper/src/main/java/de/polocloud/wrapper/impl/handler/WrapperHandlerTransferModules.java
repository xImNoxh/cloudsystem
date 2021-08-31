package de.polocloud.wrapper.impl.handler;

import de.polocloud.api.network.packets.wrapper.WrapperTransferModulesPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.wrapper.Wrapper;
import io.netty.channel.ChannelHandlerContext;

public class WrapperHandlerTransferModules implements IPacketHandler<Packet> {

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet basePacket) {
        WrapperTransferModulesPacket packet = (WrapperTransferModulesPacket) basePacket;

        Wrapper.getInstance().getModuleCopyService().setCachedModule(packet.getModulesWithInfo());
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return WrapperTransferModulesPacket.class;
    }
}
