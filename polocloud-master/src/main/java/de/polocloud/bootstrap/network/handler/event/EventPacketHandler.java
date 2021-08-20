package de.polocloud.bootstrap.network.handler.event;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.network.protocol.packet.api.EventPacket;
import de.polocloud.bootstrap.network.SimplePacketHandler;

public class EventPacketHandler {

    public EventPacketHandler() {
        new SimplePacketHandler<>(EventPacket.class, eventPacket -> {

            if (!eventPacket.getExcept().equalsIgnoreCase("null") && eventPacket.getExcept().equalsIgnoreCase("cloud")) {
                return;
            }

            PoloCloudAPI.getInstance().getEventManager().fireEvent(eventPacket.getEvent());
        });
    }
}
