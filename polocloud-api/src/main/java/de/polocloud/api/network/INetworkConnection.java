package de.polocloud.api.network;

import de.polocloud.api.network.helper.IStartable;
import de.polocloud.api.network.helper.ITerminatable;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.packet.IPacketSender;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

public interface INetworkConnection extends IStartable, ITerminatable, IPacketSender {

    /**
     * The {@link IProtocol} instance of this connection
     * to manage {@link IPacketHandler}s
     *
     * @return the protocol instance
     */
    IProtocol getProtocol();

    /**
     * Checks if the current connection is connected
     */
    boolean isConnected();

    /**
     * The netty {@link Channel} of this connection
     *
     * @return netty channel instance
     */
    Channel getChannel();

    /**
     * The netty {@link ChannelHandlerContext} of this connection
     *
     * @return netty channel-context instance
     */
    ChannelHandlerContext ctx();

    /**
     * The netty {@link ChannelHandlerContext} of this connection
     */
    void setCtx(ChannelHandlerContext ctx);

    /**
     * Gets the address the connection is bound or connected to
     * If...
     * 'SERVER/CLOUD' -> Will return the localhost address
     * 'BRIDGE/SPIGOT/PROXY' -> Will return the Master-Address
     *
     * @return address as {@link InetSocketAddress}
     */
    InetSocketAddress getConnectedAddress();
}
