package de.polocloud.api.pubsub;

import de.polocloud.api.network.protocol.packet.api.PublishPacket;

import java.util.function.Consumer;

public interface IPubSubManager {

    void publish(String channel, String s);

    void subscribe(String channel, Consumer<PublishPacket> consumer);

}
