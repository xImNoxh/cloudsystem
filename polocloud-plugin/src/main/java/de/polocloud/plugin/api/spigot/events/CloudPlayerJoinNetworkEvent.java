package de.polocloud.plugin.api.spigot.events;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.player.ICloudPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CloudPlayerJoinNetworkEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private String playerName;

    public CloudPlayerJoinNetworkEvent(String playerName) {
        this.playerName = playerName;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public ICloudPlayer getPlayer() {
        return PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(playerName);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
