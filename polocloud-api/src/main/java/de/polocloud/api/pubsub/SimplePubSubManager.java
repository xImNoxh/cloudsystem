package de.polocloud.api.pubsub;

import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.api.PublishPacket;
import de.polocloud.api.network.protocol.packet.api.SubscribePacket;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class SimplePubSubManager implements IPubSubManager {

    /**
     * The connection instance
     */
    private final INetworkConnection sender;

    /**
     * The registered handlers for a channel
     */
    private final Map<String, List<Consumer<PublishPacket>>> channelHandlers;

    /**
     * The executor-service instance
     */
    private final Executor executor;

    public SimplePubSubManager(INetworkConnection sender) {
        this.executor = Executors.newCachedThreadPool();
        this.channelHandlers = new ConcurrentHashMap<>();
        this.sender = sender;

        this.sender.getProtocol().registerPacketHandler(new IPacketHandler<Packet>() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
                executor.execute(() -> {

                    PublishPacket packet = (PublishPacket) obj;

                    String channel = packet.getChannel();

                    if (channelHandlers.containsKey(channel)) {
                        List<Consumer<PublishPacket>> consumerList = channelHandlers.get(channel);

                        for (Consumer<PublishPacket> subscribePacketConsumer : consumerList) {
                            subscribePacketConsumer.accept(packet);
                        }
                    }
                });

            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return PublishPacket.class;
            }
        });

    }


    @Override
    public void publish(String channel, String s) {
        this.sender.sendPacket(new PublishPacket(channel, s));
    }

    @Override
    public void subscribe(String channel, Consumer<PublishPacket> consumer) {
        List<Consumer<PublishPacket>> channelList = channelHandlers.containsKey(channel) ? channelHandlers.get(channel) : new CopyOnWriteArrayList<>();
        channelList.add(consumer);
        this.sender.sendPacket(new SubscribePacket(channel));
        channelHandlers.put(channel, channelList);
    }
}
