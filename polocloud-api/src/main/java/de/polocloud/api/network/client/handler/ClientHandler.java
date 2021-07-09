package de.polocloud.api.network.client.handler;

import com.google.inject.Inject;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.packet.IPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<Object> {

    private IProtocol protocol;

    private ChannelHandlerContext channelHandlerContext;

    public ClientHandler(IProtocol protocol) {
        this.protocol = protocol;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channelHandlerContext = ctx;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        System.out.println("< " + o.getClass().getSimpleName());
        if (o instanceof IPacket) {
            protocol.firePacketHandlers(channelHandlerContext, (IPacket) o);
        }

    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }
}
