package de.polocloud.plugin.api.spigot.events;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.player.ICloudPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.concurrent.CompletableFuture;

public class CloudPlayerJoinNetworkEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private String playerName;

    public CloudPlayerJoinNetworkEvent(String playerName) {
        this.playerName = playerName;
    }


    public CompletableFuture<ICloudPlayer> getPlayer() {
        return PoloCloudAPI.getInstance().getCloudPlayerManager().getOnlinePlayer(playerName);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
