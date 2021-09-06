package de.polocloud.modules.proxy.notify.events;

import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.server.CloudGameServerStatusChangeEvent;
import de.polocloud.modules.proxy.ProxyConfig;
import de.polocloud.modules.proxy.ProxyModule;
import de.polocloud.modules.proxy.notify.NotifyService;

public class CloudPlayerNotifyEvents implements IListener {

    private ProxyConfig proxyConfig;
    private NotifyService notifyService;

    public CloudPlayerNotifyEvents(NotifyService notifyService) {
        this.proxyConfig = ProxyModule.getProxyModule().getProxyConfig();
        this.notifyService = notifyService;
    }

    @EventHandler
    public void handle(CloudGameServerStatusChangeEvent event) {
        if (proxyConfig.getNotifyConfig().isUse()) {
            switch (event.getStatus()) {
                case STARTING:
                    notifyService.sendNotifyMessage(proxyConfig.getNotifyConfig().getStartingMessage(), event.getGameServer());
                    break;
                case AVAILABLE:
                    notifyService.sendNotifyMessage(proxyConfig.getNotifyConfig().getStartedMessage(), event.getGameServer());
                    break;
                case STOPPING:
                    notifyService.sendNotifyMessage(proxyConfig.getNotifyConfig().getStoppedMessage(), event.getGameServer());
                    break;
            }
        }
    }
}
