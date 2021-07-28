package de.polocloud.bootstrap.network.handler;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperRegisterStaticServerPacket;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.gameserver.SimpleGameServer;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutionException;

public class WrapperRegisterStaticServerListener extends IPacketHandler {

    @Inject
    private ITemplateService templateService;
    @Inject
    private IGameServerManager gameServerManager;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        WrapperRegisterStaticServerPacket packet = (WrapperRegisterStaticServerPacket) obj;
        Logger.log(LoggerType.INFO, "register static server with id " + packet.getSnowflake());
        try {
            IGameServer gameServer = new SimpleGameServer(packet.getServerName(), GameServerStatus.PENDING, null, packet.getSnowflake(), templateService.getTemplateByName(packet.getTemplateName()).get(), System.currentTimeMillis());
            gameServerManager.registerGameServer(gameServer);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return WrapperRegisterStaticServerPacket.class;
    }
}
