package de.polocloud.api.event.impl.net;

import de.polocloud.api.event.base.EventData;
import de.polocloud.api.event.base.CloudEvent;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;

import java.io.IOException;

@EventData
public class NettyExceptionEvent extends CloudEvent {

    private final Throwable throwable;

    private boolean shouldThrow;

    public NettyExceptionEvent(Throwable throwable) {
        this.throwable = throwable;
        setNettyFired(true);
    }

    public boolean isShouldThrow() {
        return shouldThrow;
    }

    public void setShouldThrow(boolean shouldThrow) {
        this.shouldThrow = shouldThrow;
    }

    public Throwable getThrowable() {
        return throwable;
    }

}
