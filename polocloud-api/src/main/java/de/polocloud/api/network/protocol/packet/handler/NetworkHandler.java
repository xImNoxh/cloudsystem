package de.polocloud.api.network.protocol.packet.handler;

import de.polocloud.api.event.ChannelActiveEvent;
import de.polocloud.api.event.ChannelInactiveEvent;
import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.packet.IPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NetworkHandler extends SimpleChannelInboundHandler<Object> {

    private IProtocol protocol;

    private ChannelHandlerContext channelHandlerContext;


    public NetworkHandler(IProtocol protocol) {
        this.protocol = protocol;
    }

    public void setProtocol(IProtocol protocol) {
        this.protocol = protocol;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channelHandlerContext = ctx;
        System.out.println("connected!");
        EventRegistry.fireEvent(new ChannelActiveEvent(ctx));

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        EventRegistry.fireEvent(new ChannelInactiveEvent(ctx));

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        if (o instanceof IPacket) {
            protocol.firePacketHandlers(channelHandlerContext, (IPacket) o);
        }
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }
}
