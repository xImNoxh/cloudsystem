package de.polocloud.api.fallback;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.fallback.base.IFallback;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.util.PoloUtils;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class SimpleCachedFallbackManager implements IFallbackManager {

    private final List<IFallback> availableFallbacks;

    public SimpleCachedFallbackManager() {
        this.availableFallbacks = new LinkedList<>();
    }

    @Override
    public boolean isFallback(ICloudPlayer cloudPlayer) {
        return this.availableFallbacks.stream().anyMatch(iFallback -> cloudPlayer.getMinecraftServer().getTemplate().getName().equalsIgnoreCase(iFallback.getTemplateName()));
    }

    public List<IFallback> getAvailableFallbacks() {
        return availableFallbacks;
    }


    private List<IFallback> getFallbacks(ICloudPlayer cloudPlayer) {
        List<IFallback> list = new LinkedList<>();
        for (IFallback availableFallback : this.availableFallbacks) {
            if (availableFallback.isForcedJoin() || availableFallback.getFallbackPermission() == null || availableFallback.getFallbackPermission().trim().isEmpty() || (cloudPlayer != null && cloudPlayer.hasPermission(availableFallback.getFallbackPermission()))) {
                list.add(availableFallback);
            }
        }
        return list;
    }

    @Override
    public IFallback getHighestFallback(ICloudPlayer cloudPlayer) {

        List<IFallback> availableFallbacks = this.getFallbacks(cloudPlayer);
        availableFallbacks.sort(Comparator.comparingInt(IFallback::getPriority));

        return availableFallbacks.get(availableFallbacks.size() - 1);
    }

    @Override
    public IGameServer getFallback(IFallback fallback) {
        IGameServerManager gameServerManager = PoloCloudAPI.getInstance().getGameServerManager();

        ITemplate iTemplate = PoloUtils.sneakyThrows(() -> PoloCloudAPI.getInstance().getTemplateService().getTemplateByName(fallback.getTemplateName()).get());
        List<IGameServer> gameServers = PoloUtils.sneakyThrows(() -> gameServerManager.getGameServersByTemplate(iTemplate).get());

        return gameServers.stream().max(Comparator.comparingInt(IGameServer::getOnlinePlayers)).orElse(null);
    }

    @Override
    public IGameServer getFallback(ICloudPlayer cloudPlayer) {
        IFallback highestFallback = getHighestFallback(cloudPlayer);
        return this.getFallback(highestFallback);
    }
}
