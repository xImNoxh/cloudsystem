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

        //List which contains possible Fallbacks for the specific player
        List<IFallback> list = new LinkedList<>();

        //Scanning through the local availableFallbacks for fallbacks for the specific player
        //Parameters: the fallback has ForcedJoin activated, the fallback has a permission which the player doesn't have or have
        for (IFallback availableFallback : this.availableFallbacks) {
            if (availableFallback.isForcedJoin() || availableFallback.getFallbackPermission() == null || availableFallback.getFallbackPermission().trim().isEmpty() || (cloudPlayer != null && cloudPlayer.hasPermission(availableFallback.getFallbackPermission()))) {
                list.add(availableFallback);
            }
        }
        return list;
    }

    /**
     * Getting a IFallback with the best priority for the specific player
     * @param cloudPlayer the player
     * @return 's null when no fallback was found or return's an IFallback
     */
    @Override
    public IFallback getHighestFallback(ICloudPlayer cloudPlayer) {
        List<IFallback> availableFallbacks = this.getFallbacks(cloudPlayer);
        availableFallbacks.sort(Comparator.comparingInt(IFallback::getPriority));
        return availableFallbacks.isEmpty() ? null : availableFallbacks.get(availableFallbacks.size() - 1);
    }


    /**
     * Searching the best GameServer from a Fallback (Template)
     * @param fallback the fallback
     * @return 's a Gameserver of an IFallback
     */
    @Override
    public IGameServer getFallback(IFallback fallback) {
        IGameServerManager gameServerManager = PoloCloudAPI.getInstance().getGameServerManager();

        ITemplate iTemplate = PoloUtils.sneakyThrows(() -> PoloCloudAPI.getInstance().getTemplateService().getTemplateByName(fallback.getTemplateName()).get());
        List<IGameServer> gameServers = PoloUtils.sneakyThrows(() -> gameServerManager.getGameServersByTemplate(iTemplate).get());

        return gameServers.stream().max(Comparator.comparingInt(IGameServer::getOnlinePlayers)).orElse(null);
    }

    /**
     * Final method for getting a fallback for a Player
     * @param cloudPlayer the player
     * @return 's a Gameserver of an IFallback
     */
    @Override
    public IGameServer getFallback(ICloudPlayer cloudPlayer) {
        IFallback highestFallback = getHighestFallback(cloudPlayer);
        return this.getFallback(highestFallback);
    }

    public void addFallback(IFallback fallback){
        this.availableFallbacks.add(fallback);
        availableFallbacks.sort(Comparator.comparingInt(IFallback::getPriority));
    }

    public void setAvailableFallbacks(List<IFallback> availableFallbacks) {
        this.availableFallbacks = availableFallbacks;
        availableFallbacks.sort(Comparator.comparingInt(IFallback::getPriority));
    }
}
