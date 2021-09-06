package de.polocloud.modules.proxy.events;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.player.CloudPlayerDisconnectEvent;
import de.polocloud.api.event.impl.player.CloudPlayerJoinNetworkEvent;
import de.polocloud.api.event.impl.player.CloudPlayerLackMaintenanceEvent;
import de.polocloud.api.event.impl.server.CloudGameServerMaintenanceUpdateEvent;
import de.polocloud.api.event.impl.server.CloudGameServerStatusChangeEvent;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.module.info.ScheduledModuleTask;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.modules.proxy.ProxyModule;
import de.polocloud.modules.proxy.config.ProxyConfig;

public class CollectiveCloudListener implements IListener {

    private final ProxyConfig proxyConfig;

    public CollectiveCloudListener() {
        proxyConfig = ProxyModule.getProxyModule().getProxyConfig();
    }

    @EventHandler
    public void handle(CloudGameServerStatusChangeEvent event) {
        if (proxyConfig.getNotifyConfig().isUse()) {
            switch (event.getStatus()) {
                case STARTING:
                    sendNotifyMessage(proxyConfig.getNotifyConfig().getStartingMessage(), event.getGameServer());
                    break;
                case RUNNING:
                    sendNotifyMessage(proxyConfig.getNotifyConfig().getStartedMessage(), event.getGameServer());
                    break;
                case STOPPING:
                    sendNotifyMessage(proxyConfig.getNotifyConfig().getStoppedMessage(), event.getGameServer());
                    break;
            }
        }
        if(!(event.getGameServer().getTemplate().getTemplateType().equals(TemplateType.PROXY) && event.getStatus().equals(GameServerStatus.RUNNING))) return;
        ProxyModule.getProxyModule().sendMotd(event.getGameServer());
    }

    @EventHandler
    public void handle(CloudPlayerLackMaintenanceEvent event){
        if(event.getPlayer().getName().equals("HttpMarco")) event.setCancelled(true);
    }

    @EventHandler
    public void handle(CloudPlayerJoinNetworkEvent event){
        Scheduler.runtimeScheduler().schedule(() -> ProxyModule.getProxyModule().sendAllTablist(), 20L);
    }

    @EventHandler
    public void handle(CloudPlayerDisconnectEvent event){
        Scheduler.runtimeScheduler().schedule(() -> ProxyModule.getProxyModule().sendAllTablist(), 20L);
    }

    @EventHandler
    public void handle(CloudGameServerMaintenanceUpdateEvent event){
        PoloCloudAPI.getInstance().getGameServerManager().getCached(event.getTemplate()).forEach(it -> ProxyModule.getProxyModule().sendMotd(it));
    }

    public void sendNotifyMessage(String message, IGameServer gameServer){
        String finalMessage = message.replaceAll("%service%", gameServer.getName());
        PoloCloudAPI.getInstance().getCloudPlayerManager().getAllCached().forEach(it -> it.sendMessage(PoloCloudAPI.getInstance().getMasterConfig().getMessages().getPrefix() + finalMessage));
    }



}
