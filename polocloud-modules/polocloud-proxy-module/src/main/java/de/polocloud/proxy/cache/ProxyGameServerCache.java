package de.polocloud.proxy.cache;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.template.TemplateType;
import de.polocloud.bootstrap.Master;

import java.util.ArrayList;

public class ProxyGameServerCache extends ArrayList<IGameServer> {

    public ProxyGameServerCache() {
        init();
    }

    public void init(){
        Master.getInstance().getGameServerManager().getGameServersByType(TemplateType.PROXY).thenAccept(key -> addAll(key));
    }

}
