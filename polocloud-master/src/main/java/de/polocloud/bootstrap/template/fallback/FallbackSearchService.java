package de.polocloud.bootstrap.template.fallback;

import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.TemplateType;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FallbackSearchService {

    private MasterConfig masterConfig;

    public FallbackSearchService(MasterConfig masterConfig) {
        this.masterConfig = masterConfig;
    }

    public List<IGameServer> searchForTemplate(ICloudPlayer cloudPlayer, boolean hubCommand) {
        try {
            for (FallbackProperty fallbackProperty : masterConfig.getProperties().getFallbackProperties()) {
                if ((cloudPlayer == null && !fallbackProperty.getFallbackPermission().equals(""))) {
                    continue;
                }
                
                if (hubCommand && !fallbackProperty.isForcedJoin() || (cloudPlayer != null && !fallbackProperty.getFallbackPermission().equals("") && !cloudPlayer.hasPermissions(fallbackProperty.getFallbackPermission()).get())) {
                    continue;
                }

                ITemplate template = Master.getInstance().getTemplateService().getTemplateByName(fallbackProperty.getTemplateName()).get();

                if (template == null) {
                    Logger.log(LoggerType.WARNING, "The template for the fallback server with the template name " + fallbackProperty.getTemplateName() + ":" + fallbackProperty.getPriority() + " wasn't found! Please check your config.json! Skipping...");
                    continue;
                }

                if (template.getTemplateType().equals(TemplateType.PROXY)) {
                    Logger.log(LoggerType.WARNING, "A fallback server in the config.json was a proxy server. A proxy server is not compatible as a fallback server!");
                    continue;
                }
                List<IGameServer> gameServersByTemplate = Master.getInstance().getGameServerManager().getGameServersByTemplate(template).get();

                if (gameServersByTemplate.isEmpty()) {
                    continue;
                } else {
                    gameServersByTemplate = gameServersByTemplate.stream().filter(iGameServer -> iGameServer.getStatus() == GameServerStatus.RUNNING).collect(Collectors.toList());
                    if (cloudPlayer != null) {
                        gameServersByTemplate = gameServersByTemplate.stream().filter(iGameServer -> iGameServer != cloudPlayer.getMinecraftServer()).collect(Collectors.toList());
                    }
                    if (gameServersByTemplate.isEmpty()) {
                        continue;
                    }
                    return gameServersByTemplate;
                }
            }
            return null;
        } catch (ExecutionException | InterruptedException exception) {
            exception.printStackTrace();
            Logger.log(LoggerType.ERROR, "Unexpected error occurred while searching for a fallback!\n" +
                "Please report this error.");
            return null;
        }
    }

    public IGameServer searchForGameServer(List<IGameServer> gameServers) {
        if (gameServers == null || gameServers.isEmpty()) {
            return null;
        }
        return gameServers.stream().max(Comparator.comparingInt(IGameServer::getOnlinePlayers)).orElse(null);
    }

    public boolean isOnFallback(ICloudPlayer iCloudPlayer) {
        return this.masterConfig.getProperties().getFallbackProperties().stream().filter(fallbackProperty -> fallbackProperty.getTemplateName().toLowerCase(Locale.ROOT).equals(iCloudPlayer.getMinecraftServer().getTemplate().getName().toLowerCase(Locale.ROOT))).findFirst().orElse(null) != null;
    }
}
