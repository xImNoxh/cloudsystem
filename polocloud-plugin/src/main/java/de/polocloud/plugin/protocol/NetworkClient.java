package de.polocloud.plugin.protocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.network.client.INettyClient;
import de.polocloud.api.network.client.SimpleNettyClient;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.SimpleProtocol;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerRegisterPacket;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

public class NetworkClient {

    private INettyClient client;
    private CloudAPI cloudAPI;

    public void connect(int port) {
        this.cloudAPI = new PoloCloudAPI();

        String path = null;
        try {
            File parentFile = new File(NetworkClient.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getParentFile().getParentFile();
            parentFile = new File(parentFile + "/PoloCloud.json");

            Gson gson = new GsonBuilder().create();

            FileReader reader = new FileReader(parentFile);

            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

            String masterAddress = jsonObject.get("Master-Address").getAsString();

            System.out.println("master address: " + masterAddress);


            reader.close();


            String[] split = masterAddress.split(":");
            this.client = new SimpleNettyClient(split[0], Integer.parseInt(split[1]), new SimpleProtocol());

            new Thread(() -> {
                this.client.start();
                System.exit(-1);
            }).start();

            new Thread(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                register(port);
            }).start();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    public void registerPacketHandler(IPacketHandler packetHandler) {
        this.client.getProtocol().registerPacketHandler(packetHandler);
    }

    private void register(int port) {
        try {
            String path = new File(NetworkClient.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getPath();
            String[] split = path.split("/");
            System.out.println(path);

            sendPacket(new GameServerRegisterPacket(Long.parseLong(split[split.length - 3].split("#")[1]), port));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(IPacket packet) {
        this.client.sendPacket(packet);
    }
}
