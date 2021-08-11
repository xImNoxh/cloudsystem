package de.polocloud.bootstrap.network.ports;

import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.bootstrap.Master;

import java.util.concurrent.ExecutionException;

public class PortService {

    private IGameServerManager gameServerManager;

    public PortService(IGameServerManager gameServerManager) {
        this.gameServerManager = gameServerManager;
    }

    public int getNextStartedPort() throws ExecutionException, InterruptedException {
        int defaultPort = Master.getInstance().getMasterConfig().getProperties().getDefaultProxyStartPort();
        while (isDetectedPort(defaultPort)){
            defaultPort++;
        }
        return defaultPort;
    }

    public boolean isDetectedPort(int port) throws ExecutionException, InterruptedException {
        return gameServerManager.getGameServers().get().stream().anyMatch(it -> it.getPort() == port);
    }


}
