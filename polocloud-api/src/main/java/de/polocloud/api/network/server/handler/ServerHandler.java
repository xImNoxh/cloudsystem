package de.polocloud.api.network.server.handler;

import com.google.inject.Inject;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.packet.IPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends SimpleChannelInboundHandler<Object> {

    @Inject
    private IProtocol protocol;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("new channel active");
        ctx.writeAndFlush("Hallo Welt");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext chx, Object object) throws Exception {
        System.out.println("< " + object.getClass().getSimpleName());

        if (object instanceof IPacket) {
            protocol.firePacketHandlers(chx, (IPacket) object);
        }

    }
}
