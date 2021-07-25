package de.polocloud.api.event.player;

import de.polocloud.api.event.CloudEvent;
import de.polocloud.api.player.ICloudPlayer;

import java.util.UUID;

public class CloudPlayerJoinNetworkEvent extends CloudPlayerEvent {


    public CloudPlayerJoinNetworkEvent(ICloudPlayer player) {
        super(player);
    }
}
