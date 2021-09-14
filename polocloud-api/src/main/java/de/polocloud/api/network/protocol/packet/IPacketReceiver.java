package de.polocloud.api.network.protocol.packet;

import de.polocloud.api.common.INamable;
import de.polocloud.api.network.protocol.packet.base.Packet;

public interface IPacketReceiver extends INamable {

    /**
     * Makes this receiver receive a certain {@link Packet}
     * By simply flushing this de.polocloud.modules.smartproxy.packet into its channel or context
     *
     * @param packet the de.polocloud.modules.smartproxy.packet
     */
    void receivePacket(Packet packet);
}
