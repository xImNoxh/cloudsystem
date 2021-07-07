package de.polocloud.api.network.server;

import com.esotericsoftware.kryonetty.ThreadedServerEndpoint;
import com.esotericsoftware.kryonetty.network.handler.NetworkListener;
import com.google.inject.Inject;
import de.polocloud.api.network.protocol.IProtocol;

import javax.inject.Named;

public class SimpleNettyServer implements INettyServer {

    @Inject
    @Named(value = "setting_server_start_port")
    private int port;

    @Inject
    private IProtocol protocol;

    private ThreadedServerEndpoint threadedServerEndpoint;

    @Override
    public void start() {
        this.threadedServerEndpoint = new ThreadedServerEndpoint(this.protocol.getProtocol());
        this.threadedServerEndpoint.start(this.port);
        System.out.println("starting server on port " + this.port);
    }

    @Override
    public boolean terminate() {
        this.threadedServerEndpoint.close();
        return true;
    }

    @Override
    public void registerListener(NetworkListener networkListener) {
        this.threadedServerEndpoint.getEventHandler().register(networkListener);
    }
}
