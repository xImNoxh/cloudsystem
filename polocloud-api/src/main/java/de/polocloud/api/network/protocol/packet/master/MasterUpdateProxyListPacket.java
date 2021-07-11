package de.polocloud.api.network.protocol.packet.master;

import de.polocloud.api.network.protocol.packet.IPacket;

import java.util.List;

public class MasterUpdateProxyListPacket implements IPacket {

    private List<String> proxyList;

    public MasterUpdateProxyListPacket() {

    }

    public MasterUpdateProxyListPacket(List<String> proxyList) {
        this.proxyList = proxyList;
    }

    public List<String> getProxyList() {
        return proxyList;
    }
}
