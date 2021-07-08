package de.polocloud.wrapper;

import com.esotericsoftware.kryonetty.network.ConnectEvent;
import com.esotericsoftware.kryonetty.network.handler.NetworkHandler;
import com.esotericsoftware.kryonetty.network.handler.NetworkListener;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.guice.PoloAPIGuiceModule;
import de.polocloud.api.network.IStartable;
import de.polocloud.api.network.ITerminatable;
import de.polocloud.api.network.client.SimpleNettyClient;
import de.polocloud.api.network.protocol.packet.TestPacket;

public class Wrapper implements IStartable, ITerminatable {

    private CloudAPI cloudAPI;

    private SimpleNettyClient nettyClient;

    public Wrapper() {
        this.cloudAPI = new PoloCloudAPI(new PoloAPIGuiceModule());
    }

    @Override
    public void start() {
        this.nettyClient = this.cloudAPI.getGuice().getInstance(SimpleNettyClient.class);
        this.nettyClient.start();

        this.nettyClient.registerListener(new NetworkListener() {

            @NetworkHandler
            public void handle(ConnectEvent event) {
                System.out.println("connected");
            }

        });
        Wrapper.this.nettyClient.sendPacket(new TestPacket("Hallo Welt"));
    }

    @Override
    public boolean terminate() {
        return this.nettyClient.terminate();
    }
}
