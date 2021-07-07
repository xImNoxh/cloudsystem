package de.polocloud.api.network.protocol.packet;

public class TestPacket implements IPacket {

    private String key;

    public TestPacket() {
    }

    public TestPacket(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
