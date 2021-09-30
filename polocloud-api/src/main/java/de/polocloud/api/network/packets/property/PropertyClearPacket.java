package de.polocloud.api.network.packets.property;

import de.polocloud.api.common.AutoRegistry;
import de.polocloud.api.network.protocol.packet.base.json.PacketSerializable;
import de.polocloud.api.network.protocol.packet.base.json.SimplePacket;
import de.polocloud.api.property.IProperty;
import de.polocloud.api.property.def.SimpleProperty;

import java.util.UUID;

@AutoRegistry
public class PropertyClearPacket extends SimplePacket {

    @PacketSerializable
    private final UUID uniqueId;

    public PropertyClearPacket(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }


    public UUID getUniqueId() {
        return uniqueId;
    }
}
