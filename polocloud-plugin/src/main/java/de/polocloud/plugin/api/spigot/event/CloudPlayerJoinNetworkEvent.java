package de.polocloud.plugin.api.spigot.event;

import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.plugin.api.CloudExecutor;
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
        return CloudExecutor.getInstance().getCloudPlayerManager().getOnlinePlayer(playerName);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
