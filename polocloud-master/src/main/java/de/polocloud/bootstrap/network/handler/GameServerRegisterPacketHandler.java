package de.polocloud.bootstrap.network.handler;

import com.google.inject.Inject;
import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.event.gameserver.CloudGameServerStatusChangeEvent;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.gameserver.ServiceVisibility;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.command.CommandListAcceptorPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerMaintenanceUpdatePacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerMaxPlayersUpdatePacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerMotdUpdatePacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerRegisterPacket;
import de.polocloud.api.network.protocol.packet.master.MasterRequestServerListUpdatePacket;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.TemplateType;
import de.polocloud.bootstrap.client.IWrapperClientManager;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.bootstrap.config.messages.Messages;
import de.polocloud.bootstrap.gameserver.SimpleGameServer;
import de.polocloud.bootstrap.pubsub.MasterPubSubManager;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutionException;

public class GameServerRegisterPacketHandler extends IPacketHandler<Packet> {

    @Inject
    private IGameServerManager gameServerManager;

    @Inject
    private IWrapperClientManager wrapperClientManager;

    @Inject
    private MasterConfig masterConfig;

    @Inject
    private MasterPubSubManager pubSubManager;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {

        GameServerRegisterPacket packet = (GameServerRegisterPacket) obj;

        try {
            IGameServer gameServer = gameServerManager.getGameSererBySnowflake(packet.getSnowflake()).get();

            ((SimpleGameServer) gameServer).setCtx(ctx);
            ((SimpleGameServer) gameServer).setPort(packet.getPort());

            Messages messages = masterConfig.getMessages();
            gameServer.sendPacket(new GameServerMaintenanceUpdatePacket(gameServer.getTemplate().isMaintenance(),
                gameServer.getTemplate().getTemplateType().equals(TemplateType.PROXY) ?
                    messages.getProxyMaintenanceMessage() : messages.getGroupMaintenanceMessage()));

            if (gameServer.getTemplate().getTemplateType().equals(TemplateType.MINECRAFT)) {
                gameServer.sendPacket(new CommandListAcceptorPacket());
            }

            gameServer.sendPacket(new GameServerMaxPlayersUpdatePacket(
                gameServer.getTemplate().getTemplateType().equals(TemplateType.PROXY) ? messages.getNetworkIsFull() : messages.getServiceIsFull()
                , gameServer.getTemplate().getMaxPlayers()));

            gameServer.sendPacket(new GameServerMotdUpdatePacket(gameServer.getMotd()));

            gameServer.setStatus(GameServerStatus.RUNNING);
            gameServer.setVisible(ServiceVisibility.VISIBLE);

            ITemplate template = gameServer.getTemplate();
            if (template.getTemplateType() == TemplateType.MINECRAFT) {
                try {
                    gameServerManager.getGameServersByType(TemplateType.PROXY).get().stream().filter(key -> key.getStatus() == GameServerStatus.RUNNING).forEach(it -> {
                        it.sendPacket(new MasterRequestServerListUpdatePacket(gameServer.getName(), "127.0.0.1", gameServer.getPort(), gameServer.getSnowflake()));
                        //TODO update host
                    });
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            pubSubManager.publish("polo:event:serverStarted", gameServer.getName());
            EventRegistry.fireEvent(new CloudGameServerStatusChangeEvent(gameServer, CloudGameServerStatusChangeEvent.Status.RUNNING));

            Logger.log(LoggerType.INFO, "The server " + ConsoleColors.LIGHT_BLUE + gameServer.getName() +
                ConsoleColors.GRAY + " is now " + ConsoleColors.GREEN + "connected" + ConsoleColors.GRAY +
                ". (" + (System.currentTimeMillis() - gameServer.getStartTime()) + "ms)");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return GameServerRegisterPacket.class;
    }
}
