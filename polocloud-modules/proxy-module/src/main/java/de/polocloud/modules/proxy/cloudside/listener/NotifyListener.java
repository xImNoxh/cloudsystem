package de.polocloud.modules.proxy.cloudside.listener;

import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.server.CloudGameServerStatusChangeEvent;
import de.polocloud.modules.proxy.api.ProxyConfig;
import de.polocloud.modules.proxy.ProxyModule;
import de.polocloud.modules.proxy.api.notify.NotifyService;

public class NotifyListener implements IListener {

    @EventHandler
    public void handle(CloudGameServerStatusChangeEvent event) {

        ProxyConfig proxyConfig = ProxyModule.getProxyModule().getProxyConfig();
        NotifyService notifyService = ProxyModule.getProxyModule().getNotifyService();

        if (proxyConfig == null || notifyService == null) {
            return;
        }

        if (proxyConfig.getNotifyConfig().isEnabled()) {
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
