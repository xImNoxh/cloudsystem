package de.polocloud.api.event.impl.net;

import de.polocloud.api.event.base.CloudEvent;
import de.polocloud.api.event.base.EventData;
import de.polocloud.api.network.protocol.packet.base.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
@EventData(nettyFire = false)
public class PacketReceiveEvent extends CloudEvent {

    /**
     * The received event
     */
    private final Packet packet;
}
