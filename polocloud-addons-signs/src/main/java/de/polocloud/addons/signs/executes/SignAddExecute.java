package de.polocloud.addons.signs.executes;

import de.polocloud.addons.signs.Sign;
import de.polocloud.addons.signs.SignService;
import de.polocloud.api.gameserver.IGameServer;

public class SignAddExecute implements SignExecute {


    private SignService signService;

    public SignAddExecute(SignService signService) {
        this.signService = signService;
    }

    @Override
    public void execute(IGameServer gameServer) {
        Sign sign = signService.getNextFreeSignByTemplate(gameServer.getTemplate());

        if(sign == null) return;

        sign.setGameServer(gameServer);
    }
}
