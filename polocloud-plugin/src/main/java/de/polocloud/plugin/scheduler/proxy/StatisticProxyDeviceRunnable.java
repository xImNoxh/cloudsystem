package de.polocloud.plugin.scheduler.proxy;

import de.polocloud.api.network.protocol.packet.statistics.StatisticMemoryPacket;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.scheduler.StatisticMathBalancer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;

public class StatisticProxyDeviceRunnable extends StatisticMathBalancer {

    private ScheduledTask task;

    public StatisticProxyDeviceRunnable(Plugin plugin, NetworkClient networkClient) {
        task = ProxyServer.getInstance().getScheduler().schedule(plugin, () -> networkClient.sendPacket(new StatisticMemoryPacket(getUsedMemory())),5l,5l,  TimeUnit.SECONDS);
    }

    public ScheduledTask getTask() {
        return task;
    }
}
