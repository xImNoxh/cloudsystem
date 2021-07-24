package de.polocloud.plugin.api.player;

import de.polocloud.api.network.protocol.packet.api.APIRequestCloudPlayerPacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.api.response.ResponseHandler;
import de.polocloud.plugin.protocol.NetworkClient;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class APICloudPlayerManager implements ICloudPlayerManager {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final NetworkClient networkClient = CloudPlugin.getInstance().getNetworkClient();

    @Override
    public void register(ICloudPlayer cloudPlayer) {
        throw new NotImplementedException();
    }

    @Override
    public void unregister(ICloudPlayer cloudPlayer) {
        throw new NotImplementedException();
    }

    @Override
    public CompletableFuture<List<ICloudPlayer>> getAllOnlinePlayers() {
        CompletableFuture<List<ICloudPlayer>> completableFuture = new CompletableFuture<>();

        executor.execute(() -> {

            UUID requestId = UUID.randomUUID();
            APIRequestCloudPlayerPacket packet = new APIRequestCloudPlayerPacket(requestId, APIRequestCloudPlayerPacket.Action.ALL, "_");
            ResponseHandler.register(requestId, completableFuture);

            networkClient.sendPacket(packet);

        });

        return completableFuture;
    }

    @Override
    public CompletableFuture<ICloudPlayer> getOnlinePlayer(String name) {
        CompletableFuture<ICloudPlayer> completableFuture = new CompletableFuture<>();

        executor.execute(() -> {

            UUID requestId = UUID.randomUUID();
            APIRequestCloudPlayerPacket packet = new APIRequestCloudPlayerPacket(requestId, APIRequestCloudPlayerPacket.Action.BY_NAME, name);
            ResponseHandler.register(requestId, completableFuture);
            networkClient.sendPacket(packet);

        });

        return completableFuture;
    }

    @Override
    public CompletableFuture<ICloudPlayer> getOnlinePlayer(UUID uuid) {
        CompletableFuture<ICloudPlayer> completableFuture = new CompletableFuture<>();

        executor.execute(() -> {

            UUID requestId = UUID.randomUUID();
            APIRequestCloudPlayerPacket packet = new APIRequestCloudPlayerPacket(requestId, APIRequestCloudPlayerPacket.Action.BY_UUID, uuid.toString());
            ResponseHandler.register(requestId, completableFuture);

            networkClient.sendPacket(packet);

        });

        return completableFuture;
    }

    @Override
    public CompletableFuture<Boolean> isPlayerOnline(String name) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        executor.execute(() -> {

            UUID requestId = UUID.randomUUID();
            APIRequestCloudPlayerPacket packet = new APIRequestCloudPlayerPacket(requestId, APIRequestCloudPlayerPacket.Action.ONLINE_NAME, name);
            ResponseHandler.register(requestId, completableFuture);

            networkClient.sendPacket(packet);

        });

        return completableFuture;
    }

    @Override
    public CompletableFuture<Boolean> isPlayerOnline(UUID uuid) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        executor.execute(() -> {

            UUID requestId = UUID.randomUUID();
            APIRequestCloudPlayerPacket packet = new APIRequestCloudPlayerPacket(requestId, APIRequestCloudPlayerPacket.Action.ONLINE_UUID, uuid.toString());
            ResponseHandler.register(requestId, completableFuture);

            networkClient.sendPacket(packet);

        });

        return completableFuture;
    }
}
