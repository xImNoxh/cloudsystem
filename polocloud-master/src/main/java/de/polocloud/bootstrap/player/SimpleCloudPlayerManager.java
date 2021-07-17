package de.polocloud.bootstrap.player;

import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

        for (ICloudPlayer cloudPlayer : this.cloudPlayers) {
            if (cloudPlayer.getName().equalsIgnoreCase(name)) {
                return CompletableFuture.completedFuture(cloudPlayer);
            }
        }

        return null;
    }

    @Override
    public CompletableFuture<ICloudPlayer> getOnlinePlayer(UUID uuid) {

        for (ICloudPlayer cloudPlayer : this.cloudPlayers) {
            if (cloudPlayer.getUUID().toString().equalsIgnoreCase(uuid.toString())) {
                return CompletableFuture.completedFuture(cloudPlayer);
            }
        }

        return null;
    }

    @Override
    public CompletableFuture<Boolean> isPlayerOnline(String name) {
        return CompletableFuture.completedFuture(getOnlinePlayer(name) != null);
    }

    @Override
    public CompletableFuture<Boolean> isPlayerOnline(UUID uuid) {
        return CompletableFuture.completedFuture(getOnlinePlayer(uuid) != null);

    }
}
