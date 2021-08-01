package de.polocloud.signs.signs.initializer;

import de.polocloud.api.template.ITemplate;
import de.polocloud.plugin.api.CloudExecutor;
import de.polocloud.signs.SignService;
import de.polocloud.signs.signs.ConfigSignLocation;
import de.polocloud.signs.signs.IGameServerSign;
import org.bukkit.Bukkit;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

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
        } catch (InterruptedException | ExecutionException exception) {
            exception.printStackTrace();
            Bukkit.getLogger().log(Level.SEVERE, "Unexpected error occurred while initializing GameServerSigns!\n" +
                "Please report this error.");
        }
    }

    public void addSign(ConfigSignLocation configSignLocation) throws ExecutionException, InterruptedException {
        CloudExecutor.getInstance().getTemplateService().getTemplateByName(configSignLocation.getGroup()).thenAccept(key -> SignService.getInstance().getCache().add(new IGameServerSign(configSignLocation, key)));
    }

}
