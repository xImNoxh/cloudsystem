package de.polocloud.api.network.client;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.PacketRegistry;
import de.polocloud.api.network.protocol.packet.handler.*;
import de.polocloud.api.network.request.SimpleRequestManager;
import de.polocloud.api.network.request.IRequestManager;
import de.polocloud.api.scheduler.Scheduler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

public class SimpleNettyClient implements INettyClient {

    @Inject
    @Named("setting_client_host")
    private String host;

    @Inject
    @Named("setting_client_port")
    private int port;

    @Inject
    private IProtocol protocol;

    private ChannelFuture channelFuture;
    private Channel channel;
    private final IRequestManager requestManager = new SimpleRequestManager(this);
    private ChannelHandlerContext ctx;

    public SimpleNettyClient() {

    }

    public SimpleNettyClient(String host, int port, IProtocol protocol) {
        this.host = host;
        this.port = port;
        this.protocol = protocol;
    }

    public void start(Consumer<SimpleNettyClient> consumer) {
        PacketRegistry.registerDefaultInternalPackets();

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
                    if (consumer != null) {
                        Scheduler.runtimeScheduler().schedule(() -> consumer.accept(SimpleNettyClient.this));
                    }
                    if (!channelFuture.isSuccess()) {
                        channelFuture.cause().printStackTrace();
                    }
                });
                this.channel = channelFuture.channel();
                ChannelFuture closeFuture = channel.closeFuture();
                closeFuture.sync();

            } catch (Exception exc) {
                exc.printStackTrace();
            }
        } finally {
            System.out.println("Netty thread stopped.");
        }
    }
    @Override
    public void start() {
        this.start(null);
    }

    @Override
    public boolean terminate() {
        channel.close();
        return true;
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
        ctx().writeAndFlush(packet).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (!channelFuture.isSuccess()) {
                    channelFuture.cause().printStackTrace();
                }
            }
        });
    }

    @Override
    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public IRequestManager getRequestManager() {
        return this.requestManager;
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

