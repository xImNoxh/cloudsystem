package de.polocloud.api.event.impl.server;

import de.polocloud.api.event.base.EventData;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;

import java.io.IOException;

@EventData(nettyFire = true)
public class CloudGameServerStatusChangeEvent extends GameServerEvent {

    private final GameServerStatus status;

    public CloudGameServerStatusChangeEvent(IGameServer gameServer, GameServerStatus status) {
        super(gameServer);
        this.status = status;
    }

    public GameServerStatus getStatus() {
        return status;
    }

}
