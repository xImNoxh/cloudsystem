package de.polocloud.api.event.impl.player;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.base.EventData;
import de.polocloud.api.event.base.CloudEvent;
import de.polocloud.api.player.ICloudPlayer;
import org.jetbrains.annotations.Nullable;

@EventData(nettyFire = true)
public abstract class CloudPlayerEvent extends CloudEvent {

    private final String player;

    protected CloudPlayerEvent(ICloudPlayer player) {
        this.player = player.getName();
    }

    @Nullable
    public ICloudPlayer getPlayer() {
        return PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(player);
    }
}
