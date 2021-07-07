package de.polocloud.api.network.client;

import com.esotericsoftware.kryonetty.ClientEndpoint;
import com.esotericsoftware.kryonetty.network.handler.NetworkListener;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.packet.IPacket;

public class SimpleNettyClient implements INettyClient {

    @Inject
    @Named("setting_client_host")
    private String host;

    @Inject
    @Named("setting_client_port")
    private int port;

    @Inject
    private IProtocol protocol;

    private ClientEndpoint clientEndpoint;

    @Override
    public void start() {
        this.clientEndpoint = new ClientEndpoint(protocol.getProtocol());
        this.clientEndpoint.connect(this.host, this.port);
    }

    @Override
    public boolean terminate() {
        this.clientEndpoint.close();
        return true;
    }

    @Override
    public void sendPacket(IPacket object) {
        this.clientEndpoint.send(object);
    }

    @Override
    public void registerListener(NetworkListener networkListener) {
        this.clientEndpoint.getEventHandler().register(networkListener);
    }
}
