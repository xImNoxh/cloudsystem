package de.polocloud.api.event.impl.net;

import de.polocloud.api.event.base.EventData;
import de.polocloud.api.event.base.CloudEvent;
import io.netty.channel.ChannelHandlerContext;

@EventData
public class ChannelInactiveEvent extends CloudEvent {

    private final ChannelHandlerContext chx;

    public ChannelInactiveEvent(ChannelHandlerContext chx) {
        this.chx = chx;
        setNettyFired(true);
    }

    public ChannelHandlerContext getChx() {
        return chx;
    }

}
