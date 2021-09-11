package de.polocloud.modules.proxy.api.motd;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.template.helper.TemplateType;

import de.polocloud.api.common.IReloadable;
import de.polocloud.modules.proxy.api.ProxyConfig;
import de.polocloud.modules.proxy.ProxyModule;

public class MotdService implements IReloadable {

    /**
     * Sends the {@link ProxyMotd} to a given {@link IGameServer}
     *
     * @param server the server to send it to
     */
    public void sendMotd(IGameServer server){
        ProxyConfig proxyConfig = ProxyModule.getProxyModule().getProxyConfig();

      //  server.setMotd(proxyConfig.getMotdBaseOnServer(server).getDescription());
    }

    @Override
    public void reload() {
        for (IGameServer gameServer : PoloCloudAPI.getInstance().getGameServerManager().getAllCached(TemplateType.PROXY)) {
            this.sendMotd(gameServer);
        }
    }

}
