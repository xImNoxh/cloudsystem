package de.polocloud.bootstrap.network.handler.property;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.network.packets.property.PropertyDeletePacket;
import de.polocloud.api.network.packets.property.PropertyInsertPacket;
import de.polocloud.api.property.IProperty;
import de.polocloud.api.property.IPropertyManager;
import de.polocloud.bootstrap.network.SimplePacketHandler;

import java.util.UUID;

public class PropertyPacketHandler {

    public PropertyPacketHandler() {

        IPropertyManager propertyManager = PoloCloudAPI.getInstance().getPropertyManager();

        new SimplePacketHandler<>(PropertyInsertPacket.class, packet -> {

            IProperty property = packet.getProperty();
            UUID uniqueId = packet.getUniqueId();

            propertyManager.insertProperty(uniqueId, iProperty -> iProperty.copyFrom(property));
        });

        new SimplePacketHandler<>(PropertyDeletePacket.class, packet -> {

            String property = packet.getName();
            UUID uniqueId = packet.getUniqueId();

            propertyManager.deleteProperty(uniqueId, property);
        });

    }
}
