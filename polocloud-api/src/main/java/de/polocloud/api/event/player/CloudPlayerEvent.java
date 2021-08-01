package de.polocloud.api.event.player;

import de.polocloud.api.event.CloudEvent;
import de.polocloud.api.player.ICloudPlayer;

public class CloudPlayerEvent implements CloudEvent {

    private ICloudPlayer player;

    public CloudPlayerEvent(ICloudPlayer player) {
        this.player = player;
    }

    public ICloudPlayer getPlayer() {
        return player;
    }
}
