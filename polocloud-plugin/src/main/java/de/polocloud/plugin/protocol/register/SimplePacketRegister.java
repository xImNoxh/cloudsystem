package de.polocloud.plugin.protocol.register;

import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.plugin.CloudPlugin;
import io.netty.channel.ChannelHandlerContext;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SimplePacketRegister<T extends Packet> {

    public SimplePacketRegister(Class<? extends Packet> clazz, Consumer<T> consumer) {
        CloudPlugin.getCloudPluginInstance().getNetworkClient().registerPacketHandler(new IPacketHandler<Packet>() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Packet packet) {
                consumer.accept((T) packet);
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return clazz;
            }
        });
    }

    public SimplePacketRegister(Class<? extends Packet> clazz, BiConsumer<ChannelHandlerContext, T> consumer) {
        CloudPlugin.getCloudPluginInstance().getNetworkClient().registerPacketHandler(new IPacketHandler<Packet>() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Packet packet) {
                consumer.accept(ctx, (T) packet);
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return clazz;
            }
        });
    }


}
