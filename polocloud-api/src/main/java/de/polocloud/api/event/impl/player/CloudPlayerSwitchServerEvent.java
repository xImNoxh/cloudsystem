package de.polocloud.api.event.impl.player;

import de.polocloud.api.event.base.EventData;
import de.polocloud.api.event.base.ICancellable;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.base.SimpleGameServer;
import de.polocloud.api.player.ICloudPlayer;

@EventData(nettyFire = true)
public class CloudPlayerSwitchServerEvent extends CloudPlayerEvent implements ICancellable {

    private SimpleGameServer target;
    private final SimpleGameServer from;

    private boolean cancelled;

    public CloudPlayerSwitchServerEvent(ICloudPlayer player, IGameServer to, IGameServer from) {
        super(player);
        this.target = (SimpleGameServer) to;
        this.from = (SimpleGameServer) from;
    }

    public IGameServer getTarget() {
        return target;
    }

    public void setTarget(IGameServer to) {
        this.target = (SimpleGameServer) to;
        this.cancelled = true;
    }

    public IGameServer getFrom() {
        return from;
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
