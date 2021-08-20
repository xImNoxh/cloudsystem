package de.polocloud.bootstrap.listener;

import de.polocloud.api.event.handling.IEventHandler;
import de.polocloud.api.event.impl.net.NettyExceptionEvent;

import java.io.IOException;

public class NettyExceptionListener implements IEventHandler<NettyExceptionEvent> {
    @Override
    public void handleEvent(NettyExceptionEvent event) {
        Throwable throwable = event.getThrowable();
        if (throwable.getMessage().equalsIgnoreCase("Connection reset by peer") && throwable instanceof IOException) {
            event.setShouldThrow(false);
            return;
        }
    }
}
