package de.polocloud.api.gameserver;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerUpdatePacket;
import de.polocloud.api.network.protocol.packet.master.MasterRequestsServerTerminatePacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.util.PoloUtils;
import de.polocloud.api.util.Snowflake;
import de.polocloud.api.wrapper.IWrapper;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DefaultGameServer implements IGameServer {

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
    private ITemplate template;

    public DefaultGameServer(String name, String motd, boolean serviceVisibility, GameServerStatus gameServerStatus, long snowflake, long ping, long startedTime, long memory, int port, int maxplayers, ITemplate template) {
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
    }

    @Override
    public long getSnowflake() {
        return snowflake;
    }

    @Override
    public IWrapper getWrapper() {
        for (String wrapperName : getTemplate().getWrapperNames()) {
            IWrapper get = PoloCloudAPI.getInstance().getWrapperManager().getWrapper(wrapperName);
            if (get != null) {
                return get;
            }
        }
        return null;
    }


    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setSnowflake(long snowflake) {
        this.snowflake = snowflake;
    }

    @Override
    public void clone(Consumer<IGameServer> consumer) {
        IGameServer gameServer = this;
        consumer.accept(gameServer);
    }

    public void setTemplate(ITemplate template) {
        this.template = template;
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
        this.setTemplate(PoloUtils.sneakyThrows(() -> PoloCloudAPI.getInstance().getTemplateService().getTemplateByName(template).get()));
    }

    @Override
    public ITemplate getTemplate() {
        return template;
    }

    @Override
    public List<ICloudPlayer> getCloudPlayers() {
        return PoloUtils.sneakyThrows(() -> PoloCloudAPI.getInstance().getCloudPlayerManager().getAllOnlinePlayers().get()).stream().filter(cloudPlayer -> cloudPlayer.getMinecraftServer().getName().equalsIgnoreCase(this.name) || cloudPlayer.getProxyServer().getName().equalsIgnoreCase(this.name)).collect(Collectors.toList());
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
        getWrapper().sendPacket(new MasterRequestsServerTerminatePacket(this));
    }

    @Override
    public void terminate() {
        stop();
    }

    @Override
    public void sendPacket(Packet packet) {
        PoloCloudAPI.getInstance().getConnection().sendPacket(packet);
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
        sendPacket(new GameServerUpdatePacket(this));
    }
}
