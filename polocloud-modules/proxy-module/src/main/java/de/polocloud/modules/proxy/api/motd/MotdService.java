package de.polocloud.modules.proxy.api.motd;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.template.helper.TemplateType;

import de.polocloud.api.common.IReloadable;
import de.polocloud.modules.proxy.api.ProxyConfig;
import de.polocloud.modules.proxy.ProxyModule;

public class MotdService implements IReloadable {

    /**
     * Updates the motd and stuff for a {@link IGameServer}
     *
     * @param gameServer the server
     */
    public void sendMotd(IGameServer gameServer) {
        ProxyConfig proxyConfig = ProxyModule.getProxyModule().getProxyConfig();

        if (proxyConfig == null) {
            return;
        }
        ProxyMotd proxyMotd;

        if (gameServer.getTemplate().isMaintenance()) {
            proxyMotd = proxyConfig.getMaintenanceMotd();
        } else {
            proxyMotd = proxyConfig.getOnlineMotd();
        }

        if (!proxyMotd.isEnabled()) {
            return;
        }

        gameServer.setServerPing(proxyMotd.getDescription(), gameServer.getMaxPlayers(), gameServer.getOnlinePlayers(), proxyMotd.getVersionTag(), proxyMotd.getPlayerInfo());
        gameServer.update();
    }


    @Override
    public void reload() {
        for (IGameServer gameServer : PoloCloudAPI.getInstance().getGameServerManager().getAllCached(TemplateType.PROXY)) {
            this.sendMotd(gameServer);
        }
    }

}
