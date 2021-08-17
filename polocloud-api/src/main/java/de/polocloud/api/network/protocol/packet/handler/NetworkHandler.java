package de.polocloud.api.network.protocol.packet.handler;

import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.event.channel.ChannelActiveEvent;
import de.polocloud.api.event.channel.ChannelInactiveEvent;
import de.polocloud.api.event.netty.NettyExceptionEvent;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.server.SimpleNettyServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

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

        if (this.networkConnection instanceof SimpleNettyServer) {
            SimpleNettyServer simpleNettyServer = (SimpleNettyServer)networkConnection;
            simpleNettyServer.getConnectedClients().add(ctx.channel());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        EventRegistry.fireEvent(new ChannelInactiveEvent(ctx));

        if (this.networkConnection instanceof SimpleNettyServer) {
            SimpleNettyServer simpleNettyServer = (SimpleNettyServer)networkConnection;
            simpleNettyServer.getConnectedClients().removeIf(channel -> channel.equals(ctx.channel()));
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet o) throws Exception {
        networkConnection.getProtocol().firePacketHandlers(channelHandlerContext, o);
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }
}
