package de.polocloud.api.event.impl.player;

import de.polocloud.api.event.base.EventData;
import de.polocloud.api.player.ICloudPlayer;

@EventData(nettyFire = true)
public class CloudPlayerDisconnectEvent extends CloudPlayerEvent {

    public CloudPlayerDisconnectEvent(ICloudPlayer player) {
        super(player);
    }

}
