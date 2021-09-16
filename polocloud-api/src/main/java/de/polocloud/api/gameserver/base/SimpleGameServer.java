package de.polocloud.api.gameserver.base;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.event.impl.server.GameServerPropertyUpdateEvent;
import de.polocloud.api.event.impl.server.GameServerStatusChangeEvent;
import de.polocloud.api.event.impl.server.GameServerUpdateEvent;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.packets.gameserver.GameServerUpdatePacket;
import de.polocloud.api.network.protocol.packet.base.other.ForwardingPacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.property.IProperty;
import de.polocloud.api.property.def.SimpleProperty;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.util.Acceptable;
import de.polocloud.api.util.gson.Exclude;
import de.polocloud.api.util.gson.PoloHelper;
import de.polocloud.api.util.Snowflake;
import de.polocloud.api.wrapper.base.IWrapper;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This is just an info class
 * No Netty instances like {@link ChannelHandlerContext} can be returned
 */
public class SimpleGameServer implements IGameServer {

    /**
     * The motd of the server
     */
    private String motd;

    /**
     * The id of this server
     */
    private int id;

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
     * The online players
     */
    private int onlinePlayers;

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

    private String versionString;

    private String[] playerInfo;

    public SimpleGameServer() {
    }

    public SimpleGameServer(int id, String motd, GameServerStatus gameServerStatus, long snowflake, long startedTime, long memory, int port, int maxplayers, String template) {
        this.id = id;
        this.motd = motd;
        this.gameServerStatus = gameServerStatus;
        this.snowflake = snowflake;
        this.startedTime = startedTime;
        this.memory = memory;
        this.port = port;
        this.maxPlayers = maxplayers;
        this.template = template;
        this.registered = false;
        this.host = "127.0.0.1";
        this.properties = new ArrayList<>();
        this.onlinePlayers = getPlayers().size();

        this.versionString = null;
        this.playerInfo = new String[0];
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

        PoloCloudAPI.getInstance().getEventManager().fireEvent(new GameServerPropertyUpdateEvent(this, property), event -> {
            if (!event.isCancelled()) {
                this.properties.add(event.getProperty());
            }
        });

    }

    @Override
    public void scheduleShutdown(TimeUnit unit, long l) {
        Scheduler.runtimeScheduler().schedule(this::terminate, unit.toMillis(l));
    }

    @Override
    public void scheduleShutdown(TimeUnit unit, long l, Acceptable<IGameServer> request) {
        long snowflake = this.snowflake;
        Scheduler.runtimeScheduler().schedule(() -> {
            try {
                if (snowflake == this.snowflake) {
                    if (request.isAccepted(this)) {
                        this.terminate();
                    }
                }
            } catch (Exception e) {
                //Exception
            }
        } ,unit.toMillis(l));
    }

    @Override
    public int getId() {
        return id;
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
        return template + "-" + id;
    }

    @Override
    public GameServerStatus getStatus() {
        return gameServerStatus;
    }

    @Override
    public void setStatus(GameServerStatus status) {
        gameServerStatus = status;
        statusChanged = true;
    }

    @Override
    public IGameServer sync() {
        if (PoloCloudAPI.getInstance() == null || PoloCloudAPI.getInstance().getGameServerManager() == null) {
            return this;
        }
        return PoloCloudAPI.getInstance().getGameServerManager().getCached(this.getName());
    }

    @Override
    public long getSnowflake() {
        return snowflake;
    }

    @Override
    public IWrapper getWrapper() {
        for (IWrapper allWrapper : getWrappers()) {
            return allWrapper;
        }
        return null;
    }

    @Override
    public IWrapper[] getWrappers() {
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
    public void setId(int id) {
        this.id = id;
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

    @Override
    public void newId() {
        IGameServerManager gameServerManager = PoloCloudAPI.getInstance().getGameServerManager();
        if (this.getTemplate() == null) {
            setId(-1);
            return;
        }
        int freeId = gameServerManager.getFreeId(this.getTemplate());
        this.setId(freeId);
    }

    @Override
    public void newPort() {
        IGameServerManager gameServerManager = PoloCloudAPI.getInstance().getGameServerManager();
        if (this.getTemplate() == null) {
            setPort(-1);
            return;
        }
        int freePort = gameServerManager.getFreePort(this.getTemplate());
        this.setPort(freePort);
    }

    @Override
    public void newIdentification() {
        this.newId();
        this.newSnowflake();
        this.newPort();
        this.setStartedTime(System.currentTimeMillis());
    }

    @Override
    public void setTemplate(ITemplate template) {
        this.template = template.getName();

        this.setMotd(template.getMotd());
        this.setMaxPlayers(template.getMaxPlayers());
        this.setMemory(template.getMaxMemory());
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
    public ITemplate getTemplate() {
        return PoloCloudAPI.getInstance().getTemplateManager().getTemplate(this.template);
    }

    @Override
    public List<ICloudPlayer> getPlayers() {
        return PoloCloudAPI.getInstance().getCloudPlayerManager().getAllCached().stream().filter(cloudPlayer -> cloudPlayer.getMinecraftServer() != null && cloudPlayer.getMinecraftServer().getName().equalsIgnoreCase(this.getName()) || cloudPlayer.getProxyServer() != null && cloudPlayer.getProxyServer().getName().equalsIgnoreCase(this.getName())).collect(Collectors.toList());
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
        return onlinePlayers;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public long getStartTime() {
        return startedTime;
    }


    @Override
    public void terminate() {
        PoloCloudAPI.getInstance().getGameServerManager().stopServer(this);
    }

    @Override
    public void receivePacket(Packet packet) {
        sendPacket(new ForwardingPacket(PoloType.GENERAL_GAMESERVER, this.getName(), packet));
    }

    @Override
    public void sendPacket(Packet packet) {
        if (this.channelHandlerContext != null) {
            this.channelHandlerContext.writeAndFlush(packet).addListener(PoloHelper.getChannelFutureListener(SimpleGameServer.class));
            return;
        }
        PoloCloudAPI.getInstance().getConnection().sendPacket(new ForwardingPacket(PoloType.GENERAL_GAMESERVER, this.getName(), packet));
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
    public void setServerPing(String motd, int maxPlayers, int onlinePlayers, String versionString, String[] playerInfo) {
        this.motd = motd;
        this.maxPlayers = maxPlayers;
        this.onlinePlayers = onlinePlayers;

        this.versionString = versionString;
        this.playerInfo = playerInfo;
    }

    public String[] getPlayerInfo() {
        return playerInfo;
    }

    public String getVersionString() {
        return versionString;
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
    public void update() {
        PoloCloudAPI.getInstance().getEventManager().fireEvent(new GameServerUpdateEvent(this), gameServerUpdateEvent -> {
            if (!gameServerUpdateEvent.isCancelled()) {
                this.updateInternally();
                PoloCloudAPI.getInstance().sendPacket(new GameServerUpdatePacket(this));
            }
        });

    }

    @Override
    public InetSocketAddress getAddress() {
        return new InetSocketAddress(this.host, this.port);
    }

    @Override
    public void updateInternally() {
        if (statusChanged) {
            statusChanged = false;
            PoloCloudAPI.getInstance().getEventManager().fireEvent(new GameServerStatusChangeEvent(this, this.gameServerStatus));
        }
        PoloCloudAPI.getInstance().getGameServerManager().update(this);
    }
}
