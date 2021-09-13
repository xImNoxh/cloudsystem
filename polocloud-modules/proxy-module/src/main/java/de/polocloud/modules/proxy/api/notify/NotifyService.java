package de.polocloud.modules.proxy.api.notify;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.modules.proxy.ProxyModule;

public class NotifyService  {

    /**
     * Sends a notify message to all players with a given permission
     *
     * @param message the message
     * @param gameServer the gameserver
     */
    public void sendNotifyMessage(String message, IGameServer gameServer){
        String finalMessage = message.replaceAll("%service%", gameServer.getName());

        for (ICloudPlayer cloudPlayer : PoloCloudAPI.getInstance().getCloudPlayerManager().getAllCached()) {
            if (cloudPlayer.hasPermission(ProxyModule.getProxyModule().getProxyConfig().getNotifyConfig().getPermission())) {
                if (ProxyModule.getProxyModule().getProxyConfig().getNotifyConfig().getDisabledMessages().contains(cloudPlayer.getUUID())) {
                    continue;
                }
                cloudPlayer.sendMessage(PoloCloudAPI.getInstance().getMasterConfig().getMessages().getPrefix() + finalMessage);
            }
        }

    }

}
