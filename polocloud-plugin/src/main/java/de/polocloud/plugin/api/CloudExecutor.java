package de.polocloud.plugin.api;

import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.plugin.api.server.APIGameServerManager;

public class CloudExecutor {

    private static CloudExecutor instance = new CloudExecutor();

    private IGameServerManager gameServerManager = new APIGameServerManager();

    public static CloudExecutor getInstance() {
        return instance;
    }

    public IGameServerManager getGameServerManager() {
        return gameServerManager;
    }
}
