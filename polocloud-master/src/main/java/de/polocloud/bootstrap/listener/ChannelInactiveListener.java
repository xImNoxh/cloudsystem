package de.polocloud.bootstrap.listener;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.handling.IEventHandler;
import de.polocloud.api.event.impl.net.ChannelInactiveEvent;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.wrapper.base.IWrapper;
import de.polocloud.api.wrapper.IWrapperManager;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
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
                PoloLogger.print(LogLevel.INFO, "§7The Wrapper §7'§3" + wrapper.getName() + "§7' §cdisconnected§7!");

                //Stopping all servers from the wrapper
                if (!wrapper.getServers().isEmpty()) {
                    for (IGameServer server : wrapper.getServers()) {
                        if (server.isRegistered()) {
                            server.terminate();
                        }
                    }
                }

                //Finally unregistering
                wrapperManager.unregisterWrapper(wrapper);
            }
        }

    }

}
