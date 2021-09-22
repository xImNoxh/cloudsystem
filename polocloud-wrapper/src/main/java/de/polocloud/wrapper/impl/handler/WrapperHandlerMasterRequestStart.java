package de.polocloud.wrapper.impl.handler;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.packets.master.MasterRequestServerStartPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.wrapper.Wrapper;
import de.polocloud.wrapper.manager.server.ServiceStarter;
import io.netty.channel.ChannelHandlerContext;

public class WrapperHandlerMasterRequestStart implements IPacketHandler<Packet> {


    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {

        MasterRequestServerStartPacket packet = (MasterRequestServerStartPacket) obj;
        IGameServer gameServer = packet.getGameServer();
        Wrapper.getInstance().startServer(gameServer);
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return MasterRequestServerStartPacket.class;
    }
}
