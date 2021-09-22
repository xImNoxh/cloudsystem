package de.polocloud.wrapper.impl.handler;

import de.polocloud.api.network.packets.wrapper.WrapperRequestCPUUsage;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.wrapper.Wrapper;
import io.netty.channel.ChannelHandlerContext;

public class WrapperHandlerMasterRequestCPU implements IPacketHandler<Packet> {

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {

        WrapperRequestCPUUsage packet = (WrapperRequestCPUUsage) obj;
        if (packet.getName().equalsIgnoreCase(Wrapper.getInstance().getName())) {
            packet.respond(data -> data.append("cpu", Wrapper.getInstance().getCpuUsage()));
        }
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return WrapperRequestCPUUsage.class;
    }
}
