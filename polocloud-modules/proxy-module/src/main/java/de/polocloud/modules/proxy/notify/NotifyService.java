package de.polocloud.modules.proxy.notify;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.modules.proxy.notify.events.CloudPlayerNotifyEvents;
public class NotifyService {

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
        PoloCloudAPI.getInstance().getCloudPlayerManager().getAllCached().forEach(it ->
            it.sendMessage(PoloCloudAPI.getInstance().getMasterConfig().getMessages().getPrefix() + finalMessage));
    }

}
