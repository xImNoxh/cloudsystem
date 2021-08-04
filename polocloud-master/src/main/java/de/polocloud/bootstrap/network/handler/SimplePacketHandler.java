package de.polocloud.bootstrap.network.handler;

import com.google.common.collect.Lists;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.function.BiConsumer;

public class SimplePacketHandler extends IPacketHandler<Packet> {

    private Class<? extends Packet> packet;
    private BiConsumer<ChannelHandlerContext, Packet> handle;

    public static List<SimplePacketHandler> handlers = Lists.newArrayList();

    public SimplePacketHandler(Class<? extends Packet> packet, BiConsumer<ChannelHandlerContext, Packet> handle) {
        this.packet = packet;
        this.handle = handle;
    }

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        handle.accept(ctx, obj);
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return packet;
    }
}
