package de.polocloud.modules.proxy.tablist.events;

import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.player.CloudPlayerDisconnectEvent;
import de.polocloud.api.event.impl.player.CloudPlayerJoinNetworkEvent;
import de.polocloud.api.event.impl.player.CloudPlayerSwitchServerEvent;

public class CloudPlayerTablistEvents implements IListener {

    @EventHandler
    public void handle(CloudPlayerJoinNetworkEvent event){

    }

    @EventHandler
    public void handle(CloudPlayerDisconnectEvent event){

    }

    @EventHandler
    public void handle(CloudPlayerSwitchServerEvent event){

    }

}
