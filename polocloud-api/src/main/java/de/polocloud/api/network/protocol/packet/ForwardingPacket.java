package de.polocloud.api.network.protocol.packet;

import de.polocloud.api.common.PoloType;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;

import java.io.IOException;

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

        int packetId = PacketRegistry.getPacketId(packet.getClass());
        buf.writeInt(packetId);
        packet.write(buf);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        type = buf.readEnum();
        receiver = buf.readString();
        int id = buf.readInt();
        packet = PacketRegistry.createPacket(id);
        packet.read(buf);
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
