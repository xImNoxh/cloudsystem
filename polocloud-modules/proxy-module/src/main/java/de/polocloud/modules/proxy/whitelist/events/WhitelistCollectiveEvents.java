package de.polocloud.modules.proxy.whitelist.events;

import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.player.CloudPlayerLackMaintenanceEvent;
import de.polocloud.modules.proxy.whitelist.WhitelistProxyService;

public class WhitelistCollectiveEvents implements IListener {

    @EventHandler
    public void handle(CloudPlayerLackMaintenanceEvent event){
        if(WhitelistProxyService.getInstance().getWhitelistProperty() != null
            && WhitelistProxyService.getInstance().getWhitelistProperty().getWhitelistPlayers().contains(event.getPlayer().getName()))
            event.setCancelled(true);
    }

}
