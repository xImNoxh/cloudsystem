package de.polocloud.plugin.protocol.register;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.plugin.protocol.NetworkClient;
import io.netty.channel.ChannelHandlerContext;

import java.util.function.BiConsumer;

public abstract class Register {

    private NetworkClient networkClient;

    public Register(NetworkClient networkClient) {

        System.out.println("networkClient");
        System.out.println(networkClient == null);
        this.networkClient = networkClient;
    }

    public Register register(BiConsumer<ChannelHandlerContext, Packet> input, Class<? extends Packet> cl) {
        networkClient.registerPacketHandler(new IPacketHandler<Packet>() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
                input.accept(ctx, obj);
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return cl;
            }
        });
        return this;
    }

}
