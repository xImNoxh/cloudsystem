package de.polocloud.api.network.protocol.packet.statistics;

import de.polocloud.api.network.protocol.packet.IPacket;

public class StatisticMemoryPacket implements IPacket {

    private long currentMemory;

    public StatisticMemoryPacket(long currentMemory) {
        this.currentMemory = currentMemory;
    }

    public long getCurrentMemory() {
        return currentMemory;
    }

    public StatisticMemoryPacket() {
    }
}
