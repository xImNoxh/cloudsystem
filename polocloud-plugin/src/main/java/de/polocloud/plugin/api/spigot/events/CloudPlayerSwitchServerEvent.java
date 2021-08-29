package de.polocloud.plugin.api.spigot.events;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.player.ICloudPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.concurrent.CompletableFuture;

public class CloudPlayerSwitchServerEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private String playerName;
    private String fromName;
    private String toName;

    public CloudPlayerSwitchServerEvent(String playerName, String toName, String fromName) {
        this.playerName = playerName;
        this.toName = toName;
        this.fromName = fromName;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getPlayerName() {
        return playerName;
    }

    public CompletableFuture<IGameServer> getFrom() {
        return null;
       // return PoloCloudAPI.getInstance().getGameServerManager().getCachedObject(fromName);
    }

    public CompletableFuture<IGameServer> getTo() {
        return null;
        // return PoloCloudAPI.getInstance().getGameServerManager().getCachedObject(toName);
    }

    public ICloudPlayer getPlayer() {
        return PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(playerName);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
