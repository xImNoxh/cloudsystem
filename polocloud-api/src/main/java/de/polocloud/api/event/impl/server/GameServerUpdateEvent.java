package de.polocloud.api.event.impl.server;

import de.polocloud.api.event.base.EventData;
import de.polocloud.api.event.base.ICancellable;
import de.polocloud.api.gameserver.base.IGameServer;

@EventData(nettyFire = true)
public class GameServerUpdateEvent extends GameServerEvent implements ICancellable {

    private boolean cancelled;

    public GameServerUpdateEvent(IGameServer gameServer) {
        super(gameServer);
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
