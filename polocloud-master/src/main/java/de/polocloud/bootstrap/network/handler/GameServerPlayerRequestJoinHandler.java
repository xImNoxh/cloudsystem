package de.polocloud.bootstrap.network.handler;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerRequestJoinPacket;
import de.polocloud.api.network.protocol.packet.master.MasterPlayerRequestResponsePacket;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class GameServerPlayerRequestJoinHandler extends IPacketHandler {

    @Inject
    private IGameServerManager gameServerManager;
    @Inject
    private ITemplateService templateService;
    @Inject
    private MasterConfig config;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {
        GameServerPlayerRequestJoinPacket packet = (GameServerPlayerRequestJoinPacket) obj;
        UUID uuid = packet.getUuid();
        try {
            List<IGameServer> gameServersByTemplate = gameServerManager.getGameServersByTemplate(templateService.getTemplateByName(config.getProperties().getFallback()[0])).get();

            IGameServer targetServer = null;
            for (IGameServer iGameServer : gameServersByTemplate) {
                if (iGameServer.getStatus() == GameServerStatus.RUNNING) {

                    if (targetServer == null) {
                        targetServer = iGameServer;
                    } else {
                        if (targetServer.getCloudPlayers().size() >= iGameServer.getCloudPlayers().size()) {
                            targetServer = iGameServer;
                        }
                    }
                }
                if (targetServer == null) {
                    ctx.writeAndFlush(new MasterPlayerRequestResponsePacket(uuid, "", -1));
                    return;
                }
            }

            ctx.writeAndFlush(new MasterPlayerRequestResponsePacket(uuid, targetServer.getName(), targetServer.getSnowflake()));
            //Logger.log(LoggerType.INFO, "sending player to " + targetServer.getName() + " / " + targetServer.getSnowflake());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Class<? extends IPacket> getPacketClass() {
        return GameServerPlayerRequestJoinPacket.class;
    }
}
