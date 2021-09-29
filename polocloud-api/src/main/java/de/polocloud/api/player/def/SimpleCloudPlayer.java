package de.polocloud.api.player.def;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.bridge.PoloPluginBridge;
import de.polocloud.api.bridge.PoloPluginBungeeBridge;
import de.polocloud.api.chat.CloudComponent;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.event.impl.player.CloudPlayerPermissionCheckEvent;
import de.polocloud.api.fallback.base.IFallback;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerUpdatePacket;
import de.polocloud.api.network.packets.gameserver.proxy.ProxyTablistUpdatePacket;
import de.polocloud.api.network.packets.master.MasterPlayerKickPacket;
import de.polocloud.api.network.packets.master.MasterPlayerSendComponentPacket;
import de.polocloud.api.network.packets.master.MasterPlayerSendMessagePacket;
import de.polocloud.api.network.packets.master.MasterPlayerSendToServerPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.base.response.PacketMessenger;
import de.polocloud.api.network.protocol.packet.base.response.ResponseState;
import de.polocloud.api.network.protocol.packet.base.response.base.IResponse;
import de.polocloud.api.network.protocol.packet.base.response.base.IResponseElement;
import de.polocloud.api.network.protocol.packet.base.response.def.Response;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.extras.IPlayerConnection;
import de.polocloud.api.player.extras.IPlayerSettings;
import de.polocloud.api.property.IProperty;
import de.polocloud.api.template.base.ITemplate;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter @Setter
public class SimpleCloudPlayer implements ICloudPlayer {

    private final String name;
    private final UUID uniqueId;
    private SimplePlayerConnection connection;

    private String minecraftServer;
    private String proxyServer;

    public SimpleCloudPlayer(String name, UUID uniqueId, IPlayerConnection connection) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.connection = (SimplePlayerConnection) connection;
    }

    @Override
    public void sendTo(IGameServer gameServer) {
        PoloPluginBridge poloBridge = PoloCloudAPI.getInstance().getPoloBridge();
        if (poloBridge instanceof PoloPluginBungeeBridge) {
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
    public IPlayerConnection getConnection() {
        return connection;
    }

    @Override
    public long getPing() {
        if (PoloCloudAPI.getInstance().getPoloBridge() != null) {
            return PoloCloudAPI.getInstance().getPoloBridge().getPing(this.uniqueId);
        }
        IResponseElement element = PacketMessenger.create().blocking().timeOutAfter(TimeUnit.SECONDS, 1L).orElse(new Response(ResponseState.TIMED_OUT)).send("player-ping", new JsonData("uniqueId", this.uniqueId)).get("ping");
        return element.isNull() ? -1L : element.getAsLong();
    }

    @Override
    public IPlayerSettings getSettings() {
        PoloPluginBridge poloBridge = PoloCloudAPI.getInstance().getPoloBridge();
        if (poloBridge instanceof PoloPluginBungeeBridge) {
            return ((PoloPluginBungeeBridge) poloBridge).getSettings(this.uniqueId);
        } else {
            IResponse response = PacketMessenger.create().setUpPassOn().blocking().orElse(new Response(ResponseState.TIMED_OUT)).timeOutAfter(TimeUnit.SECONDS, 2L).send("player-settings", new JsonData("uniqueId", this.uniqueId));

            if (response.isTimedOut() || response.getStatus() != ResponseState.SUCCESS) {
                return null;
            }
            return response.get("settings").getAsCustom(SimplePlayerSettings.class);
        }
    }


    @Override
    public ICloudPlayer sync() {
        if (PoloCloudAPI.getInstance() == null || PoloCloudAPI.getInstance().getCloudPlayerManager() == null) {
            return this;
        }
        return PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(this.getName());
    }


    @Override
    public void update() {
        PoloCloudAPI.getInstance().getCloudPlayerManager().update(this);
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
    public void sendToFallbackExcept(String... except) {
        IGameServer fallback = getFallbackRecursive(0, except);
        if (fallback == null) {
            this.kick(PoloCloudAPI.getInstance().getMasterConfig().getMessages().getKickedAndNoFallbackServer());
            return;
        }
        this.sendTo(fallback);
    }

    private IGameServer getFallbackRecursive(int tries, String... except) {
        tries++;

        if (tries >= 10) {
            return null;
        }

        List<IFallback> fallbacks = PoloCloudAPI.getInstance().getFallbackManager().getAvailableFallbacks();

        IFallback fb = fallbacks.get(ThreadLocalRandom.current().nextInt(fallbacks.size()));
        ITemplate iTemplate = PoloCloudAPI.getInstance().getTemplateManager().getTemplate(fb.getTemplateName());
        List<IGameServer> gameServers = PoloCloudAPI.getInstance().getGameServerManager().getAllCached(iTemplate).stream().filter(gameServer -> gameServer.getStatus().equals(GameServerStatus.AVAILABLE)).collect(Collectors.toList());
        IGameServer fallback = gameServers.get(ThreadLocalRandom.current().nextInt(gameServers.size()));

        if (fallback != null && Arrays.asList(except).contains(fallback.getName())) {
            return getFallbackRecursive(tries, except);
        }
        return fallback;
    }

    @Override
    public void sendToFallback() {
        this.sendToFallbackExcept();
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
    public void sendMessage(CloudComponent component) {
        if (PoloCloudAPI.getInstance().getPoloBridge() != null && PoloCloudAPI.getInstance().getPoloBridge() instanceof PoloPluginBungeeBridge) {
            ((PoloPluginBungeeBridge)PoloCloudAPI.getInstance().getPoloBridge()).sendComponent(this.uniqueId, component);
            return;
        }
        Packet packet = new MasterPlayerSendComponentPacket(this.uniqueId, component);
        PoloCloudAPI.getInstance().sendPacket(packet);
    }

    @Override
    public ExecutorType getType() {
        return ExecutorType.PLAYER;
    }

    @Override
    public boolean hasPermission(String permission) {

        if (PoloCloudAPI.getInstance().getPoloBridge() != null) {
            return PoloCloudAPI.getInstance().getPoloBridge().hasPermission(this.uniqueId, permission);
        } else {
            CloudPlayerPermissionCheckEvent checkEvent = PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudPlayerPermissionCheckEvent(this, permission));
            return checkEvent.hasPermission();
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
