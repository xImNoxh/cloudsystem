package de.polocloud.api.network.request;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.client.INettyClient;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.base.response.Response;
import de.polocloud.api.network.protocol.packet.base.response.ResponseState;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.api.network.server.INettyServer;
import de.polocloud.api.util.Snowflake;
import de.polocloud.api.util.map.MapEntry;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * The packetMessenger is for sending {@link Packet}'s to a target
 */

public class PacketMessenger {

    private static final Map<UUID, Future<?>> futureMap = new ConcurrentHashMap<>();

    public static <T> CompletableFuture<T> getCompletableFuture(UUID requestId, boolean autoRemove) {
        if (autoRemove) {
            return (CompletableFuture<T>) futureMap.remove(requestId);
        } else {
            return (CompletableFuture<T>) futureMap.get(requestId);
        }
    }

    public static void register(UUID requestID, Future<?> completableFuture) {
        futureMap.put(requestID, completableFuture);
    }

    /**
     * The targets to send the packet to
     */
    private final List<Channel> target;

    /**
     * A consumer to handle a response if coming
     */
    private Consumer<Response> responseConsumer;

    /**
     * If the thread should be blocked to return
     */
    private boolean blocking;

    /**
     * The timeout value if timed out
     */
    private Response timedValue;

    /**
     * The timeout for the query
     */
    private int timeOut;

    public PacketMessenger() {
        this.target = new ArrayList<>();
        this.responseConsumer = null;
        this.blocking = false;
        this.timeOut = 3000;
        this.timedValue = new Response(new JsonData(), ResponseState.TIMED_OUT);
    }

    /**
     * Creates a new PacketMessenger2 object
     * This is the method to initialize the object
     * IMPORTANT.
     *
     * @return The object
     */
    public static PacketMessenger newInstance() {
        return new PacketMessenger();
    }

    /**
     * Sets the target of this packet to send to
     *
     * @param channels The channels
     * @return This
     */
    public PacketMessenger target(Channel... channels) {
        this.target.addAll(Arrays.asList(channels));
        return this;
    }

    //Sets IGameServer targets
    public PacketMessenger target(IGameServer... clients) {
        List<Channel> channels = new ArrayList<>();
        for (IGameServer client : clients) {
            if (client.ctx() != null) {
                channels.add(client.ctx().channel());
            }
        }
        return target(channels.toArray(new Channel[0]));
    }

    //Sets PoloType targets
    public PacketMessenger target(PoloType... types) {
        List<Channel> channels = new ArrayList<>();
        if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {
            INettyServer nettyServer = (INettyServer) PoloCloudAPI.getInstance().getConnection();
            for (MapEntry<PoloType, INettyClient> entry : nettyServer.getClientsWithType().iterable()) {
                if (Arrays.asList(types).contains(entry.getKey())) {
                    channels.add(entry.getValue().getChannel());
                }
            }
        }
        return target(channels.toArray(new Channel[0]));
    }

    /**
     * Enables blocking to return value
     */
    public PacketMessenger blocking() {
        this.blocking = true;
        return this;
    }

    /**
     * Sets the fallback value when timing out
     *
     * @param response the response
     * @return current instance
     */
    public PacketMessenger orElse(Response response) {
        this.timedValue = response;
        return this;
    }

    /**
     * Sets the timeout value for timing out
     *
     * @param millis the millis
     * @return current instance
     */
    public PacketMessenger timeOutAfterMillis(long millis) {
        this.timeOut = (int) millis;
        return this;
    }

    /**
     * Sets the timeout value for timing out
     *
     * @return current instance
     */
    public PacketMessenger timeOutAfter(TimeUnit unit, long l) {
        return this.timeOutAfterMillis(unit.toMillis(l));
    }

    /**
     * Sets the handler if a response is received
     *
     * @param responseConsumer the handler
     * @return this instance
     */
    public PacketMessenger handler(Consumer<Response> responseConsumer) {
        this.responseConsumer = responseConsumer;
        return this;
    }

    public Response send(Packet packet) {

        long requestId = packet.getSnowflake();
        INetworkConnection connection = PoloCloudAPI.getInstance().getConnection();

        if (connection instanceof INettyServer && !target.isEmpty()) {
            INettyServer nettyServer = (INettyServer)connection;
            nettyServer.sendPacket(packet, this.target.toArray(new Channel[0]));
        }

        Response[] response = {null};

        connection.sendPacket(packet);
        connection.getProtocol().registerPacketHandler(new IPacketHandler<Response>() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Response packet) {
                if (packet.getSnowflake() == requestId) {
                    response[0] = packet;
                    if (responseConsumer != null) {
                        responseConsumer.accept(packet);
                    }
                    connection.getProtocol().unregisterPacketHandler(this);
                }
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return Response.class;
            }
        });

        if (!blocking) {
            return null;
        }

        int count = 0;
        while (response[0] == null && count++ < timeOut) {
            try {
                Thread.sleep(0, 500000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (count >= timeOut) {
            response[0] = timedValue;
        }
        return response[0];
    }

}
