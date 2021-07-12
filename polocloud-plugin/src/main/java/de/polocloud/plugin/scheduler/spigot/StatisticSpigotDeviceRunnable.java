package de.polocloud.plugin.scheduler.spigot;

import de.polocloud.api.network.protocol.packet.statistics.StatisticMemoryPacket;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.scheduler.StatisticMathBalancer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class StatisticSpigotDeviceRunnable extends StatisticMathBalancer {

    private BukkitTask task;

    public StatisticSpigotDeviceRunnable(Plugin plugin, NetworkClient networkClient) {
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> networkClient.sendPacket(new StatisticMemoryPacket(getUsedMemory())),100,100);
    }

    public BukkitTask getTask() {
        return task;
    }
}
