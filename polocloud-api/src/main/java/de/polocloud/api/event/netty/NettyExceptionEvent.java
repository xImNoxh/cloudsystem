package de.polocloud.api.event.netty;

import de.polocloud.api.event.CloudEvent;

public class NettyExceptionEvent implements CloudEvent {
    
    private Throwable throwable;

    private boolean shouldThrow;

    public NettyExceptionEvent(Throwable throwable) {
        this.throwable = throwable;
    }

    public void setShouldThrow(boolean shouldThrow) {
        this.shouldThrow = shouldThrow;
    }

    public boolean isShouldThrow() {
        return shouldThrow;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
