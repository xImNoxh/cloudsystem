package de.polocloud.api.network.packets.api;

import de.polocloud.api.common.PoloType;
import de.polocloud.api.event.base.CloudEvent;
import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;
import java.util.Arrays;

@AutoRegistry//(id = 0x11)
public class EventPacket extends Packet {

    /**
     * The cloud event
     */
    private CloudEvent event;

    /**
     * Who should not receive the event
     */
    private String except;

    /**
     * The ignored types
     */
    private PoloType[] ignoredTypes;

    /**
     * If handled async
     */
    private boolean async;

    public EventPacket(CloudEvent event, String except, PoloType[] ignoredTypes, boolean async) {
        this.event = event;
        this.except = except;
        this.ignoredTypes = ignoredTypes;
        this.async = async;
    }

    @Override
    public void read(IPacketBuffer buffer) throws IOException {

        try {
            this.async = buffer.readBoolean();
            this.except = buffer.readString();

            int size = buffer.readInt();
            this.ignoredTypes = new PoloType[size];
            for (int i = 0; i < size; i++) {
                this.ignoredTypes[i] = buffer.readEnum();
            }


            String cl = buffer.readString();

            Class<?> eventClass = Class.forName(cl);
            event = (CloudEvent) buffer.readCustom(eventClass);
            event.setNettyFired(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void write(IPacketBuffer buffer) throws IOException {

        buffer.writeBoolean(async); //If async
        buffer.writeString(except); //The ignored receiver

        //Ignored types
        buffer.writeInt(ignoredTypes.length);
        for (PoloType ignoredType : ignoredTypes) {
            buffer.writeEnum(ignoredType);
        }

        //The event in buffer
        buffer.writeString(event.getClass().getName());
        buffer.writeCustom(event);

    }

    @Override
    public String toString() {
        return "EventPacket{" +
            "event=" + event +
            ", except='" + except + '\'' +
            ", ignoredTypes=" + Arrays.toString(ignoredTypes) +
            ", async=" + async +
            '}';
    }

    public CloudEvent getEvent() {
        return event;
    }

    public PoloType[] getIgnoredTypes() {
        return ignoredTypes;
    }

    public boolean isAsync() {
        return async;
    }

    public String getExcept() {
        return except;
    }
}
