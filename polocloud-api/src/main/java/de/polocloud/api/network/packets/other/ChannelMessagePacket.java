package de.polocloud.api.network.packets.other;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.common.AutoRegistry;
import de.polocloud.api.util.other.WrappedObject;

import java.io.IOException;

@AutoRegistry
public class ChannelMessagePacket<T> extends Packet {

    private String channel;

    private long startTime;

    private String className;

    private WrappedObject<T> wrappedObject;

    public ChannelMessagePacket(String channel, T providedObject) {
        this.channel = channel;
        this.wrappedObject = new WrappedObject<>(providedObject);
        this.className = providedObject == null ? "null" : providedObject.getClass().getName();
        this.startTime = System.currentTimeMillis();
    }

    public String getChannel() {
        return channel;
    }

    public long getStartTime() {
        return startTime;
    }

    public WrappedObject<T> getWrappedObject() {
        return wrappedObject;
    }

    public T getProvidedObject() {
        try {
            Class<T> tClass = (Class<T>) Class.forName(className);
            return wrappedObject.unwrap(tClass);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }



    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(channel);
        buf.writeString(className);
        buf.writeString(wrappedObject.toString());
        buf.writeLong(startTime);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        channel = buf.readString();
        className = buf.readString();
        wrappedObject = new WrappedObject<>(buf.readString(), className);
        startTime = buf.readLong();
    }
}
