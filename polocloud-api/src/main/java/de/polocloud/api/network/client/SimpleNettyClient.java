package de.polocloud.api.network.client;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.codec.PacketDecoder;
import de.polocloud.api.network.protocol.codec.PacketEncoder;
import de.polocloud.api.network.protocol.codec.prepender.NettyPacketLengthDeserializer;
import de.polocloud.api.network.protocol.codec.prepender.NettyPacketLengthSerializer;
import de.polocloud.api.network.protocol.packet.handler.*;

import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.util.gson.PoloHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

@Getter
public class SimpleNettyClient implements INettyClient{

    @Inject
    @Named("setting_client_host")
    private String host;

    @Inject
    @Named("setting_client_port")
    private int port;

    private boolean everConnected;

    @Inject
    private IProtocol protocol;

    private ChannelFuture channelFuture;
    private Channel channel;
    private ChannelHandlerContext ctx;

    public SimpleNettyClient() {

    }

    public SimpleNettyClient(String host, int port, IProtocol protocol) {
        this.host = host;
        this.port = port;
        this.protocol = protocol;
        this.everConnected = false;

    }

    @Override
    public void start(Consumer<INettyClient> consumer, Consumer<Throwable> error) {

        MultithreadEventLoopGroup workerGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();

        try {
            try {
                MultithreadEventLoopGroup eventLoopGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workerGroup);
                bootstrap.channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class);
                bootstrap.handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                            .addLast(new NettyPacketLengthDeserializer())
                            .addLast(new PacketDecoder())
                            .addLast(new NettyPacketLengthSerializer())
                            .addLast(new PacketEncoder())
                            .addLast(new NetworkHandler(SimpleNettyClient.this));

                    }
                });
                this.channelFuture = bootstrap.connect(host, port).addListener((ChannelFutureListener) channelFuture -> {
                    if (!channelFuture.isSuccess()) {
                        Throwable cause = channelFuture.cause();
                        //Connection refused
                        if (error != null) {
                            error.accept(cause);
                        }
                    } else {
                        everConnected = true;
                        if (consumer != null) {
                            Scheduler.runtimeScheduler().async().schedule(() -> consumer.accept(SimpleNettyClient.this));
                        }
                    }
                });
                this.channel = channelFuture.channel();
                ChannelFuture closeFuture = channel.closeFuture();
                closeFuture.sync();

            } catch (Exception exc) {
                exc.printStackTrace();
            }
        } finally {
            if (everConnected) {
                PoloLogger.print(LogLevel.WARNING, "§7The §bNetty-Thread §7has stopped! Shutting down §3PoloCloud-" + PoloCloudAPI.getInstance().getType().getName() + "§7...");
                PoloCloudAPI.getInstance().terminate();
            }
        }
    }

    @Override
    public void start() {
        this.start(null, Throwable::printStackTrace);
    }

    @Override
    public boolean terminate() {
        return channel == null || channel.close().isSuccess();
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public InetSocketAddress getConnectedAddress() {
        return new InetSocketAddress(this.host, this.port);
    }

    @Override
    public void sendPacket(Packet packet) {
        if (ctx() == null) {
            Scheduler.runtimeScheduler().schedule(() -> sendPacket(packet), () -> ctx() != null);
            return;
        }
        ctx().writeAndFlush(packet).addListener(PoloHelper.getChannelFutureListener(SimpleNettyClient.class));
    }

    @Override
    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public IProtocol getProtocol() {
        return this.protocol;
    }

    @Override
    public ChannelHandlerContext ctx() {
        return ctx;
    }
}

