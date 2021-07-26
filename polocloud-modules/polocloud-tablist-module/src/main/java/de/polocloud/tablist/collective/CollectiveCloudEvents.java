package de.polocloud.tablist.collective;

import de.polocloud.api.event.EventHandler;
import de.polocloud.api.event.player.CloudPlayerJoinNetworkEvent;
import de.polocloud.api.player.ICloudPlayer;

public class CollectiveCloudEvents implements EventHandler<CloudPlayerJoinNetworkEvent> {

    @Override
    public void handleEvent(CloudPlayerJoinNetworkEvent event) {
        ICloudPlayer player = event.getPlayer();
        player.sendTablist("a", "b");
    }
}
