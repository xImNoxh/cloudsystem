package de.polocloud.modules.proxy.cloudside.listener;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.template.TemplateMaintenanceChangeEvent;
import de.polocloud.api.event.impl.server.GameServerStatusChangeEvent;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.modules.proxy.ProxyModule;

public class ServerMotdListener implements IListener {

    @EventHandler
    public void handle(GameServerStatusChangeEvent event) {
        IGameServer gameServer = event.getGameServer();
        GameServerStatus status = event.getStatus();
        if (status == GameServerStatus.AVAILABLE && gameServer.getTemplate().getTemplateType() == TemplateType.PROXY) {
            ProxyModule.getProxyModule().getMotdService().sendMotd(gameServer);
        }

    }

    @EventHandler
    public void handle(TemplateMaintenanceChangeEvent event){
        for (IGameServer gameServer : PoloCloudAPI.getInstance().getGameServerManager().getAllCached(event.getTemplate())) {
            ProxyModule.getProxyModule().getMotdService().sendMotd(gameServer);
        }
    }

}
