package de.polocloud.api.network.protocol.packet.handler;

import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.event.channel.ChannelActiveEvent;
import de.polocloud.api.event.channel.ChannelInactiveEvent;
import de.polocloud.api.event.netty.NettyExceptionEvent;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NetworkHandler extends SimpleChannelInboundHandler<Packet> {

    private IProtocol protocol;

    private ChannelHandlerContext channelHandlerContext;

    public NetworkHandler(IProtocol protocol) {
        this.protocol = protocol;
    }

    public void setProtocol(IProtocol protocol) {
        this.protocol = protocol;
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
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        EventRegistry.fireEvent(new ChannelInactiveEvent(ctx));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet o) throws Exception {
        protocol.firePacketHandlers(channelHandlerContext, o);
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }
}
