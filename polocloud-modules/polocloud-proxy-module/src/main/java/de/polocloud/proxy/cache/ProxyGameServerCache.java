package de.polocloud.proxy.cache;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.template.TemplateType;
import de.polocloud.bootstrap.Master;
import de.polocloud.proxy.ProxyModule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ProxyGameServerCache extends ArrayList<IGameServer> {

    public ProxyGameServerCache() {
        init();
    }

    public void init() {
        try {
            List<IGameServer> servers = Master.getInstance().getGameServerManager().getGameServersByType(TemplateType.PROXY).get();
            addAll(servers);
            sendDefaultMotd();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void sendDefaultMotd() {
        for (IGameServer gameServer : this) {
            if (gameServer.getTemplate().isMaintenance()) {
                gameServer.setMotd(ProxyModule.getInstance().getMotdInfoService().getMaintenanceMotd());
                return;
            }
            gameServer.setMotd(ProxyModule.getInstance().getMotdInfoService().getCurrentMotd());
        }
    }

}
