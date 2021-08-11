package de.polocloud.plugin.api.player;

import de.polocloud.api.network.protocol.packet.api.cloudplayer.APIRequestCloudPlayerPacket;
import de.polocloud.api.network.protocol.packet.cloudplayer.CloudPlayerRegisterPacket;
import de.polocloud.api.network.protocol.packet.cloudplayer.CloudPlayerUnregisterPacket;
import de.polocloud.api.network.response.ResponseHandler;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.plugin.CloudPlugin;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class APICloudPlayerManager implements ICloudPlayerManager {

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public void register(ICloudPlayer cloudPlayer) {
        CloudPlugin.getCloudPluginInstance().getNetworkClient().sendPacket(new CloudPlayerRegisterPacket(cloudPlayer));
    }

    @Override
    public void unregister(ICloudPlayer cloudPlayer) {
        CloudPlugin.getCloudPluginInstance().getNetworkClient().sendPacket(new CloudPlayerUnregisterPacket(cloudPlayer));
    }

    @Override
    public CompletableFuture<List<ICloudPlayer>> getAllOnlinePlayers() {
        return (CompletableFuture<List<ICloudPlayer>>) sendPlayerData(new CompletableFuture<List<ICloudPlayer>>(), APIRequestCloudPlayerPacket.Action.ALL, "_");
    }

    @Override
    public CompletableFuture<ICloudPlayer> getOnlinePlayer(String name) {
        return (CompletableFuture<ICloudPlayer>) sendPlayerData(new CompletableFuture<ICloudPlayer>(), APIRequestCloudPlayerPacket.Action.BY_NAME, name);
    }

    @Override
    public CompletableFuture<ICloudPlayer> getOnlinePlayer(UUID uuid) {
        return (CompletableFuture<ICloudPlayer>) sendPlayerData(new CompletableFuture<ICloudPlayer>(), APIRequestCloudPlayerPacket.Action.BY_UUID, uuid.toString());
    }

    @Override
    public CompletableFuture<Boolean> isPlayerOnline(String name) {
        return (CompletableFuture<Boolean>) sendPlayerData(new CompletableFuture<Boolean>(), APIRequestCloudPlayerPacket.Action.ONLINE_NAME, name);
    }

    @Override
    public CompletableFuture<Boolean> isPlayerOnline(UUID uuid) {
        return (CompletableFuture<Boolean>) sendPlayerData(new CompletableFuture<Boolean>(), APIRequestCloudPlayerPacket.Action.ONLINE_UUID, uuid.toString());
    }

    public CompletableFuture<?> sendPlayerData(CompletableFuture<?> future, APIRequestCloudPlayerPacket.Action action, String data) {
        executor.execute(() -> {
            UUID requestId = UUID.randomUUID();
            APIRequestCloudPlayerPacket packet = new APIRequestCloudPlayerPacket(requestId, action, data);
            ResponseHandler.register(requestId, future);
            CloudPlugin.getCloudPluginInstance().getNetworkClient().sendPacket(packet);
        });
        return future;
    }

}
