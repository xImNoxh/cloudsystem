package de.polocloud.api.network.protocol.packet.statistics;

import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class StatisticPacket extends Packet {

    private long currentMemory;
    private int tps;
    private long timestamp;

    public StatisticPacket() {

    }

    public StatisticPacket(long currentMemory, int tps, long timestamp) {
        this.currentMemory = currentMemory;
        this.tps = tps;
        this.timestamp = timestamp;
    }

    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        byteBuf.writeLong(currentMemory);
        byteBuf.writeInt(tps);
        byteBuf.writeLong(timestamp);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        currentMemory = byteBuf.readLong();
        tps = byteBuf.readInt();
        timestamp = byteBuf.readLong();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getTps() {
        return tps;
    }

    public long getCurrentMemory() {
        return currentMemory;
    }
    
}
