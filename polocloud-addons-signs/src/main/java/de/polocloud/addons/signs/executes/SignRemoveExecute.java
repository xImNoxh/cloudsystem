package de.polocloud.addons.signs.executes;

import de.polocloud.addons.signs.Sign;
import de.polocloud.addons.signs.SignService;
import de.polocloud.api.gameserver.IGameServer;

public class SignRemoveExecute implements SignExecute{

    private SignService signService;

    public SignRemoveExecute(SignService signService) {
        this.signService = signService;
    }

    @Override
    public void execute(IGameServer gameServer) {
        Sign sign = signService.getSignByGameServer(gameServer);

        if(sign == null) return;
        sign.setGameServer(null);




    }
}
