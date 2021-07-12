package de.polocloud.bootstrap.network.handler;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerRegisterPacket;
import de.polocloud.api.network.protocol.packet.master.MasterRequestServerListUpdatePacket;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.TemplateType;
import de.polocloud.bootstrap.client.IWrapperClientManager;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.bootstrap.gameserver.SimpleGameServer;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class GameServerRegisterPacketHandler extends IPacketHandler {

    @Inject
    private IGameServerManager gameServerManager;

    @Inject
    private IWrapperClientManager wrapperClientManager;

    @Inject
    private MasterConfig masterConfig;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {

        GameServerRegisterPacket packet = (GameServerRegisterPacket) obj;

        IGameServer gameServer = gameServerManager.getGameSererBySnowflake(packet.getSnowflake());
        ((SimpleGameServer) gameServer).setCtx(ctx);
        ((SimpleGameServer) gameServer).setPort(packet.getPort());
        gameServer.setStatus(GameServerStatus.RUNNING);


        ITemplate template = gameServer.getTemplate();
        if (template.getTemplateType() == TemplateType.MINECRAFT) {

            List<IGameServer> proxyGameServerList = gameServerManager.getGameServersByType(TemplateType.PROXY);

            for (IGameServer proxyGameServer : proxyGameServerList) {
                if(proxyGameServer.getStatus() == GameServerStatus.RUNNING){
                    proxyGameServer.sendPacket(new MasterRequestServerListUpdatePacket(gameServer.getName(), "127.0.0.1", gameServer.getPort(), gameServer.getSnowflake())); //TODO update host
                }
            }

        } else {
            List<IGameServer> serverList = gameServerManager.getGameServersByType(TemplateType.MINECRAFT);
            for (IGameServer iGameServer : serverList) {
                gameServer.sendPacket(new MasterRequestServerListUpdatePacket(gameServer.getName(), "127.0.0.1", iGameServer.getPort(),
                    iGameServer.getSnowflake())); //TODO update host
            }
        }
        Logger.log(LoggerType.INFO, "The server " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + gameServer.getName() +
            ConsoleColors.GRAY.getAnsiCode() + " is now " + ConsoleColors.GREEN.getAnsiCode() + "connected" + ConsoleColors.GRAY.getAnsiCode() + ".");
    }

    @Override
    public Class<? extends IPacket> getPacketClass() {
        return GameServerRegisterPacket.class;
    }
}
