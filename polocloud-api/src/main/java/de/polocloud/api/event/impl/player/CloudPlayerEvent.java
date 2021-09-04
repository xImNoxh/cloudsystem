package de.polocloud.api.event.impl.player;

import de.polocloud.api.event.base.EventData;
import de.polocloud.api.event.base.CloudEvent;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.SimpleCloudPlayer;

import java.io.IOException;

@EventData(nettyFire = true)
public abstract class CloudPlayerEvent extends CloudEvent {

    private SimpleCloudPlayer player;

    protected CloudPlayerEvent(ICloudPlayer player) {
        this.player = (SimpleCloudPlayer) player;
    }

    public ICloudPlayer getPlayer() {
        return player;
    }
}
