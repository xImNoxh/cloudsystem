package de.polocloud.api.player;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.bridge.PoloPluginBridge;
import de.polocloud.api.bridge.PoloPluginBungeeBridge;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerUpdatePacket;
import de.polocloud.api.network.packets.gameserver.permissions.PermissionCheckResponsePacket;
import de.polocloud.api.network.packets.gameserver.proxy.ProxyTablistUpdatePacket;
import de.polocloud.api.network.packets.master.MasterPlayerKickPacket;
import de.polocloud.api.network.packets.master.MasterPlayerSendMessagePacket;
import de.polocloud.api.network.packets.master.MasterPlayerSendToServerPacket;
import de.polocloud.api.network.request.PacketMessenger;
import de.polocloud.api.property.IProperty;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class SimpleCloudPlayer implements ICloudPlayer {

    private final String name;
    private final UUID uniqueId;

    private String minecraftServer;
    private String proxyServer;

    public SimpleCloudPlayer(String name, UUID uniqueId) {
        this.name = name;
        this.uniqueId = uniqueId;
    }

    @Override
    public void sendTo(IGameServer gameServer) {
        PoloPluginBridge poloBridge = PoloCloudAPI.getInstance().getPoloBridge();
        if (poloBridge != null && poloBridge instanceof PoloPluginBungeeBridge) {
            ((PoloPluginBungeeBridge)poloBridge).connect(this.uniqueId, gameServer.getName());
            return;
        }
        MasterPlayerSendToServerPacket packet = new MasterPlayerSendToServerPacket(this.uniqueId, gameServer.getName());
        PoloCloudAPI.getInstance().sendPacket(packet);
    }

    @Override
    public void sendTabList(String header, String footer) {
        if (PoloCloudAPI.getInstance().getPoloBridge() != null) {
            PoloCloudAPI.getInstance().getPoloBridge().sendTabList(this.uniqueId, header, footer);
            return;
        }
        ProxyTablistUpdatePacket packet = new ProxyTablistUpdatePacket(this.uniqueId, header, footer);
        PoloCloudAPI.getInstance().sendPacket(packet);
    }

    @Override
    public Future<Boolean> hasPermissions(String permission) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        if (PoloCloudAPI.getInstance().getPoloBridge() != null) {
            completableFuture.complete(PoloCloudAPI.getInstance().getPoloBridge().hasPermission(this.uniqueId, permission));
        } else {
            UUID requestId = UUID.randomUUID();
            PermissionCheckResponsePacket packet = new PermissionCheckResponsePacket(requestId, permission, this.uniqueId, false);
            PacketMessenger.register(requestId, completableFuture);
            getProxyServer().sendPacket(packet);
        }
        return completableFuture;
    }

    @Override
    public void update() {
        PoloCloudAPI.getInstance().getCloudPlayerManager().updateObject(this);
        PoloCloudAPI.getInstance().sendPacket(new CloudPlayerUpdatePacket(this));
    }

    @Override
    public void kick(String reason) {
        if (PoloCloudAPI.getInstance().getPoloBridge() != null) {
            PoloCloudAPI.getInstance().getPoloBridge().kickPlayer(this.uniqueId, reason);
            return;
        }
        MasterPlayerKickPacket packet = new MasterPlayerKickPacket(this.uniqueId, reason);
        PoloCloudAPI.getInstance().sendPacket(packet);
    }

    @Override
    public void sendToFallback() {
        IGameServer fallback = PoloCloudAPI.getInstance().getFallbackManager().getFallback(this);
        if (fallback == null) {
            kick("§cThe server you were on went down, but no fallback server was found!");
            return;
        }
        this.sendTo(fallback);
    }

    @Override
    public IGameServer getProxyServer() {
        return PoloCloudAPI.getInstance().getGameServerManager().getCached(this.proxyServer);
    }

    @Override
    public void runCommand(String command) {
        PoloCloudAPI.getInstance().getCommandManager().runCommand(command, this);
    }

    @Override
    public void sendMessage(String text) {
        if (PoloCloudAPI.getInstance().getPoloBridge() != null) {
            PoloCloudAPI.getInstance().getPoloBridge().sendMessage(this.uniqueId, text);
            return;
        }
        MasterPlayerSendMessagePacket packet = new MasterPlayerSendMessagePacket(this.uniqueId, text);
        PoloCloudAPI.getInstance().sendPacket(packet);
    }

    @Override
    public ExecutorType getType() {
        return ExecutorType.PLAYER;
    }

    @Override
    public boolean hasPermission(String permission) {
        try {
            return hasPermissions(permission).get();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }

    @Override
    public List<IProperty> getProperties() {
        return PoloCloudAPI.getInstance().getPropertyManager().getProperties(this.uniqueId);
    }

    @Override
    public IProperty getProperty(String name) {
        return getProperties().stream().filter(property -> property.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public void insertProperty(Consumer<IProperty> consumer) {
        PoloCloudAPI.getInstance().getPropertyManager().insertProperty(this.uniqueId, consumer);
    }

    @Override
    public void deleteProperty(String name) {
        PoloCloudAPI.getInstance().getPropertyManager().deleteProperty(this.uniqueId, name);
    }

    public void setMinecraftServer(String minecraftServer) {
        this.minecraftServer = minecraftServer;
    }

    public void setProxyServer(String proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    public UUID getUUID() {
        return this.uniqueId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IGameServer getMinecraftServer() {
        return PoloCloudAPI.getInstance().getGameServerManager().getCached(this.minecraftServer);
    }

}
