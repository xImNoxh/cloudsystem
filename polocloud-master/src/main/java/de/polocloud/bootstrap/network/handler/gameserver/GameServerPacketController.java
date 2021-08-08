package de.polocloud.bootstrap.network.handler.gameserver;

import com.google.inject.Inject;
import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.event.gameserver.CloudGameServerStatusChangeEvent;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.gameserver.ServiceVisibility;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.api.gameserver.APIRequestGameServerPacket;
import de.polocloud.api.network.protocol.packet.api.gameserver.APIResponseGameServerPacket;
import de.polocloud.api.network.protocol.packet.command.CommandListAcceptorPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerMaintenanceUpdatePacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerMaxPlayersUpdatePacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerMotdUpdatePacket;
import de.polocloud.api.network.protocol.packet.master.MasterRequestServerListUpdatePacket;
import de.polocloud.api.pubsub.IPubSubManager;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.api.template.TemplateType;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;
import io.netty.channel.ChannelHandlerContext;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public abstract class GameServerPacketController {

    @Inject
    private IGameServerManager gameServerManager;

    @Inject
    private ITemplateService templateService;

    @Inject
    private MasterConfig masterConfig;

    /*
    @Inject
    private IPubSubManager subManager;

     */

    public void getGameServerByConnection(ChannelHandlerContext ctx, Consumer<IGameServer> server) {
        try {
            server.accept(gameServerManager.getGameServerByConnection(ctx).get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void getRedirectPacketConnection(long snowflake, Packet packetData) {
        try {
            IGameServer iGameServer = gameServerManager.getGameSererBySnowflake(snowflake).get();
            iGameServer.sendPacket(packetData);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void getTemplateByName(String name, Consumer<ITemplate> tmp) {
        try {
            tmp.accept(templateService.getTemplateByName(name).get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void getGameServerBySnowflake(Consumer<IGameServer> server, long snowflake) {
        try {
            server.accept(gameServerManager.getGameSererBySnowflake(snowflake).get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void alertMaintenanceUpdatePacket(IGameServer gameServer) {
        gameServer.sendPacket(new GameServerMaintenanceUpdatePacket(gameServer.getTemplate().isMaintenance(),
            gameServer.getTemplate().getTemplateType().equals(TemplateType.PROXY) ?
                masterConfig.getMessages().getProxyMaintenanceMessage() : masterConfig.getMessages().getGroupMaintenanceMessage()));
    }

    public void sendCloudCommandAcceptList(IGameServer gameServer) {
        if (gameServer.getTemplate().getTemplateType().equals(TemplateType.MINECRAFT))
            gameServer.sendPacket(new CommandListAcceptorPacket());

    }


    public void alertMaxPlayerUpdatePacket(IGameServer gameServer) {
        gameServer.sendPacket(new GameServerMaxPlayersUpdatePacket(
            gameServer.getTemplate().getTemplateType().equals(TemplateType.PROXY) ? masterConfig.getMessages().getNetworkIsFull() : masterConfig.getMessages().getServiceIsFull()
            , gameServer.getTemplate().getMaxPlayers()));
    }

    public void sendMotdUpdatePacket(IGameServer gameServer) {
        gameServer.sendPacket(new GameServerMotdUpdatePacket(gameServer.getMotd()));

    }

    public void updateProxyServerList(IGameServer gameServer) {
        ITemplate template = gameServer.getTemplate();
        if (template.getTemplateType() == TemplateType.MINECRAFT) {
            getGameServerByProxyType(iGameServers -> iGameServers.stream().filter(key -> key.getStatus() == GameServerStatus.RUNNING).forEach(it -> {
                it.sendPacket(new MasterRequestServerListUpdatePacket(gameServer.getName(), "127.0.0.1", gameServer.getPort(), gameServer.getSnowflake()));
            }));
        }
    }

    public void getGameServerByProxyType(Consumer<List<IGameServer>> iGameServerConsumer) {
        try {
            iGameServerConsumer.accept(gameServerManager.getGameServersByType(TemplateType.PROXY).get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void confirmPacketTypeResponse(APIRequestGameServerPacket.Action action, IGameServer service, UUID id, String value){
        setListGameServerData(action,  service, id, value);
        setSingletonGameServerData(action, service, id, value);
        setTemplateServerData(action, value, id, service);
    }

    public void sendServerStartLog(IGameServer server){
        Logger.log(LoggerType.INFO, "The server " + ConsoleColors.LIGHT_BLUE + server.getName() + ConsoleColors.GRAY
            + " is now " + ConsoleColors.GREEN + "connected" + ConsoleColors.GRAY + ". (" + (System.currentTimeMillis() - server.getStartTime()) + "ms)");
    }

    public void callServerStartedEvent(IGameServer gameServer){
      //  subManager.publish("polo:event:serverStarted", gameServer.getName());
        EventRegistry.fireEvent(new CloudGameServerStatusChangeEvent(gameServer, CloudGameServerStatusChangeEvent.Status.RUNNING));
    }

    public void getGameServerByTemplate(String name, Consumer<List<IGameServer>> services) {
        getTemplateByName(name, template -> gameServerManager.getGameServersByTemplate(template).thenAccept(it -> services.accept(it)));
    }

    public void setListGameServerData(APIRequestGameServerPacket.Action type, IGameServer server, UUID id, String value) {
        if (isPossibleListAction(type))
            getGameServerList(type, iGameServers -> server.sendPacket(new APIResponseGameServerPacket(id, iGameServers, APIResponseGameServerPacket.Type.LIST)), value);
    }

    public void setTemplateServerData(APIRequestGameServerPacket.Action action, String value, UUID id, IGameServer service) {
        if (isTemplateListType(action))
            getGameServerByTemplate(value, iGameServers -> service.sendPacket(new APIResponseGameServerPacket(id, (iGameServers), APIResponseGameServerPacket.Type.LIST)));
    }

    public void setSingletonGameServerData(APIRequestGameServerPacket.Action type, IGameServer server, UUID id, String value) {
        if (isSingletonType(type))
            getGameServerSingleton(type, service -> server.sendPacket(new APIResponseGameServerPacket(id, Collections.singletonList(service), APIResponseGameServerPacket.Type.SINGLE)), value);
    }

    public boolean isPossibleListAction(APIRequestGameServerPacket.Action type) {
        return type.equals(APIRequestGameServerPacket.Action.ALL) || type.equals(APIRequestGameServerPacket.Action.LIST_BY_TYPE);
    }

    public boolean isSingletonType(APIRequestGameServerPacket.Action type) {
        return type.equals(APIRequestGameServerPacket.Action.NAME) || type.equals(APIRequestGameServerPacket.Action.SNOWFLAKE);
    }

    public boolean isTemplateListType(APIRequestGameServerPacket.Action type) {
        return type.equals(APIRequestGameServerPacket.Action.LIST_BY_TEMPLATE);
    }

    public void getGameServerList(APIRequestGameServerPacket.Action type, Consumer<List<IGameServer>> services, String value) {
        (type.equals(APIRequestGameServerPacket.Action.ALL) ? gameServerManager.getGameServers() : gameServerManager.getGameServersByType(TemplateType.valueOf(value))).thenAccept(it -> services.accept(it));
    }

    public void getGameServerSingleton(APIRequestGameServerPacket.Action type, Consumer<IGameServer> service, String value) {
        (type.equals(APIRequestGameServerPacket.Action.NAME) ? gameServerManager.getGameServerByName(value) : gameServerManager.getGameSererBySnowflake(Long.parseLong(value))).thenAccept(it -> service.accept(it));
    }


}
