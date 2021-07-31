package de.polocloud.bootstrap.network.handler;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperRegisterStaticServerPacket;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.gameserver.SimpleGameServer;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutionException;

public class WrapperRegisterStaticServerListener extends IPacketHandler<Packet> {

    @Inject
    private ITemplateService templateService;
    @Inject
    private IGameServerManager gameServerManager;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        WrapperRegisterStaticServerPacket packet = (WrapperRegisterStaticServerPacket) obj;
        Logger.log(LoggerType.INFO, "register static server with id " + packet.getSnowflake());
        IGameServer gameServer = null;
        try {
            ITemplate template = templateService.getTemplateByName(packet.getTemplateName()).get();
            gameServer = new SimpleGameServer(packet.getServerName(), GameServerStatus.PENDING, null, packet.getSnowflake(), template, System.currentTimeMillis(), template.getMotd(), template.getMaxPlayers());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        gameServerManager.registerGameServer(gameServer);
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return WrapperRegisterStaticServerPacket.class;
    }
}
