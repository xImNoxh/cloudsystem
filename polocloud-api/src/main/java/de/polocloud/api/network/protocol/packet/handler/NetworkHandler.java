package de.polocloud.api.network.protocol.packet.handler;

import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.event.channel.ChannelActiveEvent;
import de.polocloud.api.event.channel.ChannelInactiveEvent;
import de.polocloud.api.event.netty.NettyExceptionEvent;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.server.SimpleNettyServer;
import de.polocloud.api.scheduler.Scheduler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.LinkedList;
import java.util.List;

public class NetworkHandler extends SimpleChannelInboundHandler<Packet> {

    private final INetworkConnection networkConnection;

    private ChannelHandlerContext channelHandlerContext;

    public NetworkHandler(INetworkConnection networkConnection) {
        this.networkConnection = networkConnection;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        NettyExceptionEvent event = new NettyExceptionEvent(cause);
        EventRegistry.fireEvent(event);

        if (event.isShouldThrow()) {
            cause.printStackTrace();
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channelHandlerContext = ctx;

        EventRegistry.fireEvent(new ChannelActiveEvent(ctx));

        networkConnection.setCtx(ctx);
        if (this.networkConnection instanceof SimpleNettyServer) {
            SimpleNettyServer simpleNettyServer = (SimpleNettyServer) networkConnection;
            simpleNettyServer.getConnectedClients().add(ctx.channel());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        EventRegistry.fireEvent(new ChannelInactiveEvent(ctx));

        networkConnection.setCtx(ctx);
        if (this.networkConnection instanceof SimpleNettyServer) {
            SimpleNettyServer simpleNettyServer = (SimpleNettyServer) networkConnection;
            List<Channel> connectedClients = new LinkedList<>(simpleNettyServer.getConnectedClients());
            connectedClients.removeIf(connectedClient -> (connectedClient != null && ctx.channel() != null) && (connectedClient.remoteAddress().equals(ctx.channel().remoteAddress())) && connectedClient.localAddress().equals(ctx.channel().localAddress()));
            simpleNettyServer.setConnectedClients(connectedClients);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet o) throws Exception {
        //TODO: CHECK ASYNC PACKET HANDLING IF ERRORS
        Scheduler.runtimeScheduler().schedule(() -> networkConnection.getProtocol().firePacketHandlers(channelHandlerContext, o));
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }
}
