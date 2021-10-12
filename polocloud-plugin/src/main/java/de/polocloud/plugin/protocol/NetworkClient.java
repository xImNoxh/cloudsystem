package de.polocloud.plugin.protocol;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.event.handling.IEventHandler;
import de.polocloud.api.event.impl.net.ChannelActiveEvent;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.client.INettyClient;
import de.polocloud.api.network.client.SimpleNettyClient;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.SimpleProtocol;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.bootstrap.IBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

public class NetworkClient implements INetworkConnection, IEventHandler<ChannelActiveEvent> {

    /**
     * The netty client instance
     */
    private final SimpleNettyClient client;

    /**
     * The bootstrap instance
     */
    private final IBootstrap bootstrap;

    /**
     * The address parsed as String-Array
     */
    private final String[] address;

    /**
     * If connected
     */
    private boolean connected;

    public NetworkClient(IBootstrap bootstrap) {
        JsonData jsonData = CloudPlugin.getInstance().getJson() == null ? new JsonData() : CloudPlugin.getInstance().getJson();

        this.bootstrap = bootstrap;
        this.connected = false;
        this.address =  jsonData.fallback("127.0.0.1").getString("Master-Address").split(":");
        this.client = new SimpleNettyClient(address[0], Integer.parseInt(address[1]), new SimpleProtocol());

        PoloCloudAPI.getInstance().getEventManager().registerHandler(ChannelActiveEvent.class, this);
    }


    /**
     * Connects this client to the given port-address
     * And accepts the provided consumer if it was successfully connected
     *
     * @param consumer the consumer to handle
     */
    public void connect(Consumer<INettyClient> consumer) {

        new Thread(() -> {
            System.out.println("[CloudPlugin] Trying to connect to Cloud (" + this.address[0] + ":" + this.address[1] + ")");
            this.client.start(client -> {
                connected = true;
                consumer.accept(client);
            }, Throwable::printStackTrace);
            System.exit(-1);
        }).start();
    }

    /**
     * Registering an {@link IPacketHandler}
     *
     * @param packetHandler the handler
     */
    public void registerPacketHandler(IPacketHandler<Packet> packetHandler) {
        this.client.getProtocol().registerPacketHandler(packetHandler);
    }

    @Override
    public Channel getChannel() {
        return this.client.getChannel();
    }

    @Override
    public ChannelHandlerContext ctx() {
        return this.client.ctx();
    }

    @Override
    public void setCtx(ChannelHandlerContext ctx) {
        this.client.setCtx(ctx);
    }

    @Override
    public void sendPacket(Packet packet) {
        this.client.sendPacket(packet);
    }


    @Override
    public IProtocol getProtocol() {
        return this.client.getProtocol();
    }

    @Override
    public boolean isConnected() {
        return connected;
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

    @Override
    public void handleEvent(ChannelActiveEvent event) {
        new NetworkPluginRegister(this.bootstrap);
        this.bootstrap.registerPacketListening();
    }
}
