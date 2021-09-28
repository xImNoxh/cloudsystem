package de.polocloud.wrapper.impl.handler;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.network.packets.master.MasterRequestsServerTerminatePacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.wrapper.Wrapper;
import io.netty.channel.ChannelHandlerContext;

public class WrapperHandlerMasterRequestTerminate implements IPacketHandler<Packet> {

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        MasterRequestsServerTerminatePacket packet = (MasterRequestsServerTerminatePacket) obj;

        Wrapper.getInstance().stopServer(PoloCloudAPI.getInstance().getGameServerManager().getCached(packet.getName()));
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return MasterRequestsServerTerminatePacket.class;
    }
}
