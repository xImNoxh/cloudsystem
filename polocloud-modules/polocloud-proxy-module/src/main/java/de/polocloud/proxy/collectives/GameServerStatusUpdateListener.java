package de.polocloud.proxy.collectives;

import de.polocloud.api.event.EventHandler;
import de.polocloud.api.event.gameserver.CloudGameServerStatusChangeEvent;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.template.TemplateType;
import de.polocloud.proxy.ProxyModule;
import de.polocloud.proxy.cache.ProxyGameServerCache;

public class GameServerStatusUpdateListener implements EventHandler<CloudGameServerStatusChangeEvent> {

    @Override
    public void handleEvent(CloudGameServerStatusChangeEvent event) {

        System.out.println("Handling event wooho");
        IGameServer gameServer = event.getGameServer();
        if (gameServer.getTemplate().getTemplateType().equals(TemplateType.MINECRAFT)) return;

        ProxyGameServerCache cache = ProxyModule.getInstance().getCache();

        if (event.getStatus() == CloudGameServerStatusChangeEvent.Status.RUNNING) {
            if (!sameService(gameServer, cache)) cache.add(gameServer);

            if (event.getGameServer().getTemplate().isMaintenance()) {
                gameServer.setMotd(ProxyModule.getInstance().getMotdInfoService().getMaintenanceMotd());
                return;
            }
            gameServer.setMotd(ProxyModule.getInstance().getMotdInfoService().getCurrentMotd());
            return;
        }

        if (event.getStatus() == CloudGameServerStatusChangeEvent.Status.STOPPING) {
            if (sameService(gameServer, cache)) cache.remove(gameServer);
            return;
        }
    }

    public boolean sameService(IGameServer gameServer, ProxyGameServerCache cache) {
        return cache.stream().anyMatch(gameServer1 -> gameServer1.getSnowflake() == gameServer.getSnowflake());
    }

}
