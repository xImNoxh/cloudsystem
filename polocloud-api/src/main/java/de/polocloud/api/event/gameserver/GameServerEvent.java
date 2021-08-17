package de.polocloud.api.event.gameserver;

import de.polocloud.api.event.CloudEvent;
import de.polocloud.api.gameserver.IGameServer;

public class GameServerEvent implements CloudEvent {

    private final IGameServer gameServer;

    public GameServerEvent(IGameServer gameServer) {
        this.gameServer = gameServer;
    }

    public IGameServer getGameServer() {
        return gameServer;
    }
}
