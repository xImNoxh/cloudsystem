package de.polocloud.api.network.server;

import com.google.inject.Inject;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.PacketRegistry;
import de.polocloud.api.network.protocol.packet.handler.*;
import de.polocloud.api.network.request.SimpleRequestManager;
import de.polocloud.api.network.request.IRequestManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import javax.inject.Named;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

public class SimpleNettyServer implements INettyServer {


    /**
     * The port of this server to run on
     */
    @Inject @Named(value = "setting_server_start_port") private int port;

    /**
     * The protocol instance for managing handlers
     */
    @Inject private IProtocol protocol;

    /**
     * All connected clients
     */
    private final List<Channel> connectedClients;

    /**
     * The request manager
     */
    private final IRequestManager requestManager;

    public SimpleNettyServer() {
        this.connectedClients = new LinkedList<>();
        this.requestManager = new SimpleRequestManager(this);
    }

    @Override
    public void start() {
        PacketRegistry.registerDefaultInternalPackets();
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
            ChannelFuture channelFuture = serverBootstrap.bind(this.port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        System.out.println("Starting new Server on port » " + this.port);
    }

    @Override
    public boolean terminate() {
        return true;
    }

    @Override
    public List<Channel> getConnectedClients() {
        return connectedClients;
    }

    @Override
    public IRequestManager getRequestManager() {
        return this.requestManager;
    }

    @Override
    public IProtocol getProtocol() {
        return protocol;
    }

    @Override
    public InetSocketAddress getConnectedAddress() {
        return new InetSocketAddress(this.port);
    }

    @Override
    public void sendPacket(Packet packet) {
        for (Channel connectedClient : this.connectedClients) {
            connectedClient.writeAndFlush(packet);
        }
    }
}
