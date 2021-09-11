package de.polocloud.modules.proxy.cloudside.listener;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.server.CloudGameServerMaintenanceUpdateEvent;
import de.polocloud.api.event.impl.server.CloudGameServerStatusChangeEvent;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.modules.proxy.ProxyModule;
import de.polocloud.modules.proxy.api.motd.MotdService;

public class ServerMotdListener implements IListener {

    @EventHandler
    public void handle(CloudGameServerStatusChangeEvent event) {
        if (!(event.getGameServer().getTemplate().getTemplateType().equals(TemplateType.PROXY) && event.getStatus().equals(GameServerStatus.AVAILABLE))) {
            return;
        }

        ProxyModule.getProxyModule().getMotdService().sendMotd(event.getGameServer());
    }

    @EventHandler
    public void handle(CloudGameServerMaintenanceUpdateEvent event){
        for (IGameServer gameServer : PoloCloudAPI.getInstance().getGameServerManager().getAllCached(event.getTemplate())) {
            ProxyModule.getProxyModule().getMotdService().sendMotd(gameServer);
        }
    }

}
