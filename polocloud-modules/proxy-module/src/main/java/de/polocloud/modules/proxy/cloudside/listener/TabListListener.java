package de.polocloud.modules.proxy.cloudside.listener;

import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.player.CloudPlayerDisconnectEvent;
import de.polocloud.api.event.impl.player.CloudPlayerJoinNetworkEvent;
import de.polocloud.api.event.impl.player.CloudPlayerSwitchServerEvent;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.modules.proxy.ProxyModule;
import de.polocloud.modules.proxy.api.tablist.TablistService;

public class TabListListener implements IListener {

    @EventHandler
    public void handle(CloudPlayerJoinNetworkEvent event) {
        ICloudPlayer player = event.getPlayer();

        ProxyModule.getProxyModule().getTablistService().updateTabList(player);
        ProxyModule.getProxyModule().getTablistService().updateTabListIfRequired();
    }

    @EventHandler
    public void handle(CloudPlayerDisconnectEvent event) {
        ProxyModule.getProxyModule().getTablistService().updateTabListIfRequired();
    }

    @EventHandler
    public void handle(CloudPlayerSwitchServerEvent event) {
        ProxyModule.getProxyModule().getTablistService().updateTabListIfRequired();
    }

}
