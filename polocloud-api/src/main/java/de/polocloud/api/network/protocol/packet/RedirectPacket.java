package de.polocloud.api.network.protocol.packet;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class RedirectPacket extends Packet{

    private long snowflake;
    private Packet packet;

    public RedirectPacket(){

    }

    public RedirectPacket(long snowflake, Packet packet) {
        this.snowflake = snowflake;
        this.packet = packet;
    }


    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        byteBuf.writeLong(snowflake);

        int packetId = PacketRegistry.getPacketId(packet.getClass());
        byteBuf.writeInt(packetId);
        packet.write(byteBuf);

    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        snowflake = byteBuf.readLong();

        int id = byteBuf.readInt();
        try {
            packet = PacketRegistry.createInstance(id);

            packet.read(byteBuf);

        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }


    }

    public Packet getPacket() {
        return packet;
    }

    public long getSnowflake() {
        return snowflake;
    }
}
