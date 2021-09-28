package de.polocloud.wrapper.impl.handler;

import de.polocloud.api.network.packets.wrapper.WrapperRequestUnusedMemory;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.wrapper.Wrapper;
import io.netty.channel.ChannelHandlerContext;

public class WrapperHandlerMasterRequestUnusedMemory implements IPacketHandler<Packet> {

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {

        WrapperRequestUnusedMemory packet = (WrapperRequestUnusedMemory) obj;
        if (packet.getName().equalsIgnoreCase(Wrapper.getInstance().getName())) {
            packet.respond(data -> data.append("unused", Wrapper.getInstance().getUnusedMemory()));
        }
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return WrapperRequestUnusedMemory.class;
    }
}
