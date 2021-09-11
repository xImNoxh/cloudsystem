package de.polocloud.api.event.impl.server;

import de.polocloud.api.event.base.EventData;
import de.polocloud.api.event.base.CloudEvent;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.base.SimpleGameServer;

@EventData(nettyFire = true)
public abstract class GameServerEvent extends CloudEvent {

    private final SimpleGameServer gameServer;

    public GameServerEvent(IGameServer gameServer) {
        this.gameServer = (SimpleGameServer) gameServer;
    }

    public IGameServer getGameServer() {
        return this.gameServer;
    }
}
