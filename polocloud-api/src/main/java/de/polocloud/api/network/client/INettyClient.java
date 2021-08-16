package de.polocloud.api.network.client;

import de.polocloud.api.network.IStartable;
import de.polocloud.api.network.ITerminatable;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.packet.IPacketSender;
import io.netty.channel.ChannelHandlerContext;

public interface INettyClient extends IStartable, ITerminatable, IPacketSender {

    IProtocol getProtocol();

    ChannelHandlerContext getCtx();


}
