package de.polocloud.signs.signs.initializer;

import de.polocloud.api.template.ITemplate;
import de.polocloud.plugin.api.CloudExecutor;
import de.polocloud.signs.SignService;
import de.polocloud.signs.signs.ConfigSignLocation;
import de.polocloud.signs.signs.IGameServerSign;

public class IGameServerSignInitializer {

    public IGameServerSignInitializer() {

        SignService instance = SignService.getInstance();
        for (ConfigSignLocation signLocation : instance.getSignConfig().getLocationConfig().getLocations()) {
            addSign(signLocation);
        }

        CloudExecutor.getInstance().getTemplateService().getLoadedTemplates().thenAccept(key -> {
            for (ITemplate template : key) {
                CloudExecutor.getInstance().getGameServerManager().getGameServersByTemplate(template).thenAccept(it -> it.forEach(gameServer -> {
                    IGameServerSign gameServerSign = instance.getFreeTemplateSign(gameServer);
                    if (gameServerSign != null) {
                        gameServerSign.setGameServer(gameServer);
                        gameServerSign.displayService();
                    }
                }));
            }
        });
    }

    public void addSign(ConfigSignLocation configSignLocation) {
        CloudExecutor.getInstance().getTemplateService().getTemplateByName(configSignLocation.getGroup()).thenAccept(key ->
            SignService.getInstance().getCache().add(new IGameServerSign(configSignLocation, key)));
    }

}
