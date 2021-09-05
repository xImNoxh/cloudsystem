package de.polocloud.bootstrap.fallback;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.fallback.base.SimpleFallback;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.bootstrap.Master;
import de.polocloud.api.config.master.MasterConfig;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class FallbackSearchService {

    private MasterConfig masterConfig;

    public FallbackSearchService(MasterConfig masterConfig) {
        this.masterConfig = masterConfig;
    }

    public List<IGameServer> searchForTemplate(ICloudPlayer cloudPlayer, boolean hubCommand) {
        for (SimpleFallback fallbackProperty : masterConfig.getProperties().getFallbacks()) {
            if ((cloudPlayer == null && !fallbackProperty.getFallbackPermission().equals(""))) {
                continue;
            }

            if (hubCommand && !fallbackProperty.isForcedJoin() || (cloudPlayer != null && !fallbackProperty.getFallbackPermission().equals("") && !cloudPlayer.hasPermission(fallbackProperty.getFallbackPermission()))) {
                continue;
            }

            ITemplate template = PoloCloudAPI.getInstance().getTemplateManager().getTemplate(fallbackProperty.getTemplateName());

            if (template == null) {
                PoloLogger.print(LogLevel.WARNING, "The template for the fallback server with the template name " + fallbackProperty.getTemplateName() + ":" + fallbackProperty.getPriority() + " wasn't found! Please check your config.json! Skipping...");
                continue;
            }

            if (template.getTemplateType().equals(TemplateType.PROXY)) {
                PoloLogger.print(LogLevel.WARNING, "A fallback server in the config.json was a proxy server. A proxy server is not compatible as a fallback server!");
                continue;
            }
            List<IGameServer> gameServersByTemplate = Master.getInstance().getGameServerManager().getCached(template);

            if(!gameServersByTemplate.isEmpty()){
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
    }

    public IGameServer searchForGameServer(List<IGameServer> gameServers) {
        if (gameServers == null || gameServers.isEmpty()) {
            return null;
        }
        return gameServers.stream().max(Comparator.comparingInt(IGameServer::getOnlinePlayers)).orElse(null);
    }

    public boolean isOnFallback(ICloudPlayer iCloudPlayer) {
        return this.masterConfig.getProperties().getFallbacks().stream().filter(fallbackProperty -> fallbackProperty.getTemplateName().toLowerCase(Locale.ROOT).equals(iCloudPlayer.getMinecraftServer().getTemplate().getName().toLowerCase(Locale.ROOT))).findFirst().orElse(null) != null;
    }
}
