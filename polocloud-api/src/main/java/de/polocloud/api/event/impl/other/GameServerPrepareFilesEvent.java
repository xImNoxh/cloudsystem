package de.polocloud.api.event.impl.other;

import de.polocloud.api.event.base.CloudEvent;
import de.polocloud.api.gameserver.base.IGameServer;

import java.io.File;

public class GameServerPrepareFilesEvent extends CloudEvent {

    private final IGameServer gameServer;
    private final File serverLocation;

    public GameServerPrepareFilesEvent(IGameServer gameServer, File serverLocation) {
        this.gameServer = gameServer;
        this.serverLocation = serverLocation;
    }

    public File getServerLocation() {
        return serverLocation;
    }

    public IGameServer getGameServer() {
        return gameServer;
    }
}
