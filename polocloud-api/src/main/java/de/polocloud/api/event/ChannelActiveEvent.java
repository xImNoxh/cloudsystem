package de.polocloud.api.event;

import io.netty.channel.ChannelHandlerContext;

public class ChannelActiveEvent implements CloudEvent {

    private ChannelHandlerContext chx;

    public ChannelActiveEvent(ChannelHandlerContext chx) {
        this.chx = chx;

    }

    public ChannelHandlerContext getChx() {
        return chx;
    }
}
