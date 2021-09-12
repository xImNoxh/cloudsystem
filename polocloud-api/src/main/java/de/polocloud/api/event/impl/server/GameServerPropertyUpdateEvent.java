package de.polocloud.api.event.impl.server;

import de.polocloud.api.event.base.EventData;
import de.polocloud.api.event.base.ICancellable;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.property.IProperty;
import de.polocloud.api.property.def.SimpleProperty;

@EventData(nettyFire = true)
public class GameServerPropertyUpdateEvent extends GameServerEvent implements ICancellable {

    private SimpleProperty property;
    private boolean cancelled;

    public GameServerPropertyUpdateEvent(IGameServer gameServer, SimpleProperty property) {
        super(gameServer);
        this.property = property;
    }

    public void setProperty(IProperty property) {
        this.property = (SimpleProperty) property;
    }

    public IProperty getProperty() {
        return property;
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
