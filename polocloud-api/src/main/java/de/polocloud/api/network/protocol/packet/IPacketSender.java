package de.polocloud.api.network.protocol.packet;

import de.polocloud.api.common.PoloType;
import de.polocloud.api.network.protocol.packet.base.Packet;

public interface IPacketSender {

    /**
     * Sends a {@link Packet} to the other connection side
     *
     * If this instance is...
     *
     * 'CLOUD/SERVER' -> Will send to all connected clients
     * 'BRIDGE/SPIGOT/PROXY' -> WIll send to the Master
     *
     * @param packet the de.polocloud.modules.smartproxy.packet to send
     */
    void sendPacket(Packet packet);

    /**
     * Sends a {@link Packet} to only a specified receiver type
     *
     * @param packet the de.polocloud.modules.smartproxy.packet
     * @param receiver the receiver type
     */
    default void sendPacket(Packet packet, PoloType receiver) {}

}
