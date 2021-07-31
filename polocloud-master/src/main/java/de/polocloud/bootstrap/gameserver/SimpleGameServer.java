package de.polocloud.bootstrap.gameserver;

import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerShutdownPacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.ITemplate;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

public class SimpleGameServer implements IGameServer {

    private String name;
    private GameServerStatus status;
    private ChannelHandlerContext ctx;

    private long totalMemory = 0;

    private long snowflake;
    private ITemplate template;
    private long startTime;

    private String motd;

    private long ping = -1;

    private int port;

    private List<ICloudPlayer> cloudPlayers = new ArrayList<>();

    public SimpleGameServer(String name, GameServerStatus status, ChannelHandlerContext ctx, long snowflake, ITemplate template, long startTime, String motd) {
        this.name = name;
        this.status = status;
        this.ctx = ctx;
        this.snowflake = snowflake;
        this.template = template;
        this.startTime = startTime;
        this.motd = motd;
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
    public void setStatus(GameServerStatus status) {
        this.status = status;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    public void setPing(long ping) {
        this.ping = ping;
    }

    @Override
    public long getPing() {
        return this.ping;
    }


    public void setPort(int port) {
        this.port = port;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
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
        sendPacket(new GameServerShutdownPacket());
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    @Override
    public void setMotd(String motd) {
        this.motd = motd;
    }

    @Override
    public String getMotd() {
        return motd;
    }

    @Override
    public void sendPacket(Packet packet) {
        if (this.ctx != null) {
            this.ctx.writeAndFlush(packet);
        }
    }
}
