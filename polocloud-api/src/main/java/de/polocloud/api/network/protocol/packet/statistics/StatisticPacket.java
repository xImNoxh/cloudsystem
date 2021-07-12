package de.polocloud.api.network.protocol.packet.statistics;

import de.polocloud.api.network.protocol.packet.IPacket;

public class StatisticPacket implements IPacket {

    private long currentMemory;
    private int tps;
    private long timestamp;

    public StatisticPacket(long currentMemory, int tps, long timestamp) {
        this.currentMemory = currentMemory;
        this.tps = tps;
        this.timestamp = timestamp;
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

    public StatisticPacket() {
    }
}
