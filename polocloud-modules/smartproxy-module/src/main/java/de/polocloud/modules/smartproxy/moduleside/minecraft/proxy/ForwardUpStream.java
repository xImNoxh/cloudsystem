package de.polocloud.modules.smartproxy.moduleside.minecraft.proxy;

import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

@Getter @AllArgsConstructor @Setter
public class ForwardUpStream extends SimpleChannelInboundHandler<ByteBuf> {

    /**
     * The channel of this handler
     */
    private Channel channel;

    /**
     * The downstream of this upstream
     */
    private final ForwardDownStream downstreamHandler;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.channel.close();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        this.channel.writeAndFlush(buf.retain());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            return;
        }
        PoloLogger.print(LogLevel.ERROR, "§cProxyUpstreamHandler: Exception was caught:");
        cause.printStackTrace();
    }

}
