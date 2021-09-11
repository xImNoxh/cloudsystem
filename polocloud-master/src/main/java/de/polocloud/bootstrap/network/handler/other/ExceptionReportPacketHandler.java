package de.polocloud.bootstrap.network.handler.other;

import de.polocloud.api.network.packets.master.MasterReportExceptionPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.bootstrap.Master;
import io.netty.channel.ChannelHandlerContext;


public class ExceptionReportPacketHandler implements IPacketHandler<MasterReportExceptionPacket> {


    @Override
    public void handlePacket(ChannelHandlerContext ctx, MasterReportExceptionPacket packet) {
        Throwable throwable = packet.getThrowable();
        if (throwable == null) {
            return;
        }
        Master.getInstance().reportException(throwable);
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return MasterReportExceptionPacket.class;
    }
}
