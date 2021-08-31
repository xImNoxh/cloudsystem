package de.polocloud.wrapper.impl.handler;

import de.polocloud.api.network.packets.other.FileTransferPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import io.netty.channel.ChannelHandlerContext;

public class WrapperHandlerFileTransfer implements IPacketHandler<Packet> {

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet packet) {
        FileTransferPacket fileTransferPacket = (FileTransferPacket) packet;
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return FileTransferPacket.class;
    }
}
