package de.polocloud.bootstrap.listener;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.event.ChannelInactiveEvent;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.bootstrap.client.IWrapperClientManager;
import de.polocloud.bootstrap.client.WrapperClient;
import de.polocloud.bootstrap.gameserver.SimpleGameServer;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;
import io.netty.channel.ChannelHandlerContext;

public class ChannelInactiveListener extends IPacketHandler {

    @Inject
    private IGameServerManager gameServerManager;

    @Inject
    private IWrapperClientManager wrapperClientManager;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {

        for (WrapperClient wrapperClient : wrapperClientManager.getWrapperClients()) {
            if(wrapperClient.getConnection().channel().id().asLongText().equalsIgnoreCase(ctx.channel().id().asLongText())){
                Logger.log(LoggerType.INFO, "Wrapper " + wrapperClient.getName() + " disconnected!");
                wrapperClientManager.removeWrapper(wrapperClient);
                return;
            }
        }

        for (IGameServer o : gameServerManager.getGameServers()) {
            SimpleGameServer  gameServer = (SimpleGameServer) o;
            if(gameServer.getCtx().channel().id().asLongText().equalsIgnoreCase(ctx.channel().id().asLongText())){
                Logger.log(LoggerType.INFO, "The service " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + gameServer.getName() +
                    ConsoleColors.GRAY.getAnsiCode() + " is now " + ConsoleColors.RED.getAnsiCode() + "disconnected" + ConsoleColors.GRAY.getAnsiCode() +"!");
                gameServerManager.unregisterGameServer(gameServer);
                return;
            }
        }
    }

    @Override
    public Class<? extends IPacket> getPacketClass() {
        return ChannelInactiveEvent.class;
    }
}
