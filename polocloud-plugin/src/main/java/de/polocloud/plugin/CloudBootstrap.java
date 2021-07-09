package de.polocloud.plugin;


import de.polocloud.api.CloudAPI;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.network.client.INettyClient;
import de.polocloud.api.network.client.SimpleNettyClient;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.SimpleProtocol;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerRegisterPacket;

import java.io.File;
import java.net.URISyntaxException;

public class CloudBootstrap {

    private INettyClient client;
    private CloudAPI cloudAPI;

    public void connect(int port) {
        this.cloudAPI = new PoloCloudAPI();
        this.client = new SimpleNettyClient("localhost", 8869, new SimpleProtocol());

        new Thread(() -> {



            this.client.start();

        }).start();

        new Thread(() -> {

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            register(port);
        }).start();
    }

    public void registerPacketHandler(IPacketHandler packetHandler) {
        this.client.getProtocol().registerPacketHandler(packetHandler);
    }

    private void register(int port) {
        System.out.println("try to register myself");

        try {
            String path = new File(CloudBootstrap.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getPath();
            String[] split = path.split("/");
            System.out.println(path);

            sendPacket(new GameServerRegisterPacket(Long.parseLong(split[split.length - 3]), port));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(IPacket packet) {
        this.client.sendPacket(packet);
    }


}
