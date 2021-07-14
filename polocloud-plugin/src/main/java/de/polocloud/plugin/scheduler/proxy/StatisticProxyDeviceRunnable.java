package de.polocloud.plugin.scheduler.proxy;

import de.polocloud.api.network.protocol.packet.statistics.StatisticPacket;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.scheduler.StatisticMathBalancer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;

public class StatisticProxyDeviceRunnable extends StatisticMathBalancer {

    private ScheduledTask task;

    public StatisticProxyDeviceRunnable(Plugin plugin, NetworkClient networkClient) {
        //task = ProxyServer.getInstance().getScheduler().schedule(plugin, () -> networkClient.sendPacket(new StatisticPacket(getUsedMemory(), -1, System.currentTimeMillis())), 5L, 5L,  TimeUnit.SECONDS);
    }

    public ScheduledTask getTask() {
        return task;
    }
}
