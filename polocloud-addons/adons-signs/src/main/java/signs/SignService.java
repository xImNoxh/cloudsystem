package signs;

import de.polocloud.api.event.EventHandler;
import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.event.channel.ChannelActiveEvent;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.template.ITemplate;
import de.polocloud.plugin.api.CloudExecutor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import signs.cache.SignCache;
import signs.collectives.CollectiveSignEvents;
import signs.executes.SignAddExecute;
import signs.executes.SignExecute;
import signs.executes.SignRemoveExecute;
import signs.executes.loading.SignAutoLoading;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class SignService {

    private static SignService instance;
    private SignCache cache;

    private SignExecute addSign;
    private SignExecute removeSign;

    public SignService() {

        instance = this;
        this.cache = new SignCache();

        this.removeSign = new SignRemoveExecute(this);
        this.addSign = new SignAddExecute(this);

        EventRegistry.registerListener((EventHandler<ChannelActiveEvent>) event -> CloudExecutor.getInstance().getGameServerManager()
            .getGameServerByName("Lobby-1").thenAccept(gameServer -> {
                Location location = new Location(Bukkit.getWorld("world"), -1269, 5, -390);
                for (int i = 0; i < 3; i++) {
                    addSign(gameServer.getTemplate(), location.clone().subtract(i, 0, 0));
                }
                try {
                    new SignAutoLoading(this, gameServer.getTemplate());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }), ChannelActiveEvent.class);
        new CollectiveSignEvents();
    }

    public void addSign(ITemplate template, Location location) {
        cache.add(new CloudSign(template, location));
    }

    public static SignService getInstance() {
        return instance;
    }

    public SignCache getCache() {
        return cache;
    }

    public List<CloudSign> getSignsByTemplate(ITemplate template) {
        return cache.stream().filter(key -> key.getTemplate().getName().equals(template.getName())).collect(Collectors.toList());
    }

    public CloudSign getCloudSignBySign(Sign sign) {
        return cache.stream().filter(key -> key.getSign().equals(sign)).findAny().orElse(null);
    }

    public CloudSign getSignByGameServer(IGameServer gameServer) {
        return cache.stream().filter(key -> key.getGameServer().equals(gameServer)).findAny().orElse(null);
    }

    public CloudSign getNextFreeSignByTemplate(ITemplate template) {
        return getSignsByTemplate(template).stream().filter(key -> key.getGameServer() == null).findAny().orElse(null);
    }

    public SignExecute getAddSign() {
        return addSign;
    }

    public SignExecute
    getRemoveSign() {
        return removeSign;
    }
}
