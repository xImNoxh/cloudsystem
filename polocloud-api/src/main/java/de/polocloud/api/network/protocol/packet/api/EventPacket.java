package de.polocloud.api.network.protocol.packet.api;

import de.polocloud.api.config.JsonData;
import de.polocloud.api.event.base.IEvent;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.util.PoloUtils;

import java.io.IOException;

public class EventPacket extends Packet {

    /**
     * The cloud event
     */
    private IEvent event;

    /**
     * Who should not receive the event
     */
    private String except;

    public EventPacket(IEvent event, String except) {
        this.event = event;
        this.except = except;
    }

    @Override
    public void read(IPacketBuffer buffer) throws IOException {
        try {
            this.except = buffer.readString();
            String cl = buffer.readString();

            Class<?> eventClass = Class.forName(cl);

            try {
                this.event = (IEvent) eventClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                this.event = (IEvent) PoloUtils.getInstance(eventClass);
            }
            if (this.event != null) {
                this.event.read(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void write(IPacketBuffer buffer) throws IOException {

        buffer.writeString(except);
        buffer.writeString(event.getClass().getName());
        event.write(buffer);
    }

    public IEvent getEvent() {
        return event;
    }

    public String getExcept() {
        return except;
    }
}
