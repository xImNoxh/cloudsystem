package de.polocloud.bootstrap.listener;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.handling.IEventHandler;
import de.polocloud.api.event.impl.net.ChannelInactiveEvent;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.wrapper.IWrapper;
import de.polocloud.api.wrapper.IWrapperManager;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;
import io.netty.channel.ChannelHandlerContext;

import java.util.LinkedList;
import java.util.List;

public class ChannelInactiveListener implements IEventHandler<ChannelInactiveEvent> {

    @Override
    public void handleEvent(ChannelInactiveEvent event) {
        ChannelHandlerContext ctx = event.getChx();

        IWrapperManager wrapperManager = PoloCloudAPI.getInstance().getWrapperManager();
        List<IWrapper> wrappers = new LinkedList<>(wrapperManager.getWrappers());
        for (IWrapper wrapper : wrappers) {
            if (wrapperManager.getWrapper(wrapper.getName()) == null) {
                continue;
            }
            if (wrapper.ctx().channel().id().asLongText().equalsIgnoreCase(ctx.channel().id().asLongText())) {
                Logger.log(LoggerType.INFO, "§7The Wrapper §7'§3" + wrapper.getName() + "§7' §cdisconnected§7!");

                //Stopping all servers from the wrapper
                for (IGameServer server : wrapper.getServers()) {
                    server.terminate();
                }

                //Finally unregistering
                wrapperManager.unregisterWrapper(wrapper);
                wrapperManager.syncCache();
            }
        }

    }

}
