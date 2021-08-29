package de.polocloud.api.event.impl.player;

import de.polocloud.api.event.base.EventData;
import de.polocloud.api.player.ICloudPlayer;

@EventData(nettyFire = true)
public class CloudPlayerJoinNetworkEvent extends CloudPlayerEvent {

    public CloudPlayerJoinNetworkEvent(ICloudPlayer player) {
        super(player);
    }

}
