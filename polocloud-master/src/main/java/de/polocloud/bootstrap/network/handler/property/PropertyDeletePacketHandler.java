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

public class PropertyDeletePacketHandler implements IPacketHandler<PropertyDeletePacket> {


    @Override
    public void handlePacket(ChannelHandlerContext ctx, PropertyDeletePacket packet) {
        String property = packet.getName();
        UUID uniqueId = packet.getUniqueId();

        PoloCloudAPI.getInstance().getPropertyManager().deleteProperty(uniqueId, property);
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return PropertyDeletePacket.class;
    }
}
