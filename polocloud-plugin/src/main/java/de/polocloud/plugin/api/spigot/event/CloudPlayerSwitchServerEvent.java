package de.polocloud.plugin.api.spigot.event;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.player.ICloudPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CloudPlayerSwitchServerEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private ICloudPlayer player;
    private IGameServer to;
    private IGameServer from;

    public CloudPlayerSwitchServerEvent(ICloudPlayer player, IGameServer to, IGameServer from) {
        this.player = player;
        this.to = to;
        this.from = from;
    }

    public IGameServer getFrom() {
        return from;
    }

    public IGameServer getTo() {
        return to;
    }

    public ICloudPlayer getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
