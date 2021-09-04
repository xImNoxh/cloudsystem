package de.polocloud.api.event.impl.player;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.base.EventData;
import de.polocloud.api.event.base.CloudEvent;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.SimpleCloudPlayer;

import java.io.IOException;

@EventData(nettyFire = true)
public abstract class CloudPlayerEvent extends CloudEvent {

    private String player;

    protected CloudPlayerEvent(ICloudPlayer player) {
        this.player = player.getName();
    }

    public ICloudPlayer getPlayer() {
        return PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(player);
    }
}
