package de.polocloud.api.network.request.base.future;


import de.polocloud.api.network.packets.other.TextPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.request.base.other.PoloCloudQueryTimeoutException;
import de.polocloud.api.network.request.base.component.PoloComponent;
import de.polocloud.api.network.request.base.component.SimpleComponent;
import de.polocloud.api.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class SimpleFuture<T> implements PoloFuture<T> {

    private static final long serialVersionUID = -4424743782381022342L;

    /**
     * The request
     */
    private PoloComponent<T> request;

    /**
     * The latches to lock and unlock
     */
    private final Collection<CountDownLatch> countDownLatches;

    /**
     * All listeners
     */
    private final List<PoloFutureListener<T>> listeners;

    /**
     * The response if set
     */
    private volatile T response;

    /**
     * The error
     */
    private volatile Throwable error;

    /**
     * If completed yet
     */
    private volatile boolean completed;

    /**
     * If the future was successful
     */
    private boolean success;

    /**
     * The time the future took
     */
    private long completionTimeMillis = -1L;

    /**
     * The extra packet
     */
    private Packet packet;

    public SimpleFuture(PoloComponent<T> request) {
        this.request = request;
        this.countDownLatches = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }


    @Override
    public PoloFuture<T> nonBlocking(T blockingObject) {

        SimpleComponent<T> simpleComponent = new SimpleComponent<>();
        simpleComponent.id(this.request.getId());
        simpleComponent.data(blockingObject);
        simpleComponent.success(false);
        simpleComponent.packet(new TextPacket("Non-Blocking-Packet"));

        this.completeFuture(simpleComponent);
        return this;
    }

    @Override
    public PoloFuture<T> addListener(PoloFutureListener<T> listener) {
        this.listeners.add(listener);
        return this;
    }

    @Override
    public PoloFuture<T> timeOut(long ticks, T timeOutValue) {
        Scheduler.runtimeScheduler().schedule(() -> nonBlocking(timeOutValue), ticks);
        return this;
    }

    @Override
    public T pullValue() throws PoloCloudQueryTimeoutException {

        if (success) {
            return response;
        }
       // CountDownLatch latch = new CountDownLatch(1);
        // this.countDownLatches.add(latch);

        while (!completed) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return response;
    }


    @Override
    public Packet pullPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    /**
     * Completes this request and handles all listeners
     *
     * @param response the response
     */
    public void completeFuture(PoloComponent<?> response) {
        if (this.completed) {
            return;
        }

        this.completed = true;
        this.success = response.isSuccess();
        this.completionTimeMillis = response.getCompletionTimeMillis();

        if (response.isSuccess()) {
            this.response = (T) response.getData();
        } else {
            this.error = response.getException();
        }

        for (PoloFutureListener<T> listener : this.listeners) {
            listener.handle(this);
        }

        for (CountDownLatch latch : countDownLatches) {
            latch.countDown();
        }
        this.countDownLatches.clear();
    }

    public void setRequest(PoloComponent<?> request) {
        this.request = (PoloComponent<T>) request;
    }

    @Override
    public PoloComponent<T> getRequest() {
        return request;
    }

    @Override
    public Throwable getError() {
        return error;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    public void setCompletionTimeMillis(long completionTimeMillis) {
        this.completionTimeMillis = completionTimeMillis;
    }

    @Override
    public long getCompletionTimeMillis() {
        return completionTimeMillis;
    }
}
