package de.polocloud.bootstrap.network.handler.property;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.network.packets.property.PropertyDeletePacket;
import de.polocloud.api.network.packets.property.PropertyInsertPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.api.property.IProperty;
import de.polocloud.api.property.IPropertyManager;
import de.polocloud.bootstrap.network.SimplePacketHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class PropertyInsertPacketHandler implements IPacketHandler<PropertyInsertPacket> {


    @Override
    public void handlePacket(ChannelHandlerContext ctx, PropertyInsertPacket packet) {
        IProperty property = packet.getProperty();
        UUID uniqueId = packet.getUniqueId();

        PoloCloudAPI.getInstance().getPropertyManager().insertProperty(uniqueId, iProperty -> iProperty.copyFrom(property));
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return PropertyInsertPacket.class;
    }
}
