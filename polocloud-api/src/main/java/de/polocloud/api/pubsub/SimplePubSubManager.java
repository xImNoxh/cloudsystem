package de.polocloud.api.pubsub;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.packet.IPacketSender;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.api.PublishPacket;
import de.polocloud.api.network.protocol.packet.api.SubscribePacket;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class SimplePubSubManager implements IPubSubManager {

    private IPacketSender sender;

    private Map<String, List<Consumer<PublishPacket>>> subMap = new ConcurrentHashMap<>();

    private Executor executor = Executors.newCachedThreadPool();

    public SimplePubSubManager(IPacketSender sender, IProtocol protocol) {
        this.sender = sender;

        protocol.registerPacketHandler(new IPacketHandler() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
                executor.execute(() -> {

                    PublishPacket packet = (PublishPacket) obj;

                    String channel = packet.getChannel();

                    if (subMap.containsKey(channel)) {
                        List<Consumer<PublishPacket>> consumerList = subMap.get(channel);

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
        List<Consumer<PublishPacket>> channelList;
        if (subMap.containsKey(channel)) {
            channelList = subMap.get(channel);
        } else {
            channelList = new CopyOnWriteArrayList<>();
        }

        channelList.add(consumer);

        this.sender.sendPacket(new SubscribePacket(channel));

        subMap.put(channel, channelList);

    }
}
