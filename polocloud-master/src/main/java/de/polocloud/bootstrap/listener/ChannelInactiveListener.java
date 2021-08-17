package de.polocloud.bootstrap.listener;

import com.google.inject.Inject;
import de.polocloud.api.event.EventHandler;
import de.polocloud.api.event.channel.ChannelInactiveEvent;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.bootstrap.client.IWrapperClientManager;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;
import io.netty.channel.ChannelHandlerContext;

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

        gameServerManager.getGameServerByConnection(ctx).thenAccept(iGameServer -> {
            Logger.log(LoggerType.INFO, "The service " + ConsoleColors.LIGHT_BLUE + iGameServer.getName() +
                ConsoleColors.GRAY + " is now " + ConsoleColors.RED + "disconnected" + ConsoleColors.GRAY + "!");
            gameServerManager.unregisterGameServer(iGameServer);
        });
    }

    public boolean sameChannel(ChannelHandlerContext ct, ChannelHandlerContext ctx) {
        return ct.channel().id().asLongText().equalsIgnoreCase(ctx.channel().id().asLongText());
    }

}
