package de.polocloud.modules.proxy.events;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.server.CloudGameServerMaintenanceUpdateEvent;
import de.polocloud.api.event.impl.server.CloudGameServerStatusChangeEvent;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.modules.proxy.ProxyModule;

public class CollectiveCloudListener implements IListener {

    @EventHandler
    public void handle(CloudGameServerStatusChangeEvent event) {
        if(!(event.getGameServer().getTemplate().getTemplateType().equals(TemplateType.PROXY) && event.getGameServer().getStatus().equals(GameServerStatus.RUNNING))) return;
        ProxyModule.getProxyModule().sendMotd(event.getGameServer());
    }

    @EventHandler
    public void handle(CloudGameServerMaintenanceUpdateEvent event){
        PoloCloudAPI.getInstance().getGameServerManager().getCached(event.getTemplate()).forEach(it -> ProxyModule.getProxyModule().sendMotd(it));
    }




}
