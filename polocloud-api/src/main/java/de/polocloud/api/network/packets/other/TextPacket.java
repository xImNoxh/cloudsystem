package de.polocloud.api.network.packets.other;

import de.polocloud.api.common.AutoRegistry;
import de.polocloud.api.network.protocol.packet.base.json.PacketSerializable;
import de.polocloud.api.network.protocol.packet.base.json.SimplePacket;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AutoRegistry @Getter @AllArgsConstructor
public class TextPacket extends SimplePacket {

    @PacketSerializable(String.class)
    private final String text;



}
