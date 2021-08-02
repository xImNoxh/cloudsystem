package de.polocloud.bootstrap.template.fallback;

import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.player.ICloudPlayer;
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
                //Checking the permission of the player
                if ((cloudPlayer == null && !fallbackProperty.getFallbackPermission().equals(""))) {
                    continue;
                }

                if (hubCommand && !fallbackProperty.isForcedJoin() || (cloudPlayer != null && !fallbackProperty.getFallbackPermission().equals("") && !cloudPlayer.hasPermissions(fallbackProperty.getFallbackPermission()).get())) {
                    continue;
                }

                //Get all servers of the template
                List<IGameServer> gameServersByTemplate = Master.getInstance().getGameServerManager().getGameServersByTemplate(Master.getInstance().getTemplateService().getTemplateByName(fallbackProperty.getTemplateName()).get()).get();

                //Check if the list with the Servers of the template is empty
                if (gameServersByTemplate.isEmpty()) {
                    continue;
                } else {
                    //Found a template returning
                    gameServersByTemplate = gameServersByTemplate.stream().filter(iGameServer -> iGameServer.getStatus() == GameServerStatus.RUNNING).collect(Collectors.toList());
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

    public IGameServer searchForGameServerWithCurrentServer(List<IGameServer> gameServers, IGameServer currentServer) {
        if (gameServers == null || gameServers.isEmpty()) {
            return null;
        }
        return gameServers.stream().filter(iGameServer -> iGameServer != currentServer).max(Comparator.comparingInt(IGameServer::getOnlinePlayers)).orElse(null);
    }

    public boolean isOnFallback(ICloudPlayer iCloudPlayer) {
        return this.masterConfig.getProperties().getFallbackProperties().stream().filter(fallbackProperty -> fallbackProperty.getTemplateName().toLowerCase(Locale.ROOT).equals(iCloudPlayer.getMinecraftServer().getTemplate().getName().toLowerCase(Locale.ROOT))).findFirst().orElse(null) != null;
    }
}
