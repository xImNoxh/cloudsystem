package de.polocloud.api.network.protocol.packet.base.response.extra;

import de.polocloud.api.config.JsonData;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.base.response.PacketMessenger;
import de.polocloud.api.network.protocol.packet.base.response.ResponseState;
import de.polocloud.api.network.protocol.packet.base.response.base.IResponse;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
public class SimpleNetworkPromise<E, W> implements INetworkPromise<E> {

    /**
     * The handlers
     */
    private final List<IPromiseFuture<E>> handlers;

    /**
     * The packet for the request
     */
    private final Packet packet;

    /**
     * The wrapper class
     */
    private final Class<W> wClass;

    /**
     * The messenger instance
     */
    private final PacketMessenger packetMessenger;

    /**
     * If it's blocking the thread
     */
    private boolean blocking;

    /**
     * IF its successful and completed
     */
    private boolean success, completed;

    /**
     * The error if provided
     */
    private Throwable cause;

    /**
     * The element if provided
     */
    private E element;

    public SimpleNetworkPromise(Packet packet, Class<E> eClass, Class<W> wClass) {
        this.packet = packet;
        this.wClass = wClass;
        this.handlers = new ArrayList<>();
        this.packetMessenger = PacketMessenger.create();
    }

    public INetworkPromise<E> execute() {

        this.packetMessenger.blocking();

        packetMessenger.addListener(this::handleResponse);
        IResponse send = packetMessenger.send(packet);
        handleResponse(send);
        return this;
    }

    @Override
    public INetworkPromise<E> dummy(E obj) {
        this.blocking = false;
        this.packetMessenger.noBlocking();
        this.success = true;
        this.element = obj;
        this.cause = null;
        return this;
    }

    private void handleResponse(IResponse response) {
        this.completed = true;

        if (response == null && !blocking) {
            success = false;
            element = null;
            cause = new NullPointerException("The response returned null!");
            return;
        }

        if (response.isTimedOut()) {
            success = false;
            cause = new RuntimeException("The request timed out");
        } else {

            JsonData document = response.getDocument();

            this.element = document.getObject("_element", (Type) wClass);
            this.success = document.getBoolean("_success");
            if (document.getElement("_throwable") == null || document.getElement("_throwable").isJsonNull()) {
                this.cause = null;
            } else {
                this.cause = document.getObject("_throwable", Throwable.class);
            }

            if (response.getStatus() == ResponseState.SUCCESS) {
                success = true;
            }
        }
        for (IPromiseFuture<E> handler : this.handlers) {
            handler.handle(this);
        }
    }

    @Override
    public INetworkPromise<E> blocking() {

        blocking = true;
        this.packetMessenger.blocking();

        return this;
    }

    @Override
    public void nonBlocking(IPromiseFuture<E> promise) {

        handlers.add(promise);
        this.blocking = false;
        this.packetMessenger.noBlocking();

    }
    @Override
    public @Nullable Throwable cause() {
        return cause;
    }

    @Override
    public E orElse(E e) {
        return element == null ? e : element;
    }

    @Override
    public @Nullable E get() {
        return element;
    }
}
