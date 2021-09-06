package de.polocloud.api.gameserver.base;

import com.google.gson.annotations.Expose;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.event.impl.server.CloudGameServerPropertyUpdateEvent;
import de.polocloud.api.event.impl.server.CloudGameServerStatusChangeEvent;
import de.polocloud.api.event.impl.server.CloudGameServerUpdateEvent;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.packets.gameserver.GameServerUpdatePacket;
import de.polocloud.api.network.protocol.packet.base.other.ForwardingPacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.property.IProperty;
import de.polocloud.api.property.def.SimpleProperty;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.util.gson.Exclude;
import de.polocloud.api.util.gson.PoloHelper;
import de.polocloud.api.util.Snowflake;
import de.polocloud.api.wrapper.base.IWrapper;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This is just an info class
 * No Netty instances like {@link ChannelHandlerContext} can be returned
 */
public class SimpleGameServer implements IGameServer {

    /**
     * The name of the server
     */
    private String name;

    /**
     * The motd of the server
     */
    private String motd;

    /**
     * The visibility state
     */
    private boolean serviceVisibility;

    /**
     * The status
     */
    private GameServerStatus gameServerStatus;

    boolean statusChanged;

    /**
     * The snowflake id
     */
    private long snowflake;

    /**
     * The ping in ms
     */
    private long ping;

    /**
     * The startedTime in ms
     */
    private long startedTime;

    /**
     * The startedTime as long
     */
    private long memory;

    /**
     * The port
     */
    private int port;

    /**
     * The maximum players
     */
    private int maxPlayers;

    /**
     * The template (group)
     */
    private String template;

    /**
     * If authenticated
     */
    private boolean registered;

    /**
     * The netty context
     */
    @Exclude
    private ChannelHandlerContext channelHandlerContext;

    /**
     * The host
     */
    private String host;

    /**
     * The properties
     */
    private List<IProperty> properties;

    public SimpleGameServer() {
    }

    public SimpleGameServer(String name, String motd, boolean serviceVisibility, GameServerStatus gameServerStatus, long snowflake, long ping, long startedTime, long memory, int port, int maxplayers, String template) {
        this.name = name;
        this.motd = motd;
        this.serviceVisibility = serviceVisibility;
        this.gameServerStatus = gameServerStatus;
        this.snowflake = snowflake;
        this.ping = ping;
        this.startedTime = startedTime;
        this.memory = memory;
        this.port = port;
        this.maxPlayers = maxplayers;
        this.template = template;
        this.registered = false;
        this.host = "127.0.0.1";
        this.properties = new ArrayList<>();
    }

    @Override
    public List<IProperty> getProperties() {
        return this.properties;
    }

    @Override
    public IProperty getProperty(String name) {
        return this.properties.stream().filter(property -> property.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public void insertProperty(Consumer<IProperty> consumer) {
        SimpleProperty property = new SimpleProperty();
        consumer.accept(property);

        PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudGameServerPropertyUpdateEvent(this, property), event -> {
            if (!event.isCancelled()) {
                this.properties.add(event.getProperty());
            }
        });

    }

    @Override
    public void deleteProperty(String name) {
        this.properties.removeIf(property -> property.getName().equalsIgnoreCase(name));
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String getHost() {
        return host;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    @Override
    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    @Override
    public boolean isRegistered() {
        return registered;
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public GameServerStatus getStatus() {
        return gameServerStatus;
    }

    @Override
    public void setStatus(GameServerStatus status) {
        gameServerStatus = status;
        statusChanged = true;
        if (status == GameServerStatus.INVISIBLE) {
            this.setVisible(false);
        }
        if (status == GameServerStatus.AVAILABLE) {
            this.setVisible(true);
        }
    }

    @Override
    public long getSnowflake() {
        return snowflake;
    }

    @Override
    public IWrapper getWrapper() {
        for (IWrapper allWrapper : getAllWrappers()) {
            return allWrapper;
        }
        return null;
    }

    @Override
    public IWrapper[] getAllWrappers() {
        List<IWrapper> wrappers = new ArrayList<>();
        for (String wrapperName : getTemplate().getWrapperNames()) {
            IWrapper get = PoloCloudAPI.getInstance().getWrapperManager().getWrapper(wrapperName);
            if (get != null) {
                wrappers.add(get);
            }
        }
        return wrappers.toArray(new IWrapper[0]);
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setProperties(List<IProperty> properties) {
        this.properties = properties;
    }

    @Override
    public void setSnowflake(long snowflake) {
        this.snowflake = snowflake;
    }

    @Override
    public void setStartedTime(long ms) {
        this.startedTime = ms;
    }

    @Override
    public void clone(Consumer<IGameServer> consumer) {
        IGameServer gameServer = this;
        consumer.accept(gameServer);
    }

    public void setTemplate(ITemplate template) {
        this.template = template.getName();
    }

    @Override
    public void setMemory(long memory) {
        this.memory = memory;
    }

    @Override
    public void newSnowflake() {
        this.setSnowflake(Snowflake.getInstance().nextId());
    }

    @Override
    public void setTemplate(String template) {
        this.setTemplate(PoloHelper.sneakyThrows(() -> PoloCloudAPI.getInstance().getTemplateManager().getTemplate(template)));
    }

    @Override
    public ITemplate getTemplate() {
        return PoloCloudAPI.getInstance().getTemplateManager().getTemplate(this.template);
    }

    @Override
    public List<ICloudPlayer> getCloudPlayers() {
        return PoloCloudAPI.getInstance().getCloudPlayerManager().getAllCached().stream().filter(cloudPlayer -> cloudPlayer.getMinecraftServer() != null && cloudPlayer.getMinecraftServer().getName().equalsIgnoreCase(this.name) || cloudPlayer.getProxyServer() != null && cloudPlayer.getProxyServer().getName().equalsIgnoreCase(this.name)).collect(Collectors.toList());
    }

    @Override
    public long getTotalMemory() {
        return memory;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public int getOnlinePlayers() {
        return this.getCloudPlayers().size();
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public long getPing() {
        return ping;
    }

    @Override
    public long getStartTime() {
        return startedTime;
    }

    @Override
    public void stop() {
        IWrapper wrapper = getWrapper();
        if (wrapper == null) {
            return;
        }
        wrapper.stopServer(this);
    }

    @Override
    public void terminate() {
        stop();
    }

    @Override
    public void receivePacket(Packet packet) {
        sendPacket(new ForwardingPacket(PoloType.GENERAL_GAMESERVER, this.name, packet));
    }

    @Override
    public void sendPacket(Packet packet) {
        if (this.channelHandlerContext != null) {
            this.channelHandlerContext.writeAndFlush(packet).addListener(PoloHelper.getChannelFutureListener(SimpleGameServer.class));
            return;
        }
        PoloCloudAPI.getInstance().getConnection().sendPacket(new ForwardingPacket(PoloType.GENERAL_GAMESERVER, this.name, packet));
    }

    @Override
    public String getMotd() {
        return motd;
    }

    @Override
    public void setMotd(String motd) {
        this.motd = motd;
    }

    @Override
    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public ChannelHandlerContext ctx() {
        return null;
    }

    @Override
    public void setMaxPlayers(int players) {
        this.maxPlayers = players;
    }

    @Override
    public void setVisible(boolean serviceVisibility) {
        this.serviceVisibility = serviceVisibility;
    }

    @Override
    public boolean getServiceVisibility() {
        return serviceVisibility;
    }

    @Override
    public void update() {
        PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudGameServerUpdateEvent(this), cloudGameServerUpdateEvent -> {
            if (!cloudGameServerUpdateEvent.isCancelled()) {
                this.updateInternally();
                PoloCloudAPI.getInstance().sendPacket(new GameServerUpdatePacket(this));
            }
        });

    }

    @Override
    public void updateInternally() {
        if (statusChanged) {
            statusChanged = false;
            PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudGameServerStatusChangeEvent(this, this.gameServerStatus));
        }
        PoloCloudAPI.getInstance().getGameServerManager().updateObject(this);
    }
}
