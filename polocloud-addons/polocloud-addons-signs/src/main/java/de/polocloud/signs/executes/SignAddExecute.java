package de.polocloud.signs.executes;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.signs.CloudSign;
import de.polocloud.signs.SignService;

public class SignAddExecute implements SignExecute {


    private SignService signService;

    public SignAddExecute(SignService signService) {
        this.signService = signService;
    }

    @Override
    public void execute(IGameServer gameServer) {
        CloudSign sign = signService.getNextFreeSignByTemplate(gameServer.getTemplate());
        if (sign == null) return;

        sign.setGameServer(gameServer);
        sign.setSign();
    }
}
