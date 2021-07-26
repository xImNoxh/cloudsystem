package de.polocloud.notify.collectives;

import com.google.common.collect.Maps;
import de.polocloud.api.event.EventHandler;
import de.polocloud.api.event.gameserver.CloudGameServerStatusChangeEvent;
import de.polocloud.bootstrap.Master;
import de.polocloud.notify.NotifyModule;
import de.polocloud.notify.config.NotifyConfig;
import de.polocloud.notify.config.messages.Messages;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class CloudCollectivesListener implements EventHandler<CloudGameServerStatusChangeEvent> {

    private String permission;
    private Map<CloudGameServerStatusChangeEvent.Status, String> messaging;

    public CloudCollectivesListener() {

        NotifyConfig notifyConfig = NotifyModule.getInstance().getNotifyConfig();

        messaging = Maps.newConcurrentMap();

        Messages messages = notifyConfig.getMessages();
        messaging.put(CloudGameServerStatusChangeEvent.Status.STARTING, messages.getStartingMessage().replaceAll("&", "§"));
        messaging.put(CloudGameServerStatusChangeEvent.Status.RUNNING, messages.getRunningMessage().replaceAll("&", "§"));
        messaging.put(CloudGameServerStatusChangeEvent.Status.STOPPING, messages.getStoppedMessage().replaceAll("&", "§"));

        permission = notifyConfig.getPermission();
    }

    @Override
    public void handleEvent(CloudGameServerStatusChangeEvent event) {

        String localMessage = messaging.get(event.getStatus()).replace("%service%", event.getGameServer().getName());

        Master.getInstance().getCloudPlayerManager().getAllOnlinePlayers().thenAccept(key -> key.stream().filter(players -> {
            try {
                return players.hasPermissions(permission).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return false;
        }).collect(Collectors.toList()).forEach(all -> all.sendMessage(localMessage)));
    }
}
