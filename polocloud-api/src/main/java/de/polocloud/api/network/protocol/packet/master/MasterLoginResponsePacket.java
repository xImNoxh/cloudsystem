package de.polocloud.api.network.protocol.packet.master;

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
    public void write(ByteBuf byteBuf) throws IOException {
        byteBuf.writeBoolean(this.response);

        writeString(byteBuf, this.message);

    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        this.response = byteBuf.readBoolean();

        this.message = readString(byteBuf);

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
