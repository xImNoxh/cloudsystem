package de.polocloud.bootstrap.network.handler.gameserver;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.packets.master.MasterRequestServerListUpdatePacket;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.logger.log.types.ConsoleColors;

import java.util.List;
import java.util.function.Consumer;

public abstract class GameServerPacketController {

    @Inject
    private IGameServerManager gameServerManager;

    public void getRedirectPacketConnection(long snowflake, Packet packetData) {
        IGameServer iGameServer = gameServerManager.getCached(snowflake);
        iGameServer.sendPacket(packetData);
    }

    public void getGameServerBySnowflake(Consumer<IGameServer> server, long snowflake) {
        server.accept(gameServerManager.getCached(snowflake));
    }

    public void updateProxyServerList(IGameServer gameServer) {
        ITemplate template = gameServer.getTemplate();
        if (template.getTemplateType() == TemplateType.MINECRAFT) {
            getGameServerByProxyType(iGameServers -> iGameServers.stream().filter(key -> key.getStatus() == GameServerStatus.RUNNING).forEach(it -> it.sendPacket(new MasterRequestServerListUpdatePacket(gameServer.getName(), "127.0.0.1", gameServer.getPort(), gameServer.getSnowflake()))));
        }
    }

    public void getGameServerByProxyType(Consumer<List<IGameServer>> iGameServerConsumer) {
        iGameServerConsumer.accept(gameServerManager.getCached(TemplateType.PROXY));
    }

    public void sendServerStartLog(IGameServer server) {
        PoloLogger.print(LogLevel.INFO, "The server " + ConsoleColors.LIGHT_BLUE + server.getName() + ConsoleColors.GRAY
            + " is now " + ConsoleColors.GREEN + "connected" + ConsoleColors.GRAY + ". (" + (System.currentTimeMillis() - server.getStartTime()) + "ms)");

    }

}
