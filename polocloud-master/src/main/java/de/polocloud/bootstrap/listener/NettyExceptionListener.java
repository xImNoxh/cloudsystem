package de.polocloud.bootstrap.listener;

import de.polocloud.api.event.EventHandler;
import de.polocloud.api.event.netty.NettyExceptionEvent;

import java.io.IOException;

public class NettyExceptionListener implements EventHandler<NettyExceptionEvent> {
    @Override
    public void handleEvent(NettyExceptionEvent event) {
        Throwable throwable = event.getThrowable();
        if (throwable.getMessage().equalsIgnoreCase("Connection reset by peer") && throwable instanceof IOException) {
            event.setShouldThrow(false);
            return;
        }
    }
}
