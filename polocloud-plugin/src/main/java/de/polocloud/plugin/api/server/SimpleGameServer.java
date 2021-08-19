package de.polocloud.plugin.api.server;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerUpdatePacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.util.PoloUtils;
import de.polocloud.api.util.Snowflake;
import de.polocloud.api.wrapper.IWrapper;
import de.polocloud.plugin.CloudPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SimpleGameServer implements IGameServer {

    private String name;
    private String motd;

    private boolean serviceVisibility;
    private GameServerStatus gameServerStatus;

    private long snowflake;
    private long ping;
    private long startedTime;
    private long memory;

    private int port;
    private int maxPlayers;

    private ITemplate template;

    private List<ICloudPlayer> players;

    public SimpleGameServer(String name, String motd, boolean serviceVisibility, GameServerStatus gameServerStatus, long snowflake, long ping, long startedTime, long memory, int port, int maxplayers, ITemplate template, List<ICloudPlayer> players) {
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
        this.players = new ArrayList<>();
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
        return players;
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
        return players.size();
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
        CloudPlugin.getCloudPluginInstance().getBootstrap().shutdown();
    }

    @Override
    public void terminate() {
        CloudPlugin.getCloudPluginInstance().getBootstrap().shutdown();
    }

    @Override
    public void sendPacket(Packet packet) {
        CloudPlugin.getCloudPluginInstance().getNetworkClient().sendPacket(packet);
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
