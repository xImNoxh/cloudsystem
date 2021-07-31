package de.polocloud.signs.executes;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.signs.signs.IGameServerSign;

public class ServiceUpdateExecute {

    public void update(IGameServerSign sign, IGameServer gameServer){
        if(sign == null) return;
        sign.setGameServer(gameServer);
        sign.writeSign(false);
    }

}
