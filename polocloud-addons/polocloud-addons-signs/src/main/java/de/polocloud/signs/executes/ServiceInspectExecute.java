package de.polocloud.signs.executes;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.template.ITemplate;
import de.polocloud.plugin.api.CloudExecutor;
import de.polocloud.signs.SignService;
import de.polocloud.signs.signs.IGameServerSign;
import de.polocloud.signs.signs.cache.IGameServerSignCache;
import org.bukkit.block.Sign;

import java.util.concurrent.ExecutionException;

public class ServiceInspectExecute {

    private IGameServerSignCache cache;

    public ServiceInspectExecute() {
        this.cache = SignService.getInstance().getCache();
    }

    public IGameServerSign execute(IGameServer gameServer) {
        return cache.stream().filter(key -> key.getGameServer() != null && sameService(key, gameServer)).findAny().orElse(null);
    }

    public boolean sameService(IGameServerSign sign, IGameServer check) {
        return check.getSnowflake() == sign.getGameServer().getSnowflake();
    }

    public IGameServerSign getGameSignBySign(Sign sign) {
        return cache.stream().filter(s -> s.getLocation().equals(sign.getLocation())).findAny().get();
    }


    public IGameServerSign getFreeTemplateSign(IGameServer gameServer) {
        return cache.stream().filter(key -> key.getGameServer() == null &&
            key.getTemplate().getName().equals(gameServer.getTemplate().getName())).findAny().orElse(null);
    }

    public IGameServer getGameServerWithNoSign(ITemplate template) throws ExecutionException, InterruptedException {
        return CloudExecutor.getInstance().getGameServerManager().getGameServersByTemplate(template).get().stream().
            filter(key -> cache.stream().anyMatch(it -> it.getGameServer() != null && it.getGameServer().getName().equals(key.getName()))).findAny().orElse(null);
    }


}
