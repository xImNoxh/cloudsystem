package de.polocloud.api.network.protocol.packet.handler;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.impl.net.ChannelActiveEvent;
import de.polocloud.api.event.impl.net.ChannelInactiveEvent;
import de.polocloud.api.event.impl.net.NettyExceptionEvent;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.scheduler.Scheduler;
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

        PoloCloudAPI.getInstance().getEventManager().fireEvent(new NettyExceptionEvent(cause), nettyExceptionEvent -> {
            if (nettyExceptionEvent.isShouldThrow()) {
                cause.printStackTrace();
            }
        });
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.networkConnection.setCtx((this.channelHandlerContext = ctx));

        //TODO in Spigot not called
        PoloCloudAPI.getInstance().getEventManager().fireEvent(new ChannelActiveEvent(ctx));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        this.networkConnection.setCtx((this.channelHandlerContext = ctx));

        PoloCloudAPI.getInstance().getEventManager().fireEvent(new ChannelInactiveEvent(ctx));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        Scheduler.runtimeScheduler().schedule(() -> networkConnection.getProtocol().firePacketHandlers(channelHandlerContext, packet));
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }
}
