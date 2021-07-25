package de.polocloud.api.event.channel;

import de.polocloud.api.event.CloudEvent;
import io.netty.channel.ChannelHandlerContext;

public class ChannelInactiveEvent implements CloudEvent {

    private ChannelHandlerContext chx;

    public ChannelInactiveEvent(ChannelHandlerContext chx) {
        this.chx = chx;
    }

    public ChannelHandlerContext getChx() {
        return chx;
    }
}
