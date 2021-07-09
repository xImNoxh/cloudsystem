package de.polocloud.api.network.protocol.packet.master;

import de.polocloud.api.network.protocol.packet.IPacket;

public class MasterLoginResponsePacket implements IPacket {

    private boolean response;
    private String message;

    public MasterLoginResponsePacket() {
    }
    public MasterLoginResponsePacket(boolean response, String message) {
        this.response = response;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }

    public boolean isResponse() {
        return response;
    }
}
