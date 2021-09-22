package de.polocloud.wrapper.impl.handler;

import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.packets.master.MasterLoginResponsePacket;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.wrapper.Wrapper;
import io.netty.channel.ChannelHandlerContext;

public class WrapperHandlerLoginResponse implements IPacketHandler<Packet> {

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        MasterLoginResponsePacket packet = (MasterLoginResponsePacket) obj;

        PoloLogger.print(LogLevel.INFO, packet.getMessage());

        if (!packet.isResponse()){
            System.exit(-1);
        }

        Wrapper.getInstance().setAuthenticated(true);
        Wrapper.getInstance().update();
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return MasterLoginResponsePacket.class;
    }
}
