package de.polocloud.api.network.packets.other;

import de.polocloud.api.network.protocol.packet.base.json.PacketSerializable;
import de.polocloud.api.network.protocol.packet.base.json.SimplePacket;
import de.polocloud.api.util.AutoRegistry;

@AutoRegistry
public class ChannelMessagePacket<T> extends SimplePacket {

    @PacketSerializable
    private final String channel;

    @PacketSerializable
    private final long startTime;

    @PacketSerializable
    private final T providedObject;

    public ChannelMessagePacket(String channel, T providedObject) {
        this.channel = channel;
        this.providedObject = providedObject;
        this.startTime = System.currentTimeMillis();
    }

    public String getChannel() {
        return channel;
    }

    public long getStartTime() {
        return startTime;
    }

    public T getProvidedObject() {
        return providedObject;
    }
}
