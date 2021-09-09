package de.polocloud.bootstrap.listener;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.handling.EventPriority;
import de.polocloud.api.event.handling.IEventHandler;
import de.polocloud.api.event.impl.net.ChannelActiveEvent;
import de.polocloud.api.event.impl.player.CloudPlayerSwitchServerEvent;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.player.ICloudPlayer;

public class ChannelActiveListener implements IEventHandler<ChannelActiveEvent> {


    @Override
    public void handleEvent(ChannelActiveEvent event) {

    }
}
