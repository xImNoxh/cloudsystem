package de.polocloud.api.event.channel;

import de.polocloud.api.event.CloudEvent;
import io.netty.channel.ChannelHandlerContext;

public class ChannelActiveEvent implements CloudEvent {

    private final ChannelHandlerContext chx;

    public ChannelActiveEvent(ChannelHandlerContext chx) {
        this.chx = chx;
    }

    public ChannelHandlerContext getChx() {
        return chx;
    }
}
