package de.polocloud.addons.signs.executes.loading;

import de.polocloud.addons.signs.SignService;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.template.ITemplate;
import de.polocloud.plugin.api.CloudExecutor;

public class SignAutoLoading {

    SignService signService;

    public SignAutoLoading(SignService signService, ITemplate template) {
        this.signService = signService;
        for (IGameServer iGameServer : CloudExecutor.getInstance().getGameServerManager().getGameServersByTemplate(template).join()) {
            signService.getAddSign().execute(iGameServer);
        }
    }
}
