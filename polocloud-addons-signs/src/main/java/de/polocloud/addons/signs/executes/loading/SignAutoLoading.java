package de.polocloud.addons.signs.executes.loading;

import de.polocloud.addons.signs.SignService;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.template.ITemplate;
import de.polocloud.plugin.api.CloudExecutor;

import java.util.concurrent.ExecutionException;

public class SignAutoLoading {

    SignService signService;

    public SignAutoLoading(SignService signService, ITemplate template) throws ExecutionException, InterruptedException {
        this.signService = signService;
        System.out.println(template.getName());

        System.out.println("start loading...");

        CloudExecutor.getInstance().getGameServerManager().getGameServersByTemplate(template).thenAccept(key -> {
            System.out.println(key.size());
            for (IGameServer iGameServer : key) {
                signService.getAddSign().execute(iGameServer);
            }
        });



/*
        List<IGameServer> iGameServers = CloudExecutor.getInstance().getGameServerManager().getGameServersByTemplate(template).get();
        for (IGameServer iGameServer : iGameServers) {
            System.out.println("help us polo " + iGameServer.getName());
            signService.getAddSign().execute(iGameServer);
        }

 */
    }
}
