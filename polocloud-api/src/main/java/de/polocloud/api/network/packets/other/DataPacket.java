package de.polocloud.api.network.packets.other;

import de.polocloud.api.common.AutoRegistry;
import de.polocloud.api.network.protocol.packet.base.json.PacketSerializable;
import de.polocloud.api.network.protocol.packet.base.json.SimplePacket;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AutoRegistry @AllArgsConstructor @Getter
public class DataPacket<T> extends SimplePacket {

    /**
     * The key of this data
     */
    @PacketSerializable
    private final String key;

    /**
     * The data that is getting passed around
     */
    @PacketSerializable
    private final T data;

}
