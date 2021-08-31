package de.polocloud.api.network.request.base.component;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.packets.other.BuffedPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.request.IRequestManager;
import de.polocloud.api.network.request.base.future.PoloFuture;
import de.polocloud.api.network.request.base.future.SimpleFuture;
import de.polocloud.api.config.JsonData;

import java.util.concurrent.ThreadLocalRandom;

public class SimpleComponent<T> implements PoloComponent<T> {

    /**
     * The document
     */
    private String document;

    /**
     * The key
     */
    private String key;

    /**
     * If this component is a reply
     */
    private boolean response;

    /**
     * The id of this response
     */
    private String id = String.valueOf(ThreadLocalRandom.current().nextLong());

    /**
     * If success
     */
    private boolean success;

    /**
     * The target
     */
    private String target;

    /**
     * The error
     */
    private Throwable exception;

    /**
     * The data
     */
    private T data;

    /**
     * The type class
     */
    private String typeClass;

    /**
     * The time the future took
     */
    private long completionTimeMillis;

    /**
     * A provided extra packet
     */
    private Packet packet;

    public PoloComponent<T> data(Object data) {
        if (data == null) {
            System.out.println("Nulled object set as Data for PoloComponent!");
            success = false;
            this.data = null;
            this.typeClass(Void.class);
        } else {
            this.typeClass(data.getClass());
            this.data = (T) data;
            this.success = true;
        }
        return this;
    }

    @Override
    public PoloComponent<T> packet(Packet packet) {
        this.packet = packet;
        return this;
    }

    @Override
    public PoloComponent<T> value(T t) {
        return this.data(t);
    }

    @Override
    public Class<T> typeClass() {
        try {
            return (Class<T>) Class.forName(typeClass);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public PoloComponent<T> target(String target) {
        this.target = target;
        return this;
    }

    @Override
    public PoloComponent<T> typeClass(Class<?> typeClass) {
        this.typeClass = typeClass.getName();
        return this;
    }

    @Override
    public PoloComponent<T> key(String key) {
        this.key = key;
        return this;
    }

    @Override
    public <V> PoloComponent<V> createResponse(Class<V> vClass) {
        SimpleComponent<V> response = new SimpleComponent<>();
        response.setResponse(true);
        response.id(this.id);
        response.target(target);
        response.key(this.key);
        response.setCompletionTimeMillis(this.completionTimeMillis);
        response.typeClass(vClass);
        return response;
    }

    @Override
    public PoloFuture<T> query() {
        INetworkConnection networkConnection = PoloCloudAPI.getInstance().getConnection();
        SimpleFuture<T> query = new SimpleFuture<>(this);

        IRequestManager requestManager = networkConnection.getRequestManager();

        this.completionTimeMillis = System.currentTimeMillis();
        query.setCompletionTimeMillis(System.currentTimeMillis());

        requestManager.addRequest(this.id, query);
        respond();
        return query;
    }

    @Override
    public PoloComponent<T> document(JsonData document) {
        this.document = document.toString();
        return this;
    }

    public JsonData getDocument() {
        return new JsonData(document);
    }

    @Override
    public PoloComponent<T> id(String id) {
        this.id = id;
        return this;
    }

    @Override
    public PoloComponent<T> exception(Throwable throwable) {
        this.exception = throwable;
        this.success = false;
        return this;
    }

    @Override
    public PoloComponent<T> success(boolean success) {
        this.success = success;
        return this;
    }

    @Override
    public void respond() {
        String key = (isResponse() ? "response" : "request");

        JsonData data = new JsonData();
        JsonData sub = new JsonData();

        sub.append("typeClass", this.typeClass);
        sub.append("data", this.data);
        sub.append("exception", this.exception);
        sub.append("target", this.target);
        sub.append("completionTimeMillis", this.completionTimeMillis);
        sub.append("success", this.success);
        sub.append("id", this.id);
        sub.append("response", this.response);
        sub.append("key", this.key);
        sub.append("document", this.document);

        data.append(key, sub);

        if (packet != null) {
            BuffedPacket buffedPacket = new BuffedPacket(packet);
            buffedPacket.setSnowflake(Long.parseLong(this.id));
            PoloCloudAPI.getInstance().sendPacket(buffedPacket);
        }
        PoloCloudAPI.getInstance().getPubSubManager().publish("cloud::api::" + key, data.toString());
    }

    public void setCompletionTimeMillis(long completionTimeMillis) {
        this.completionTimeMillis = completionTimeMillis;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public boolean isResponse() {
        return response;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public Throwable getException() {
        return exception;
    }

    @Override
    public T getData() {
        return data;
    }

    @Override
    public long getCompletionTimeMillis() {
        return completionTimeMillis;
    }
}
