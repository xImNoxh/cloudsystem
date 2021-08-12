package de.polocloud.plugin.protocol;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.event.channel.ChannelActiveEvent;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.client.INettyClient;
import de.polocloud.api.network.client.SimpleNettyClient;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.SimpleProtocol;
import de.polocloud.api.network.protocol.packet.IPacketSender;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerRegisterPacket;
import de.polocloud.plugin.bootstrap.IBootstrap;
import de.polocloud.plugin.protocol.config.ConfigReader;
import de.polocloud.plugin.protocol.register.NetworkPluginRegister;

import java.io.File;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class NetworkClient implements IPacketSender {

    private INettyClient client;
    private int port;
    private IBootstrap bootstrap;

    public NetworkClient(IBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        EventRegistry.registerListener(event -> {
            register(port);
        }, ChannelActiveEvent.class);
        String[] split = ConfigReader.getMasterAddress().split(":");
        this.client = new SimpleNettyClient(split[0], Integer.parseInt(split[1]), new SimpleProtocol());
    }

    public INettyClient getClient() {
        return client;
    }

    public void connect(int port) {
        this.port = port;
        new Thread(() -> {
            System.out.println("test 1213");
            this.client.start();
            System.exit(-1);
        }).start();
    }

    public void registerPacketHandler(IPacketHandler<Packet> packetHandler) {
        this.client.getProtocol().registerPacketHandler(packetHandler);
    }

    private void register(int port) {
        new NetworkPluginRegister(bootstrap);
        bootstrap.registerPacketListening();
        try {
            String path = new File(NetworkClient.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
            String[] split = path.split(path.contains("/") ? "/" : "\\\\");
            sendPacket(new GameServerRegisterPacket(Long.parseLong(split[split.length - 3].split("#")[1]), port));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendPacket(Packet packet) {
        this.client.sendPacket(packet);
    }
}
