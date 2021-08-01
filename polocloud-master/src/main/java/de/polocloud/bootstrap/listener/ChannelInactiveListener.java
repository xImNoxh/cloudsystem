package de.polocloud.bootstrap.listener;

import com.google.inject.Inject;
import de.polocloud.api.event.EventHandler;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.event.channel.ChannelInactiveEvent;
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

        wrapperClientManager.getWrapperClients().stream().filter(key -> sameChannel(key.getConnection(), ctx)).forEach(it -> {
            Logger.log(LoggerType.INFO, "Wrapper " + it.getName() + " disconnected!");
            wrapperClientManager.removeWrapper(it);
        });

        try {
            gameServerManager.getGameServers().get().stream().filter(key -> sameChannel(((SimpleGameServer) key).getCtx(), ctx)).forEach(key -> {
                Logger.log(LoggerType.INFO, "The service " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + key.getName() +
                    ConsoleColors.GRAY.getAnsiCode() + " is now " + ConsoleColors.RED.getAnsiCode() + "disconnected" + ConsoleColors.GRAY.getAnsiCode() + "!");
                gameServerManager.unregisterGameServer(key);
            });
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public boolean sameChannel(ChannelHandlerContext ct, ChannelHandlerContext ctx){
        return ct.channel().id().asLongText().equalsIgnoreCase(ctx.channel().id().asLongText());
    }


}
