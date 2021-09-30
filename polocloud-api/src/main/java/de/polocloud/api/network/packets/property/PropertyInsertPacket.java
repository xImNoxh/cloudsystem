package de.polocloud.api.network.packets.property;

import de.polocloud.api.common.AutoRegistry;
import de.polocloud.api.network.protocol.packet.base.json.PacketSerializable;
import de.polocloud.api.network.protocol.packet.base.json.SimplePacket;
import de.polocloud.api.property.IProperty;
import de.polocloud.api.property.def.SimpleProperty;

import java.util.UUID;

@AutoRegistry
public class PropertyInsertPacket extends SimplePacket {

    @PacketSerializable(SimpleProperty.class)
    private final IProperty property;

    @PacketSerializable
    private final UUID uniqueId;

    public PropertyInsertPacket(IProperty property, UUID uniqueId) {
        this.property = property;
        this.uniqueId = uniqueId;
    }

    public IProperty getProperty() {
        return property;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }
}
