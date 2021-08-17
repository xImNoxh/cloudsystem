package de.polocloud.api.network.protocol.packet;

public interface IPacketSender {

    /**
     * Sends a {@link Packet} to the other connection side
     *
     * If this instance is...
     *
     * 'CLOUD/SERVER' -> Will send to all connected clients
     * 'BRIDGE/SPIGOT/PROXY' -> WIll send to the Master
     *
     * @param packet the packet to send
     */
    void sendPacket(Packet packet);

}
