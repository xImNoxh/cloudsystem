package de.polocloud.api.event.impl.net;

import de.polocloud.api.event.base.EventData;
import de.polocloud.api.event.base.IEvent;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;

import java.io.IOException;

@EventData
public class NettyExceptionEvent implements IEvent {

    private final Throwable throwable;

    private boolean shouldThrow;

    public NettyExceptionEvent(Throwable throwable) {
        this.throwable = throwable;
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

    @Override
    public void read(IPacketBuffer buf) throws IOException {

    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {

    }

}
