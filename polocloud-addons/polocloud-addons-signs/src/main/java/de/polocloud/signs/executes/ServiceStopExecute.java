package de.polocloud.signs.executes;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.signs.signs.IGameServerSign;

public class ServiceStopExecute {

    private ServiceInspectExecute execute;

    public ServiceStopExecute(ServiceInspectExecute execute) {
        this.execute = execute;
    }

    public void update(IGameServer gameServer){
        IGameServerSign gameServerSign = execute.execute(gameServer);
        if (gameServerSign == null) return;
        gameServerSign.setGameServer(null);
        gameServerSign.writeSign();
    }

}
