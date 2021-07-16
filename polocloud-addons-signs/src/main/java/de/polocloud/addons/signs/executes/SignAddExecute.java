package de.polocloud.addons.signs.executes;

import de.polocloud.addons.signs.CloudSign;
import de.polocloud.addons.signs.SignService;
import de.polocloud.api.gameserver.IGameServer;

public class SignAddExecute implements SignExecute {


    private SignService signService;

    public SignAddExecute(SignService signService) {
        this.signService = signService;
    }

    @Override
    public void execute(IGameServer gameServer) {
        CloudSign sign = signService.getNextFreeSignByTemplate(gameServer.getTemplate());

        if(sign == null) return;

        sign.setGameServer(gameServer);
        sign.getSign().setLine(0, gameServer.getName());
        sign.getSign().update();
    }
}
