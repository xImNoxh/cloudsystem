package de.polocloud.bootstrap.player;

import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SimpleCloudPlayerManager implements ICloudPlayerManager {

    private List<ICloudPlayer> cloudPlayers = new ArrayList<>();

    @Override
    public void register(ICloudPlayer cloudPlayer) {
        cloudPlayers.add(cloudPlayer);
    }

    @Override
    public void unregister(ICloudPlayer cloudPlayer) {
        cloudPlayers.remove(cloudPlayer);
    }

    @Override
    public CompletableFuture<List<ICloudPlayer>> getAllOnlinePlayers() {
        return CompletableFuture.completedFuture(this.cloudPlayers);
    }

    @Override
    public CompletableFuture<ICloudPlayer> getOnlinePlayer(String name) {
        return CompletableFuture.completedFuture(this.cloudPlayers.stream().filter(key -> key.getName().equalsIgnoreCase(name)).findAny().orElse(null));
    }

    @Override
    public CompletableFuture<ICloudPlayer> getOnlinePlayer(UUID uuid) {
        return CompletableFuture.completedFuture(this.cloudPlayers.stream().filter(key -> key.getUUID().toString().equalsIgnoreCase(uuid.toString())).findAny().orElse(null));
    }

    @Override
    public CompletableFuture<Boolean> isPlayerOnline(String name) {
        return CompletableFuture.completedFuture(getOnlinePlayer(name) != null);
    }

    @Override
    public CompletableFuture<Boolean> isPlayerOnline(UUID uuid) {
        try {
            return CompletableFuture.completedFuture(getOnlinePlayer(uuid).get() != null);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(false);
    }
}
