package de.polocloud.api.network.packets.other;

import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.SimpleCloudPlayer;
import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.packet.base.json.PacketSerializable;
import de.polocloud.api.network.protocol.packet.base.json.SimplePacket;

import java.util.List;

@AutoRegistry
public class TextPacket extends SimplePacket {

    @PacketSerializable(String.class)
    private final String text;

    public TextPacket(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
