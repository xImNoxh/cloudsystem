package de.polocloud.notify.collectives;

import com.google.common.collect.Maps;
import de.polocloud.api.event.EventHandler;
import de.polocloud.api.event.gameserver.CloudGameServerStatusChangeEvent;
import de.polocloud.bootstrap.Master;
import de.polocloud.notify.NotifyModule;
import de.polocloud.notify.config.NotifyConfig;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class CloudCollectivesListener implements EventHandler<CloudGameServerStatusChangeEvent> {

    private String permission;
    private Map<CloudGameServerStatusChangeEvent.Status, String> messaging;

    public CloudCollectivesListener() {

        NotifyConfig notifyConfig = NotifyModule.getInstance().getNotifyConfig();

        messaging = Maps.newConcurrentMap();

        messaging.put(CloudGameServerStatusChangeEvent.Status.STARTING, notifyConfig.getStartingMessage());
        messaging.put(CloudGameServerStatusChangeEvent.Status.RUNNING, notifyConfig.getRunningMessage());
        messaging.put(CloudGameServerStatusChangeEvent.Status.STOPPING, notifyConfig.getStoppedMessage());

        permission = notifyConfig.getPermission();
    }

    @Override
    public void handleEvent(CloudGameServerStatusChangeEvent event) {

        Master.getInstance().getCloudPlayerManager().getAllOnlinePlayers().thenAccept(key -> {
           key.stream().filter(players -> {
               try {
                   return players.hasPermissions(permission).get();
               } catch (InterruptedException | ExecutionException e) {
                   e.printStackTrace();
               }
               return false;

           }).collect(Collectors.toList()).forEach(all -> {
               all.sendMessage(messaging.get(event.getStatus()));
           });
        });
    }
}
