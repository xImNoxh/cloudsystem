package de.polocloud.api.event.impl.server;

import de.polocloud.api.event.base.EventData;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;

@EventData(nettyFire = true)
public class GameServerStatusChangeEvent extends GameServerEvent {

    private final GameServerStatus status;

    public GameServerStatusChangeEvent(IGameServer gameServer, GameServerStatus status) {
        super(gameServer);
        this.status = status;
    }

    public GameServerStatus getStatus() {
        return status;
    }

}
