package de.polocloud.api.network.request;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.packets.api.PublishPacket;
import de.polocloud.api.network.packets.other.BuffedPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.api.network.request.base.component.PoloComponent;
import de.polocloud.api.network.request.base.component.SimpleComponent;
import de.polocloud.api.network.request.base.future.PoloFuture;
import de.polocloud.api.network.request.base.future.SimpleFuture;
import de.polocloud.api.network.request.base.other.IRequestHandler;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.util.PoloHelper;
import io.netty.channel.ChannelHandlerContext;

import java.util.*;
import java.util.function.Consumer;

public class SimpleRequestManager implements IRequestManager {

    /**
     * All stored futures with their ids
     */
    private final Map<String, PoloFuture<?>> futures;

    /**
     * Request handlers for api request-response
     */
    private final List<IRequestHandler<?>> requestHandlers;

    /**
     * The parent connection
     */
    private final INetworkConnection connection;

    public SimpleRequestManager(INetworkConnection connection) {
        this.connection = connection;
        this.futures = new HashMap<>();
        this.requestHandlers = new LinkedList<>();

        Scheduler.runtimeScheduler().schedule(() -> {

            connection.getProtocol().registerPacketHandler(new IPacketHandler<BuffedPacket>() {
                @Override
                public void handlePacket(ChannelHandlerContext ctx, BuffedPacket obj) {
                    Packet packet = obj.getPacket();
                    packet.setSnowflake(obj.getSnowflake());
                    PoloFuture<?> poloFuture = futures.get(String.valueOf(packet.getSnowflake()));
                    if (poloFuture != null) {
                        ((SimpleFuture<?>)poloFuture).setPacket(packet);
                        futures.put(String.valueOf(packet.getSnowflake()), poloFuture);
                    }
                }

                @Override
                public Class<? extends Packet> getPacketClass() {
                    return BuffedPacket.class;
                }
            });

            PoloCloudAPI.getInstance().getPubSubManager().subscribe("cloud::api::request", new Consumer<PublishPacket>() {
                @Override
                public void accept(PublishPacket publishPacket) {
                    JsonData data = new JsonData(publishPacket.getData());
                    PoloComponent<?> request = data.getObject("request", SimpleComponent.class);

                    PoloType connectionType = PoloCloudAPI.getInstance().getType();
                    if (connectionType.isCloud()) {
                        if (request.getTarget() == null || request.getTarget().equalsIgnoreCase("CLOUD")) {
                            for (IRequestHandler requestHandler : requestHandlers) {
                                requestHandler.handle(request);
                            }
                        } else {
                            PoloFuture<?> comply = request.query();
                            ((SimpleComponent<?>)request.createResponse(request.typeClass())).data(comply.pullValue()).respond();
                        }
                        return;
                    }

                    if (connectionType.isPlugin()) {
                        if (request.getTarget() == null || request.getTarget().equalsIgnoreCase(PoloCloudAPI.getInstance().getGameServerManager().getThisService().getName()) || request.getTarget().equalsIgnoreCase("PROXY") && PoloCloudAPI.getInstance().getGameServerManager().getThisService().getTemplate().getTemplateType() == TemplateType.PROXY || request.getTarget().equalsIgnoreCase("BUKKIT") && PoloCloudAPI.getInstance().getGameServerManager().getThisService().getTemplate().getTemplateType() == TemplateType.MINECRAFT) {
                            for (IRequestHandler requestHandler : requestHandlers) {
                                requestHandler.handle(request);
                            }
                        }
                    }

                }
            });

            PoloCloudAPI.getInstance().getPubSubManager().subscribe("cloud::api::response", publishPacket -> {
                try {

                    JsonData data = new JsonData(publishPacket.getData()).getData("response");

                    SimpleComponent<?> response = new SimpleComponent<>();

                    response.setResponse(true);
                    response.key(data.fallback("None").getString("key"));
                    response.id(data.fallback("None").getString("id"));
                    response.typeClass(Class.forName(data.getString("typeClass")));
                    response.success(data.fallback(false).getBoolean("success"));
                    response.setCompletionTimeMillis(data.fallback(System.currentTimeMillis()).getLong("completionTimeMillis"));

                    Class<?> aClass = response.typeClass();

                    if (aClass.equals(Boolean.class) || aClass.equals(boolean.class)) {
                        response.data(data.getBoolean("data"));
                    } else if (aClass.equals(Integer.class) || aClass.equals(int.class)) {
                        response.data(data.getInteger("data"));
                    } else if (aClass.equals(Long.class) || aClass.equals(long.class)) {
                        response.data(data.getLong("data"));
                    } else if (aClass.equals(Short.class) || aClass.equals(short.class)) {
                        response.data(data.getShort("data"));
                    } else if (aClass.equals(Byte.class) || aClass.equals(byte.class)) {
                        response.data(data.getByte("data"));
                    } else if (aClass.equals(Double.class) || aClass.equals(double.class)) {
                        response.data(data.getDouble("data"));
                    } else if (aClass.equals(Float.class) || aClass.equals(float.class)) {
                        response.data(data.getFloat("data"));
                    } else if (aClass.equals(UUID.class)) {
                        response.data(UUID.fromString(data.getString("data")));
                    } else if (aClass.equals(String.class)) {
                        response.data(data.getString("data"));
                    } else if (Enum.class.isAssignableFrom(aClass)) {
                        response.data(Objects.requireNonNull(PoloHelper.getEnumByName(aClass, data.getString("data"))));
                    } else {
                        response.data(data.getObject("data", aClass));
                    }

                    String exceptionClass = data.fallback("None").getString("exceptionClass");
                    if (!exceptionClass.equalsIgnoreCase("None")) {
                        if (data.getElement("exception").isJsonNull()) {
                            response.exception(null);
                        } else {
                            Class<?> exClass = Class.forName(exceptionClass);
                            Object exception = data.getObject("exception", exClass);
                            response.exception((Throwable) exception);
                        }
                    }

                    SimpleFuture<?> future = (SimpleFuture<?>) retrieveFuture(response.getId());
                    if (future == null) {
                        if (PoloCloudAPI.getInstance().getType().isCloud()) {
                            //PoloCloudAPI.getInstance().getCommandExecutor().sendMessage("§cTried to retrieve Future with id §e" + response.getId() + " §cbut was not found!");
                        } else {
                            //System.out.println("[RequestManager] Tried to retrieve Future with id " + response.getId() + " but was not found!");
                        }
                        return;
                    }
                    PoloComponent<?> request = future.getRequest();
                    request.typeClass(response.typeClass());
                    ((SimpleComponent<?>)request).setCompletionTimeMillis(response.getCompletionTimeMillis());
                    future.setRequest(request);
                    future.completeFuture(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }, () -> PoloCloudAPI.getInstance() != null && PoloCloudAPI.getInstance().getPubSubManager() != null);

    }

    @Override
    public void registerRequestHandler(IRequestHandler<?> handler) {
        this.requestHandlers.add(handler);
    }

    @Override
    public void unregisterRequestHandler(IRequestHandler<?> handler) {
        this.requestHandlers.remove(handler);
    }

    /**
     * Adds a {@link PoloFuture} to the cache with a given id
     *
     * @param id the id of the request
     * @param future the future
     */
    public void addRequest(String id, PoloFuture<?> future) {
        futures.put(id, future);
    }

    /**
     * Gets an {@link PoloFuture} from cache
     * and then automatically removes it from cache
     *
     * @param id the id
     * @return future or null
     */
    public PoloFuture<?> retrieveFuture(String id) {
        PoloFuture<?> future = futures.get(id);
        futures.remove(id);
        return future;
    }

}
