package de.polocloud.api.network.protocol.packet.base.other;

import de.polocloud.api.common.PoloType;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;

import java.io.IOException;

@AutoRegistry
public class ForwardingPacket extends Packet {

    private PoloType type;
    private String receiver;
    private Packet packet;

    public ForwardingPacket() {
        this(PoloType.WRAPPER, "none", null);
    }

    public ForwardingPacket(PoloType type, String receiver, Packet packet) {
        this.receiver = receiver;
        this.type = type;
        this.packet = packet;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeEnum(type);
        buf.writeString(receiver);

        buf.writePacket(this.packet);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        this.type = buf.readEnum();
        this.receiver = buf.readString();
        this.packet = buf.readPacket();
    }

    public Packet getPacket() {
        return packet;
    }

    public PoloType getType() {
        return type;
    }

    public String getReceiver() {
        return receiver;
    }
}
