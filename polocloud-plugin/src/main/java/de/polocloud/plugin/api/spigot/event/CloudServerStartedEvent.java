package de.polocloud.plugin.api.spigot.event;

import de.polocloud.api.gameserver.IGameServer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CloudServerStartedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private IGameServer gameServer;

    public CloudServerStartedEvent(IGameServer gameServer) {
        this.gameServer = gameServer;
    }

    public IGameServer getGameServer() {
        return gameServer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
