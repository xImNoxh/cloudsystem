package de.polocloud.api.event.impl.player;

import de.polocloud.api.event.base.ICancellable;
import de.polocloud.api.player.ICloudPlayer;

public class CloudPlayerLackMaintenanceEvent extends CloudPlayerEvent implements ICancellable {

    private boolean cancelled;

    public CloudPlayerLackMaintenanceEvent(ICloudPlayer player) {
        super(player);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
