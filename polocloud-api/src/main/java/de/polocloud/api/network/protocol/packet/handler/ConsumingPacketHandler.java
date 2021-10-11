package de.polocloud.api.network.protocol.packet.handler;

import de.polocloud.api.network.protocol.packet.base.Packet;
import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;

import java.util.function.Consumer;

@AllArgsConstructor
public class ConsumingPacketHandler<T extends Packet> implements IPacketHandler<T> {

    private final Class<T> typeClass;
    private final Consumer<T> consumer;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, T packet) {
        this.consumer.accept(packet);
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return typeClass;
    }
}
