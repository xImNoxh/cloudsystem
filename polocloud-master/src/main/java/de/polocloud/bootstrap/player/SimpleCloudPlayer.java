package de.polocloud.bootstrap.player;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.protocol.packet.gameserver.permissions.PermissionCheckResponsePacket;
import de.polocloud.api.network.protocol.packet.gameserver.proxy.ProxyTablistUpdatePacket;
import de.polocloud.api.network.protocol.packet.master.MasterPlayerKickPacket;
import de.polocloud.api.network.protocol.packet.master.MasterPlayerSendMessagePacket;
import de.polocloud.api.network.protocol.packet.master.MasterPlayerSendToServerPacket;
import de.polocloud.api.network.response.ResponseHandler;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.bootstrap.Master;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleCloudPlayer implements ICloudPlayer {

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private String name;
    private UUID uuid;

    private IGameServer proxyGameServer;
    private IGameServer minecraftGameServer;

    public SimpleCloudPlayer(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public IGameServer getProxyServer() {
        return this.proxyGameServer;
    }

    @Override
    public IGameServer getMinecraftServer() {
        return this.minecraftGameServer;
    }

    public void setMinecraftGameServer(IGameServer minecraftGameServer) {
        this.minecraftGameServer = minecraftGameServer;
    }

    public void setProxyGameServer(IGameServer proxyGameServer) {
        this.proxyGameServer = proxyGameServer;
    }

    @Override
    public void sendMessage(String message) {
        getProxyServer().sendPacket(new MasterPlayerSendMessagePacket(getUUID(), message));
    }

    @Override
    public void sendTablist(String header, String footer) {
        getProxyServer().sendPacket(new ProxyTablistUpdatePacket(uuid, header, footer));
    }

    @Override
    public void sendTo(IGameServer gameServer) {
        getProxyServer().sendPacket(new MasterPlayerSendToServerPacket(getUUID(), gameServer.getName()));
    }

    @Override
    public CompletableFuture<Boolean> hasPermissions(String permission) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        executor.execute(() -> {
            UUID requestId = UUID.randomUUID();
            PermissionCheckResponsePacket packet = new PermissionCheckResponsePacket(requestId, permission, uuid, false);
            ResponseHandler.register(requestId, completableFuture);
            getProxyServer().sendPacket(packet);
        });
        return completableFuture;
    }

    @Override
    public void kick(String message) {
        getProxyServer().sendPacket(new MasterPlayerKickPacket(getUUID(), message));
    }

    @Override
    public void sendToFallback() {
        IGameServer gameServer = Master.getInstance().getFallbackSearchService().searchForGameServer(Master.getInstance().getFallbackSearchService().searchForTemplate(this, false));
        if (gameServer == null) {
            kick("§cThe server you were on went down, but no fallback server was found!");
            return;
        }
        sendMessage("§cThe server you were on went down, you have been moved to a fallback server! §8(§b" + gameServer.getName() + "§8)");
        sendTo(gameServer);
    }
}
