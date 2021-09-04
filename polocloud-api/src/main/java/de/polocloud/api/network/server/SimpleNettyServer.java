package de.polocloud.api.network.server;

import com.google.inject.Inject;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.net.ChannelActiveEvent;
import de.polocloud.api.event.impl.net.ChannelInactiveEvent;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.client.INettyClient;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.codec.PacketDecoder;
import de.polocloud.api.network.protocol.codec.PacketEncoder;
import de.polocloud.api.network.protocol.codec.prepender.NettyPacketLengthDeserializer;
import de.polocloud.api.network.protocol.codec.prepender.NettyPacketLengthSerializer;
import de.polocloud.api.network.protocol.packet.handler.*;

import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.util.map.UniqueMap;
import de.polocloud.api.wrapper.base.IWrapper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

import javax.inject.Named;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.List;

public class SimpleNettyServer implements INettyServer, IListener {


    /**
     * The port of this server to run on
     */
    @Inject @Named(value = "setting_server_start_port") private int port;

    /**
     * The protocol instance for managing handlers
     */
    @Inject private IProtocol protocol;

    /**
     * The channel
     */
    private Channel channel;

    /**
     * All connected clients
     */
    private final ChannelGroup connectedClients;

    private final UniqueMap<PoloType, INettyClient> connectedInfos;

    private ChannelHandlerContext ctx;

    public SimpleNettyServer() {
        this.connectedClients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        this.connectedInfos = new UniqueMap<>();

        PoloCloudAPI.getInstance().getEventManager().registerListener(this);
    }

    @Override
    public void start() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            (serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)).childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    channel.pipeline()
                        .addLast(new NettyPacketLengthDeserializer())
                        .addLast(new PacketDecoder())
                        .addLast(new NettyPacketLengthSerializer())
                        .addLast(new PacketEncoder())
                        .addLast(new NetworkHandler(SimpleNettyServer.this));
                }

            }).option(ChannelOption.SO_BACKLOG, 128);
            ChannelFuture channelFuture = serverBootstrap.bind(this.port).addListener((ChannelFutureListener) future -> channel = future.channel()).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }


    @EventHandler
    public void handleInactive(ChannelInactiveEvent event) {
        ChannelHandlerContext chx = event.getChx();
        Channel channel = chx.channel();
        this.connectedClients.removeIf(c -> c.id().asLongText().equalsIgnoreCase(channel.id().asLongText()));
    }

    @EventHandler
    public void handleActive(ChannelActiveEvent event) {
        ChannelHandlerContext chx = event.getChx();
        Channel channel = chx.channel();
        this.connectedClients.add(channel);
    }

    @Override
    public void sendPacket(Packet packet, Channel... channels) {
        for (Channel connectedClient : channels) {
            if (!connectedClient.isOpen() || !connectedClient.isWritable()) {
                Scheduler.runtimeScheduler().schedule(() -> sendPacket(packet, channels), () -> connectedClient.isOpen() && connectedClient.isWritable());
                return;
            }
            connectedClient.writeAndFlush(packet).addListener((ChannelFutureListener) channelFuture -> {
                if (!channelFuture.isSuccess()) {
                    Throwable cause = channelFuture.cause();
                    if (cause instanceof ClosedChannelException) {
                        return;
                    }
                    System.out.println("[NettyServer@" + packet.getClass().getSimpleName() + "] Ran into error while processing Packet :");
                    cause.printStackTrace();
                }
            });
        }
    }

    @Override
    public boolean terminate() {
        if (this.channel == null) {
            return false;
        }
        return this.channel.close().isSuccess();
    }

    @Override
    public List<Channel> getConnectedClients() {
        return new ArrayList<>(connectedClients);
    }

    @Override //TODO
    public UniqueMap<PoloType, INettyClient> getClientsWithType() {
        return connectedInfos;
    }

    @Override
    public IProtocol getProtocol() {
        return protocol;
    }

    @Override
    public InetSocketAddress getConnectedAddress() {
        return new InetSocketAddress(this.port);
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void sendPacket(Packet packet, PoloType receiver) {
        if (receiver == PoloType.GENERAL_GAMESERVER || receiver == PoloType.PLUGIN_PROXY || receiver == PoloType.PLUGIN_SPIGOT) {
            for (IGameServer iGameServer : PoloCloudAPI.getInstance().getGameServerManager().getAllCached()) {
                iGameServer.sendPacket(packet);
            }
        }
        if (receiver == PoloType.WRAPPER) {
            for (IWrapper wrapper : PoloCloudAPI.getInstance().getWrapperManager().getWrappers()) {
                wrapper.sendPacket(packet);
            }
        }
    }

    @Override
    public ChannelHandlerContext ctx() {
        return ctx;
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public void sendPacket(Packet packet) {
        if (channel == null) {
            Scheduler.runtimeScheduler().schedule(() -> sendPacket(packet), () -> channel != null);
            return;
        }
        if (!channel.isOpen() || !channel.isWritable() || !channel.isActive()) {
            Scheduler.runtimeScheduler().schedule(() -> sendPacket(packet), () -> channel != null);
            return;
        }
        this.sendPacket(packet, this.connectedClients.toArray(new Channel[0]));
    }
}
