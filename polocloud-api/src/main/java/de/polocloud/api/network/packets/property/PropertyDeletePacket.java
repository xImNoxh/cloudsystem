package de.polocloud.api.network.packets.property;

import de.polocloud.api.network.protocol.packet.base.json.PacketSerializable;
import de.polocloud.api.network.protocol.packet.base.json.SimplePacket;

import java.util.UUID;

public class PropertyDeletePacket extends SimplePacket {

    @PacketSerializable
    private final UUID uniqueId;

    @PacketSerializable
    private final String name;

    public PropertyDeletePacket(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
    }


    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }
}
