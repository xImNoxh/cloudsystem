package de.polocloud.api.network.helper;

import de.polocloud.api.network.protocol.ProtocolState;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.net.InetSocketAddress;

public interface IConnection {

    InetSocketAddress getAddress();

    InetSocketAddress getBoundAddress();

    void sendPacket(ProtocolState state, Packet packet);

    void close() throws Exception;
}
