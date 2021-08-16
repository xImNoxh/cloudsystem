package de.polocloud.bootstrap.pubsub;

import de.polocloud.api.network.protocol.packet.api.PublishPacket;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MasterPubSubManager {

    private static MasterPubSubManager instance;
    private final Map<String, List<ChannelHandlerContext>> subscriberMap = new ConcurrentHashMap<>();

    public MasterPubSubManager() {
        instance = this;
    }

    public static MasterPubSubManager getInstance() {
        return instance;
    }

    public void subscribe(ChannelHandlerContext ctx, String channel) {
        List<ChannelHandlerContext> channelHandlerContextList;
        channelHandlerContextList = subscriberMap.containsKey(channel) ? subscriberMap.get(channel) : new ArrayList<>();
        boolean use = channelHandlerContextList.contains(ctx) ? channelHandlerContextList.remove(ctx) : channelHandlerContextList.add(ctx);
        subscriberMap.put(channel, channelHandlerContextList);

    }

    public void publish(String channel, String data) {
        publish(new PublishPacket(channel, data));
    }

    public void publish(PublishPacket packet) {
        String channel = packet.getChannel();
        if (subscriberMap.containsKey(channel)) {
            List<ChannelHandlerContext> channelHandlerContextList = subscriberMap.get(channel);
            for (ChannelHandlerContext channelHandlerContext : channelHandlerContextList) {
                channelHandlerContext.writeAndFlush(packet);
            }
        }
    }
}
