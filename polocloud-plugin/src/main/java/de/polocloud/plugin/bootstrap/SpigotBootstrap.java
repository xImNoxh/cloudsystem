package de.polocloud.plugin.bootstrap;

import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.bootstrap.command.TestCloudCommand;
import de.polocloud.plugin.function.BootstrapFunction;
import de.polocloud.plugin.function.NetworkRegisterFunction;
import de.polocloud.plugin.listener.CollectiveSpigotEvents;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.register.NetworkPluginRegister;
import de.polocloud.plugin.protocol.register.NetworkSpigotRegister;
import de.polocloud.plugin.scheduler.spigot.StatisticSpigotDeviceRunnable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotBootstrap extends JavaPlugin implements BootstrapFunction, NetworkRegisterFunction {

    @Override
    public void onEnable() {

        new CloudPlugin(this, this);

        getCommand("testCloud").setExecutor(new TestCloudCommand());
    }

    @Override
    public void executeCommand(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    @Override
    public int getNetworkPort() {
        return Bukkit.getPort();
    }

    @Override
    public void callNetwork(NetworkClient networkClient) {
        new NetworkSpigotRegister(networkClient);
        new NetworkPluginRegister(networkClient, this);
    }

    @Override
    public void initStatisticChannel(NetworkClient networkClient) {
        new StatisticSpigotDeviceRunnable(this, networkClient);
    }

    @Override
    public void registerEvents(NetworkClient networkClient) {
        new CollectiveSpigotEvents(this, networkClient);
    }

    @Override
    public void shutdown() {
        Bukkit.shutdown();
    }

    @Override
    public int getMaxPlayers() {
        return Bukkit.getMaxPlayers();
    }
}
