package de.polocloud.api.messaging.def;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.messaging.IMessageChannel;
import de.polocloud.api.messaging.IMessageListener;
import de.polocloud.api.network.packets.other.ChannelMessagePacket;
import de.polocloud.api.network.protocol.packet.IPacketReceiver;
import de.polocloud.api.util.other.Snowflake;

import java.util.ArrayList;
import java.util.List;

public class SimpleMessageChannel<T> implements IMessageChannel<T> {

    /**
     * The name of the channel
     */
    private final String name;

    /**
     * The snowflake of this channel
     */
    private final long snowflake;

    /**
     * The listeners
     */
    private final List<IMessageListener<T>> listeners;

    public SimpleMessageChannel(String name) {
        this.name = name;
        this.listeners = new ArrayList<>();
        this.snowflake = Snowflake.getInstance().nextId();
    }

    @Override
    public long getSnowflake() {
        return snowflake;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void sendMessage(T t) {
        ChannelMessagePacket<T> channelMessagePacket = new ChannelMessagePacket<>(this.name, t);
        PoloCloudAPI.getInstance().sendPacket(channelMessagePacket);
    }

    @Override
    public void sendMessage(T t, IPacketReceiver... receiver) {
        ChannelMessagePacket<T> channelMessagePacket = new ChannelMessagePacket<>(this.name, t);
        for (IPacketReceiver iPacketReceiver : receiver) {
            iPacketReceiver.receivePacket(channelMessagePacket);
        }
    }

    @Override
    public void unregisterAll() {
        this.listeners.clear();
    }

    @Override
    public void registerListener(IMessageListener<T> listener) {
        this.listeners.add(listener);
    }

    public List<IMessageListener<T>> getListeners() {
        return listeners;
    }
}
