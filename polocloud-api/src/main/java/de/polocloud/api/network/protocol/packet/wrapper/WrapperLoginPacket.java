package de.polocloud.api.network.protocol.packet.wrapper;

import de.polocloud.api.network.protocol.packet.IPacket;

public class WrapperLoginPacket implements IPacket {

    private String key;

    public WrapperLoginPacket() {
    }
    public WrapperLoginPacket(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
