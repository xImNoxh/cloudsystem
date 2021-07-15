package de.polocloud.api.network.client;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.polocloud.api.event.ChannelActiveEvent;
import de.polocloud.api.event.CloudEvent;
import de.polocloud.api.event.EventHandler;
import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.network.protocol.packet.PacketRegistry;
import de.polocloud.api.network.protocol.packet.handler.*;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.ArrayList;
import java.util.List;

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


    public SimpleNettyClient() {

    }

    public SimpleNettyClient(String host, int port, IProtocol protocol) {
        this.host = host;
        this.port = port;
        this.protocol = protocol;


    }

    private NetworkHandler networkHandler;

    @Override
    public void start() {
        PacketRegistry.registerDefaultPackets();

        networkHandler = new NetworkHandler(protocol);

        MultithreadEventLoopGroup workerGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();

        try {
            try {
                MultithreadEventLoopGroup eventLoopGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
                Bootstrap bootstrap = new Bootstrap();
                ((bootstrap.group(workerGroup)).channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)).handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                            .addLast(new NettyPacketLengthDeserializer())
                            .addLast(new PacketDecoder())
                            .addLast(new NettyPacketLengthSerializer())
                            .addLast(new PacketEncoder())
                            .addLast(networkHandler);

                    }
                });
                this.channelFuture = bootstrap.connect(host, port);
                this.channel = this.channelFuture.channel();
                ChannelFuture closeFuture = this.channel.closeFuture();
                closeFuture.sync();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        } finally {
            System.out.println("Netty thread stopped.");
        }


    }

    @Override
    public boolean terminate() {
        channel.close();
        return true;
    }

    @Override
    public void sendPacket(Packet packet) {
        if (networkHandler.getChannelHandlerContext() == null) {
            return;
        }
        networkHandler.getChannelHandlerContext().writeAndFlush(packet);
    }


    @Override
    public IProtocol getProtocol() {
        return this.protocol;
    }
}

