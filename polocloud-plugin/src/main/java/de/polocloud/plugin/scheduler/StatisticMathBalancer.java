package de.polocloud.plugin.scheduler;

public abstract class StatisticMathBalancer {

    public long getUsedMemory() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576;
    }

}
