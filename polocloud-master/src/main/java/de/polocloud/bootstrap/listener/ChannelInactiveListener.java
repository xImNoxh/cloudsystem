package de.polocloud.bootstrap.listener;

import com.google.inject.Inject;
import de.polocloud.api.event.EventHandler;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.event.ChannelInactiveEvent;
import de.polocloud.bootstrap.client.IWrapperClientManager;
import de.polocloud.bootstrap.client.WrapperClient;
import de.polocloud.bootstrap.gameserver.SimpleGameServer;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutionException;

public class ChannelInactiveListener implements EventHandler<ChannelInactiveEvent> {

    @Inject
    private IGameServerManager gameServerManager;

    @Inject
    private IWrapperClientManager wrapperClientManager;


    @Override
    public void handleEvent(ChannelInactiveEvent event) {
        ChannelHandlerContext ctx = event.getChx();

        for (WrapperClient wrapperClient : wrapperClientManager.getWrapperClients()) {
            if (wrapperClient.getConnection().channel().id().asLongText().equalsIgnoreCase(ctx.channel().id().asLongText())) {
                Logger.log(LoggerType.INFO, "Wrapper " + wrapperClient.getName() + " disconnected!");
                wrapperClientManager.removeWrapper(wrapperClient);
                return;
            }
        }

        try {
            for (IGameServer o : gameServerManager.getGameServers().get()) {
                SimpleGameServer gameServer = (SimpleGameServer) o;
                if (gameServer.getCtx().channel().id().asLongText().equalsIgnoreCase(ctx.channel().id().asLongText())) {
                    Logger.log(LoggerType.INFO, "The service " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + gameServer.getName() +
                        ConsoleColors.GRAY.getAnsiCode() + " is now " + ConsoleColors.RED.getAnsiCode() + "disconnected" + ConsoleColors.GRAY.getAnsiCode() + "!");
                    gameServerManager.unregisterGameServer(gameServer);
                    return;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


}
