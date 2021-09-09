package de.polocloud.api.event.impl.server;

import de.polocloud.api.event.base.EventData;
import de.polocloud.api.gameserver.base.IGameServer;

@EventData(nettyFire = true)
public class CloudGameServerVisibleUpdateEvent extends GameServerEvent {

    private final boolean visible;

    public CloudGameServerVisibleUpdateEvent(IGameServer gameServer, boolean visible) {
        super(gameServer);
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }
}
