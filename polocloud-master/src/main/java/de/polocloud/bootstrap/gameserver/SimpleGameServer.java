package de.polocloud.bootstrap.gameserver;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerShutdownPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerUpdatePacket;
import de.polocloud.api.network.protocol.packet.master.MasterRequestsServerTerminatePacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.util.PoloUtils;
import de.polocloud.api.util.Snowflake;
import de.polocloud.api.wrapper.IWrapper;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SimpleGameServer implements IGameServer {

    private IWrapper wrapper;
    private String name;
    private GameServerStatus status;
    private ChannelHandlerContext ctx;

    private boolean serviceVisibility;

    private long totalMemory = 0;

    private long snowflake;
    private ITemplate template;
    private long startTime;

    private int maxPlayers;
    private String motd;

    private long ping = -1;

    private int port;

    private List<ICloudPlayer> cloudPlayers = new ArrayList<>();

    public SimpleGameServer(IWrapper wrapper, String name, GameServerStatus status, ChannelHandlerContext ctx, long snowflake, ITemplate template, long startTime, String motd, int maxPlayers, boolean serviceVisibility) {
        this.wrapper = wrapper;
        this.name = name;
        this.status = status;
        this.ctx = ctx;
        this.snowflake = snowflake;
        this.template = template;
        this.startTime = startTime;
        this.motd = motd;
        this.maxPlayers = maxPlayers;
        this.serviceVisibility = serviceVisibility;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public GameServerStatus getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(GameServerStatus status) {
        this.status = status;
    }

    @Override
    public long getSnowflake() {
        return this.snowflake;
    }

    @Override
    public ITemplate getTemplate() {
        return this.template;
    }

    @Override
    public List<ICloudPlayer> getCloudPlayers() {
        return this.cloudPlayers;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public long getPing() {
        return this.ping;
    }

    public void setPing(long ping) {
        this.ping = ping;
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
    public void setMemory(long memory) {
        this.totalMemory = memory;
    }

    public void setTemplate(ITemplate template) {
        this.template = template;
    }

    @Override
    public void setTemplate(String template) {
        this.setTemplate(PoloUtils.sneakyThrows(() -> PoloCloudAPI.getInstance().getTemplateService().getTemplateByName(template).get()));
    }

    @Override
    public void setSnowflake(long snowflake) {
        this.snowflake = snowflake;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void clone(Consumer<IGameServer> consumer) {
        IGameServer copy = this;
        consumer.accept(copy);
    }


    @Override
    public void newSnowflake() {
        this.setSnowflake(Snowflake.getInstance().nextId());
    }

    @Override
    public long getStartTime() {
        return this.startTime;
    }

    @Override
    public long getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(long value) {
        totalMemory = value;
    }

    @Override
    public int getOnlinePlayers() {
        return getCloudPlayers().size();
    }

    @Override
    public void stop() {
        sendPacket(new GameServerShutdownPacket(name));
    }

    @Override
    public void terminate() {
        wrapper.stopServer(this);
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
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
    public void sendPacket(Packet packet) {
        if (this.ctx != null) this.ctx.writeAndFlush(packet);
    }

    @Override
    public int getMaxPlayers() {
        return this.maxPlayers;
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
