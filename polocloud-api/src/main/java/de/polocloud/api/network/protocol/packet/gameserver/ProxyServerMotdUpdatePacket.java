package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.packet.IPacket;

public class ProxyServerMotdUpdatePacket implements IPacket {

    private String first;
    private String second;

    public ProxyServerMotdUpdatePacket(){

    }

    public ProxyServerMotdUpdatePacket(String first, String second) {
        this.first = first;
        this.second = second;
    }

    public String getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }
}


