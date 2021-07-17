package de.polocloud.api.player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ICloudPlayerManager {

    void register(ICloudPlayer cloudPlayer);

    void unregister(ICloudPlayer cloudPlayer);

    CompletableFuture<List<ICloudPlayer>> getAllOnlinePlayers();

    CompletableFuture<ICloudPlayer> getOnlinePlayer(String name);

    CompletableFuture<ICloudPlayer> getOnlinePlayer(UUID uuid);

    CompletableFuture<Boolean> isPlayerOnline(String name);

    CompletableFuture<Boolean> isPlayerOnline(UUID uuid);

}
