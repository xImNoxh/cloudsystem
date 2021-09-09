package de.polocloud.modules.proxy.notify;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.modules.proxy.IProxyReload;
import de.polocloud.modules.proxy.notify.events.CloudPlayerNotifyEvents;

import java.util.concurrent.CopyOnWriteArrayList;

public class NotifyService implements IProxyReload {

    private static NotifyService instance;

    public NotifyService() {
        instance = this;
        PoloCloudAPI.getInstance().getEventManager().registerListener(new CloudPlayerNotifyEvents(this));
    }

    public static NotifyService getInstance() {
        return instance;
    }

    public void sendNotifyMessage(String message, IGameServer gameServer){
        String finalMessage = message.replaceAll("%service%", gameServer.getName());

        for (ICloudPlayer cloudPlayer : new CopyOnWriteArrayList<>(PoloCloudAPI.getInstance().getCloudPlayerManager().getAllCached())) {
            cloudPlayer.sendMessage(PoloCloudAPI.getInstance().getMasterConfig().getMessages().getPrefix() + finalMessage);
        }

    }

    @Override
    public void onReload() {

    }
}
