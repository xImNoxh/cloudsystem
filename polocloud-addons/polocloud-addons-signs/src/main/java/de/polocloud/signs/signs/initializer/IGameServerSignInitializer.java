package de.polocloud.signs.signs.initializer;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.template.ITemplate;
import de.polocloud.plugin.api.CloudExecutor;
import de.polocloud.signs.SignService;
import de.polocloud.signs.signs.ConfigSignLocation;
import de.polocloud.signs.signs.IGameServerSign;

import java.util.concurrent.ExecutionException;

public class IGameServerSignInitializer {

    public IGameServerSignInitializer() throws ExecutionException, InterruptedException {

        SignService instance = SignService.getInstance();
        for (ConfigSignLocation signLocation : instance.getSignConfig().getLocationConfig().getLocations()) {
            addSign(signLocation);
        }

        for (ITemplate template : CloudExecutor.getInstance().getTemplateService().getLoadedTemplates().get()) {
            CloudExecutor.getInstance().getGameServerManager().getGameServersByTemplate(template).get().forEach(it -> {
                IGameServerSign gameServerSign = instance.getFreeTemplateSign(it);
                if(gameServerSign != null){
                    gameServerSign.setGameServer(it);
                    gameServerSign.displayService();
                }
            });
        }
    }

    public void addSign(ConfigSignLocation configSignLocation) throws ExecutionException, InterruptedException {
        SignService.getInstance().getCache().add(new IGameServerSign(configSignLocation, CloudExecutor.getInstance().getTemplateService().getTemplateByName(configSignLocation.getGroup()).get()));
    }

}
