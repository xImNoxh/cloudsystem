package de.polocloud.api.event;

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
