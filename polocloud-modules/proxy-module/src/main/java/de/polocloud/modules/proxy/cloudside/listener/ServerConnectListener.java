package de.polocloud.modules.proxy.cloudside.listener;

import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.template.TemplateMaintenanceChangeEvent;
import de.polocloud.api.event.impl.server.GameServerStatusChangeEvent;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.modules.proxy.ProxyModule;

public class ServerConnectListener implements IListener {

    @EventHandler
    public void handleStart(GameServerStatusChangeEvent event) {
        ProxyModule.getProxyModule().getMessageChannel().sendMessage(ProxyModule.getProxyModule().getProxyConfig());

        IGameServer gameServer = event.getGameServer();
        if ((event.getStatus() == GameServerStatus.AVAILABLE || event.getStatus() == GameServerStatus.STARTING) && gameServer.getTemplate().getTemplateType() == TemplateType.PROXY) {
            ProxyModule.getProxyModule().updateMotd(gameServer);
        }
    }

    @EventHandler
    public void handle(TemplateMaintenanceChangeEvent event) {
        ITemplate template = event.getTemplate();

        for (IGameServer server : template.getServers()) {
            ProxyModule.getProxyModule().updateMotd(server);
        }
    }
}
