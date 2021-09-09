package de.polocloud.modules.proxy.motd.events;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.server.CloudGameServerMaintenanceUpdateEvent;
import de.polocloud.api.event.impl.server.CloudGameServerStatusChangeEvent;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.modules.proxy.motd.MotdService;

public class ServiceMotdEvents implements IListener {

    @EventHandler
    public void handle(CloudGameServerStatusChangeEvent event) {
        if(!(event.getGameServer().getTemplate().getTemplateType().equals(TemplateType.PROXY) && event.getStatus().equals(GameServerStatus.AVAILABLE))) return;

        MotdService.getInstance().sendMotd(event.getGameServer());
        MotdService.getInstance().updateVersionTag();
    }

    @EventHandler
    public void handle(CloudGameServerMaintenanceUpdateEvent event){
        PoloCloudAPI.getInstance().getGameServerManager().getAllCached(event.getTemplate()).forEach(it -> MotdService.getInstance().sendMotd(it));
    }

}
