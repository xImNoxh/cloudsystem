package de.polocloud.modules.proxy.cloudside.listener;

import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.player.CloudPlayerDisconnectEvent;
import de.polocloud.api.event.impl.player.CloudPlayerJoinNetworkEvent;
import de.polocloud.api.event.impl.player.CloudPlayerSwitchServerEvent;
import de.polocloud.modules.proxy.ProxyModule;

public class TabListListener implements IListener {

    @EventHandler
    public void handle(CloudPlayerJoinNetworkEvent event) {
        ProxyModule.getProxyModule().getTablistService().updateTabList();
    }

    @EventHandler
    public void handle(CloudPlayerDisconnectEvent event) {
        ProxyModule.getProxyModule().getTablistService().updateTabList();
    }

    @EventHandler
    public void handle(CloudPlayerSwitchServerEvent event) {
        ProxyModule.getProxyModule().getTablistService().updateTabList();
    }

}
