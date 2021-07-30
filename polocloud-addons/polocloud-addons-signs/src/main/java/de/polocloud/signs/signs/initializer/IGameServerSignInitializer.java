package de.polocloud.signs.signs.initializer;

import de.polocloud.api.template.ITemplate;
import de.polocloud.plugin.api.CloudExecutor;
import de.polocloud.signs.SignService;
import de.polocloud.signs.signs.ConfigSignLocation;
import de.polocloud.signs.signs.IGameServerSign;

import java.util.concurrent.ExecutionException;

public class IGameServerSignInitializer {

    public IGameServerSignInitializer() {
        try {

            SignService instance = SignService.getInstance();
            for (ConfigSignLocation signLocation : instance.getSignConfig().getLocationConfig().getLocations()) {
                addSign(signLocation);
            }

            CloudExecutor.getInstance().getTemplateService().getLoadedTemplates().thenAccept(key -> {
                for (ITemplate template : key) {
                    CloudExecutor.getInstance().getGameServerManager().getGameServersByTemplate(template).thenAccept(it -> it.forEach(gameServer -> {
                        IGameServerSign gameServerSign = SignService.getInstance().getExecuteService().getServiceInspectExecute().getFreeTemplateSign(gameServer);
                        if (gameServerSign != null) {
                            gameServerSign.setGameServer(gameServer);
                            gameServerSign.writeSign(false);
                        }
                    }));
                }
            });
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void addSign(ConfigSignLocation configSignLocation) throws ExecutionException, InterruptedException {

        CloudExecutor.getInstance().getTemplateService().getTemplateByName(configSignLocation.getGroup()).thenAccept(key -> SignService.getInstance().getCache().add(new IGameServerSign(configSignLocation,key)));
    }

}
