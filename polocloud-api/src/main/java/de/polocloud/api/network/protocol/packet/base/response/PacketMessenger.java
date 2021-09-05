package de.polocloud.api.network.protocol.packet.base.response;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.client.INettyClient;
import de.polocloud.api.network.packets.other.RequestPassOnPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.base.response.base.IResponse;
import de.polocloud.api.network.protocol.packet.base.response.base.ResponseFutureListener;
import de.polocloud.api.network.protocol.packet.base.response.def.Request;
import de.polocloud.api.network.protocol.packet.base.response.def.Response;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.api.network.server.INettyServer;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.util.map.MapEntry;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * The packetMessenger is for sending {@link Packet}'s to a target
 */

public class PacketMessenger {

    /**
     * The targets to send the packet to
     */
    private final List<Channel> target;

    /**
     * A consumer to handle a response if coming
     */
    private ResponseFutureListener responseFutureListener;

    /**
     * If the thread should be blocked to return
     */
    private boolean blocking;

    /**
     * The timeout value if timed out
     */
    private IResponse timedValue;

    /**
     * The timeout for the query
     */
    private int timeOut;

    /**
     * If pass on should automatically
     * be enabled on the other instance side
     */
    private boolean passOn;

    public PacketMessenger() {
        this.target = new ArrayList<>();
        this.responseFutureListener = null;
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
     * Enables pass on
     *
     * @return current instance
     */
    public PacketMessenger setUpPassOn() {
        this.passOn = true;
        return this;
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
    public PacketMessenger orElse(IResponse response) {
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
    public PacketMessenger addListener(ResponseFutureListener responseConsumer) {
        this.responseFutureListener = responseConsumer;
        return this;
    }

    /**
     * Creates a new {@link Request} with a given key ({@link String}) and data ({@link JsonData})
     * The key provides the action you want to perform (e.g. "get-ping")
     * Then in the data you can provide all your information the {@link Request} needs to create
     * a valid {@link IResponse} for you to receive
     *
     * @param key the key
     * @param data the data
     * @return response
     */
    public IResponse send(String key, JsonData data) {
        if (passOn) {
            PoloCloudAPI.getInstance().sendPacket(new RequestPassOnPacket(key));
        }
        return send(new Request(key, data));
    }

    /**
     * Works just like {@link PacketMessenger#send(String, JsonData)} but instead of a {@link Request}
     * The normal packet will be sent
     * Then you have to register an {@link IPacketHandler} and simply use {@link Packet#respond(JsonData)}
     * to respond to the incoming {@link Packet} request
     *
     * @param packet the packet
     * @return response
     */
    public IResponse send(Packet packet) {

        long requestId = packet.getSnowflake();
        INetworkConnection connection = PoloCloudAPI.getInstance().getConnection();

        if (connection instanceof INettyServer && !target.isEmpty()) {
            INettyServer nettyServer = (INettyServer)connection;
            nettyServer.sendPacket(packet, this.target.toArray(new Channel[0]));
        }

        IResponse[] response = {null};

        connection.sendPacket(packet);
        connection.getProtocol().registerPacketHandler(new IPacketHandler<Response>() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Response packet) {
                if (packet.getSnowflake() == requestId) {
                    response[0] = packet;
                    if (responseFutureListener != null) {
                        responseFutureListener.handle(packet);
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


    /**
     * All registered handlers to handle incoming {@link Request}
     * So you don't have to create an extra packet for each request
     * but you can simply just send a {@link Request} with a key and data
     */
    private static final List<Consumer<Request>> REQUEST_HANDLERS;

    static {

        //Setting new handler list
        REQUEST_HANDLERS = new ArrayList<>();

        //If the connection is set it will execute this task
        Scheduler.runtimeScheduler().schedule(() -> {

            PoloCloudAPI.getInstance().getConnection().getProtocol().registerPacketHandler(new IPacketHandler<Request>() {
                @Override
                public void handlePacket(ChannelHandlerContext ctx, Request packet) {
                    for (Consumer<Request> requestHandler : new ArrayList<>(REQUEST_HANDLERS)) {
                        requestHandler.accept(packet);
                    }
                }

                @Override
                public Class<? extends Packet> getPacketClass() {
                    return Request.class;
                }
            });

        }, () -> PoloCloudAPI.getInstance() != null && PoloCloudAPI.getInstance().getConnection() != null);
    }

    /**
     * Registers a new handler to handle all {@link Request}s
     * The handler is just simply a {@link Consumer}
     *
     * @param requestHandler the handler
     */
    public static void registerHandler(Consumer<Request> requestHandler) {
        REQUEST_HANDLERS.add(requestHandler);
    }

    /**
     * Unregisters an existing handler
     *
     * @param requestHandler the handler
     */
    public static void unregisterHandler(Consumer<Request> requestHandler) {
        REQUEST_HANDLERS.remove(requestHandler);
    }
}
