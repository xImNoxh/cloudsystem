package de.polocloud.api.event.impl.net;

import de.polocloud.api.event.base.EventData;
import de.polocloud.api.event.base.IEvent;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

@EventData
public class ChannelActiveEvent implements IEvent {

    private final ChannelHandlerContext chx;

    public ChannelActiveEvent(ChannelHandlerContext chx) {
        this.chx = chx;
    }

    public ChannelHandlerContext getChx() {
        return chx;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
    }

}
