package de.polocloud.plugin.protocol;

import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.event.channel.ChannelActiveEvent;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.client.INettyClient;
import de.polocloud.api.network.client.SimpleNettyClient;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.SimpleProtocol;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerRegisterPacket;
import de.polocloud.api.network.request.IRequestManager;
import de.polocloud.plugin.bootstrap.IBootstrap;
import de.polocloud.plugin.protocol.config.ConfigReader;
import de.polocloud.plugin.protocol.register.NetworkPluginRegister;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.function.Consumer;

public class NetworkClient implements INetworkConnection {

    private SimpleNettyClient client;
    private int port;
    private IBootstrap bootstrap;
    private String[] split;

    public NetworkClient(IBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        EventRegistry.registerListener(event -> {
            register(port);
        }, ChannelActiveEvent.class);
        this.split = ConfigReader.getMasterAddress().split(":");
        this.client = new SimpleNettyClient(split[0], Integer.parseInt(split[1]), new SimpleProtocol());
    }

    public INettyClient getClient() {
        return client;
    }

    public void connect(int port, Consumer<SimpleNettyClient> consumer) {

        this.port = port;

        new Thread(() -> {
            System.out.println("[CloudPlugin] Trying to connect to Cloud (" + this.split[0] + ":" + this.split[1] + ")");
            this.client.start(consumer);
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

    @Override
    public IRequestManager getRequestManager() {
        return this.client.getRequestManager();
    }

    @Override
    public IProtocol getProtocol() {
        return this.client.getProtocol();
    }

    @Override
    public InetSocketAddress getConnectedAddress() {
        return this.client.getConnectedAddress();
    }

    @Override
    public void start() {
        this.client.start();
    }

    @Override
    public boolean terminate() {
        return this.client.terminate();
    }
}
