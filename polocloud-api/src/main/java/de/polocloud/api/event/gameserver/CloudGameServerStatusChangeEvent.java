package de.polocloud.api.event.gameserver;

import de.polocloud.api.gameserver.IGameServer;

public class CloudGameServerStatusChangeEvent extends GameServerEvent {

    private Status status;

    public CloudGameServerStatusChangeEvent(IGameServer gameServer, Status status) {
        super(gameServer);
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public enum Status {
        STARTING,
        RUNNING,
        STOPPING,
    }

}
