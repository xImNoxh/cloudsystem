package de.polocloud.modules.smartproxy.api;

import de.polocloud.api.util.PoloHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Getter @AllArgsConstructor
public class IpSetter {

    private final String clientAddress;
    private final String channelAddress;

    public IpSetter(SocketAddress clientAddress, SocketAddress channelAddress) {
        this.clientAddress = clientAddress.toString();
        this.channelAddress = channelAddress.toString();
    }

    @SneakyThrows
    public InetSocketAddress getClientAddress() {
        return PoloHelper.getAddress(clientAddress);
    }

    @SneakyThrows
    public InetSocketAddress getChannelAddress() {
        return PoloHelper.getAddress(channelAddress);
    }


}
