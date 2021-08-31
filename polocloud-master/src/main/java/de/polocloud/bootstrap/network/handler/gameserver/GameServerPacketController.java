package de.polocloud.bootstrap.network.handler.gameserver;

import com.google.inject.Inject;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.impl.server.CloudGameServerStatusChangeEvent;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.packets.api.gameserver.APIRequestGameServerPacket;
import de.polocloud.api.network.packets.api.gameserver.APIResponseGameServerPacket;
import de.polocloud.api.network.packets.master.MasterRequestServerListUpdatePacket;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.ITemplateManager;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.logger.log.types.ConsoleColors;
import io.netty.channel.ChannelHandlerContext;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class GameServerPacketController {

    @Inject
    private IGameServerManager gameServerManager;

    @Inject
    private ITemplateManager templateService;

    @Inject
    private MasterConfig masterConfig;


    public void getGameServerByConnection(ChannelHandlerContext ctx, Consumer<IGameServer> server) {
        server.accept(gameServerManager.getCachedObject(ctx));
    }

    public void getRedirectPacketConnection(long snowflake, Packet packetData) {
        IGameServer iGameServer = gameServerManager.getCached(snowflake);
        iGameServer.sendPacket(packetData);
    }

    public void getTemplateByName(String name, Consumer<ITemplate> tmp) {
        tmp.accept(templateService.getTemplate(name));
    }

    public void getGameServerBySnowflake(Consumer<IGameServer> server, long snowflake) {
        server.accept(gameServerManager.getCached(snowflake));
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
        iGameServerConsumer.accept(gameServerManager.getGameServersByType(TemplateType.PROXY));
    }

    public void confirmPacketTypeResponse(APIRequestGameServerPacket.Action action, IGameServer service, UUID id, String value) {
        setListGameServerData(action, service, id, value);
        setSingletonGameServerData(action, service, id, value);
        setTemplateServerData(action, value, id, service);
    }

    public void sendServerStartLog(IGameServer server) {
        PoloLogger.print(LogLevel.INFO, "The server " + ConsoleColors.LIGHT_BLUE + server.getName() + ConsoleColors.GRAY
            + " is now " + ConsoleColors.GREEN + "connected" + ConsoleColors.GRAY + ". (" + (System.currentTimeMillis() - server.getStartTime()) + "ms)");

    }

    public void getGameServerByTemplate(String name, Consumer<List<IGameServer>> services) {
        getTemplateByName(name, template -> services.accept(gameServerManager.getGameServersByTemplate(template)));
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
        services.accept(type.equals(APIRequestGameServerPacket.Action.ALL) ? gameServerManager.getAllCached() : gameServerManager.getGameServersByType(TemplateType.valueOf(value)));
    }

    public void getGameServerSingleton(APIRequestGameServerPacket.Action type, Consumer<IGameServer> service, String value) {
        service.accept(type.equals(APIRequestGameServerPacket.Action.NAME) ? gameServerManager.getCached(value) : gameServerManager.getCached(Long.parseLong(value)));
    }


}
