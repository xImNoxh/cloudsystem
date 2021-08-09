package de.polocloud.plugin.protocol.register;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.plugin.CloudPlugin;
import io.netty.channel.ChannelHandlerContext;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SimplePacketRegister<T extends Packet> {

    public SimplePacketRegister(Class<? extends Packet> clazz, Consumer<T> packet) {
        CloudPlugin.getCloudPluginInstance().getNetworkClient().registerPacketHandler(new IPacketHandler<Packet>() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
                packet.accept((T) obj);
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return clazz;
            }
        });
    }

    public SimplePacketRegister(Class<? extends Packet> clazz, BiConsumer<ChannelHandlerContext, T> packet) {
        CloudPlugin.getCloudPluginInstance().getNetworkClient().registerPacketHandler(new IPacketHandler<Packet>() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
                packet.accept(ctx, (T) obj);
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return clazz;
            }
        });
    }


}
