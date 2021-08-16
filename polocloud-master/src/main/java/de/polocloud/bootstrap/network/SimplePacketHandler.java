package de.polocloud.bootstrap.network;

import com.google.common.collect.Lists;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SimplePacketHandler<T extends Packet> extends IPacketHandler<Packet> {

    public static List<SimplePacketHandler<?>> LISTENING = Lists.newArrayList();
    private Class<? extends Packet> packet;
    private BiConsumer<ChannelHandlerContext, T> actions;
    private Consumer<T> action;

    public SimplePacketHandler(Class<? extends Packet> packet, BiConsumer<ChannelHandlerContext, T> actions) {
        this.packet = packet;
        this.actions = actions;
        LISTENING.add(this);
    }

    public SimplePacketHandler(Class<? extends Packet> packet, Consumer<T> action) {
        this.packet = packet;
        this.action = action;
        LISTENING.add(this);
    }

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        packet = obj.getClass();

        if (action != null) action.accept((T) obj);
        if (actions != null) actions.accept(ctx, (T) obj);
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return packet;
    }

    public BiConsumer<ChannelHandlerContext, T> getActions() {
        return actions;
    }

    public Class<? extends Packet> getPacket() {
        return packet;
    }

    public Consumer<T> getAction() {
        return action;
    }

}
