package de.polocloud.api.pubsub;

import de.polocloud.api.network.packets.api.PublishPacket;

import java.util.function.Consumer;

public interface IPubSubManager {

    /**
     * Publishes data parsed as {@link String}
     * for a specified channel to receive the data
     * you can work with json to parse and re-parse it later
     * or just send plain strings to other servers
     *
     * @param channel the channel to receive it
     * @param s the data
     */
    void publish(String channel, String s);

    /**
     * Subscribes to a channel and registers a handler to handle
     * when data is published to this channel
     *
     * @param channel the channel
     * @param consumer the handler of the data
     */
    void subscribe(String channel, Consumer<PublishPacket> consumer);

}
