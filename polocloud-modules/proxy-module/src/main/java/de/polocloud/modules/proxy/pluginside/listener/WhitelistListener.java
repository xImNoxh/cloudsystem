package de.polocloud.modules.proxy.pluginside.listener;

import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.player.CloudPlayerLackMaintenanceEvent;
import de.polocloud.modules.proxy.ProxyModule;
import de.polocloud.modules.proxy.api.ProxyConfig;

import java.util.List;

public class WhitelistListener implements IListener {

    @EventHandler
    public void handle(CloudPlayerLackMaintenanceEvent event){

        ProxyConfig proxyConfig = ProxyModule.getProxyModule().getProxyConfig();
        if (proxyConfig == null) {
            return;
        }

        List<String> whiteListedPlayers = proxyConfig.getWhiteListedPlayers();

        if (whiteListedPlayers.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }
}
