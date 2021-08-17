package de.polocloud.api.network.protocol.packet.master;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class MasterLoginResponsePacket extends Packet {

    private boolean response;
    private String message;

    public MasterLoginResponsePacket() {
    }

    public MasterLoginResponsePacket(boolean response, String message) {
        this.response = response;
        this.message = message;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeBoolean(this.response);

        buf.writeString(this.message);

    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        this.response = buf.readBoolean();

        this.message = buf.readString();

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }

}
