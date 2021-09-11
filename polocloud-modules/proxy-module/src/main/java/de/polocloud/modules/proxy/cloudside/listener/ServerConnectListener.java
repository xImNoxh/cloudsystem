package de.polocloud.modules.proxy.cloudside.listener;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.server.CloudGameServerMaintenanceUpdateEvent;
import de.polocloud.api.event.impl.server.CloudGameServerStatusChangeEvent;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.modules.proxy.ProxyModule;
import de.polocloud.modules.proxy.api.ProxyConfig;
import de.polocloud.modules.proxy.api.motd.ProxyMotd;

public class ServerConnectListener implements IListener {

    @EventHandler
    public void handleStart(CloudGameServerStatusChangeEvent event) {
        ProxyModule.getProxyModule().getMessageChannel().sendMessage(ProxyModule.getProxyModule().getProxyConfig());

        IGameServer gameServer = event.getGameServer();
        if (event.getStatus() == GameServerStatus.AVAILABLE && gameServer.getTemplate().getTemplateType() == TemplateType.PROXY) {
            ProxyModule.getProxyModule().updateMotd(gameServer);
        }
    }

    @EventHandler
    public void handle(CloudGameServerMaintenanceUpdateEvent event) {
        ITemplate template = event.getTemplate();

        for (IGameServer server : template.getServers()) {
            ProxyModule.getProxyModule().updateMotd(server);
        }
    }
}
