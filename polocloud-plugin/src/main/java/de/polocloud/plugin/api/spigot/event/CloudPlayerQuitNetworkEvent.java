package de.polocloud.plugin.api.spigot.event;

import de.polocloud.api.player.ICloudPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CloudPlayerQuitNetworkEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private ICloudPlayer player;

    public CloudPlayerQuitNetworkEvent(ICloudPlayer player) {
        this.player = player;
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
