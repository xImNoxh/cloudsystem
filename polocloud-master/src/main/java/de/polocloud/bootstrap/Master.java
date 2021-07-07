package de.polocloud.bootstrap;

import com.esotericsoftware.kryonetty.network.ConnectEvent;
import com.esotericsoftware.kryonetty.network.ReceiveEvent;
import com.esotericsoftware.kryonetty.network.handler.NetworkHandler;
import com.esotericsoftware.kryonetty.network.handler.NetworkListener;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.network.IStartable;
import de.polocloud.api.network.ITerminatable;
import de.polocloud.api.network.protocol.packet.TestPacket;
import de.polocloud.api.network.server.SimpleNettyServer;

public class Master implements IStartable, ITerminatable {

    private CloudAPI cloudAPI;

    private SimpleNettyServer nettyServer;

    public Master() {
        this.cloudAPI = new PoloCloudAPI();
    }

    @Override
    public void start() {
        this.nettyServer = this.cloudAPI.getGuice().getInstance(SimpleNettyServer.class);

        nettyServer.start();

        this.nettyServer.registerListener(new NetworkListener() {
            @NetworkHandler
            public void handle(ConnectEvent event) {
                System.out.println("Client connected! " + event.getCtx().channel().toString());
            }

            @NetworkHandler
            public void handle(ReceiveEvent event) {
                Object object = event.getObject();

                TestPacket packet = (TestPacket) object;
                System.out.println(packet.getKey());

            }
        });
    }

    @Override
    public boolean terminate() {
        return this.nettyServer.terminate();
    }
}
