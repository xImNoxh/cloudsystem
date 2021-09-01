package de.polocloud.api.fallback;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.fallback.base.IFallback;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.base.ITemplate;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleCachedFallbackManager implements IFallbackManager {

    private List<IFallback> availableFallbacks;

    public SimpleCachedFallbackManager() {
        this.availableFallbacks = new LinkedList<>();
    }

    @Override
    public boolean isOnFallback(ICloudPlayer cloudPlayer) {
        return this.availableFallbacks.stream().anyMatch(iFallback -> cloudPlayer.getMinecraftServer().getTemplate().getName().equalsIgnoreCase(iFallback.getTemplateName()));
    }

    public List<IFallback> getAvailableFallbacks() {
        return availableFallbacks;
    }


    private List<IFallback> getFallbacks(ICloudPlayer cloudPlayer) {
        List<IFallback> list = new LinkedList<>();
        for (IFallback availableFallback : this.availableFallbacks) {
            if(availableFallback.isForcedJoin()){
                if(availableFallback.getFallbackPermission().trim().isEmpty() || (cloudPlayer != null && cloudPlayer.hasPermission(availableFallback.getFallbackPermission()))){
                    if(!availableFallback.getTemplate().isMaintenance()){
                        list.add(availableFallback);
                    }
                }
            }

            //if (availableFallback.isForcedJoin() || availableFallback.getFallbackPermission() == null || availableFallback.getFallbackPermission().trim().isEmpty() || (cloudPlayer != null && cloudPlayer.hasPermission(availableFallback.getFallbackPermission()))) {
              //  list.add(availableFallback);
            //}
        }
        return list;
    }

    @Override
    public IFallback getHighestFallback(ICloudPlayer cloudPlayer) {

        List<IFallback> availableFallbacks = this.getFallbacks(cloudPlayer);
        if (availableFallbacks.isEmpty()) {
            return null;
        }

        availableFallbacks.sort(Comparator.comparingInt(IFallback::getPriority));
        return availableFallbacks.get(0);
        //return availableFallbacks.get(availableFallbacks.size() - 1);
    }

    @Override
    public IGameServer getFallback(IFallback fallback) {
        if (fallback == null) {
            return null;
        }
        IGameServerManager gameServerManager = PoloCloudAPI.getInstance().getGameServerManager();

        ITemplate iTemplate = PoloCloudAPI.getInstance().getTemplateManager().getTemplate(fallback.getTemplateName());
        List<IGameServer> gameServers = gameServerManager.getCached(iTemplate).stream().filter(gameServer -> gameServer.getStatus().equals(GameServerStatus.RUNNING)).collect(Collectors.toList());

        return gameServers.stream().max(Comparator.comparingInt(IGameServer::getOnlinePlayers)).orElse(null);
    }

    @Override
    public IGameServer getFallback(ICloudPlayer cloudPlayer) {
        IFallback highestFallback = getHighestFallback(cloudPlayer);
        return this.getFallback(highestFallback);
    }

    @Override
    public void registerFallback(IFallback fallback) {
        this.availableFallbacks.add(fallback);
    }

    @Override
    public void setAvailableFallbacks(List<IFallback> fallbacks) {
        this.availableFallbacks = fallbacks;
    }
}
