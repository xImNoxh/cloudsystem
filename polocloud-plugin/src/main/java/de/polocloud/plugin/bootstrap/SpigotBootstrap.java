package de.polocloud.plugin.bootstrap;

import de.polocloud.api.event.ChannelActiveEvent;
import de.polocloud.api.event.EventHandler;
import de.polocloud.api.event.EventRegistry;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.api.CloudExecutor;
import de.polocloud.plugin.api.spigot.event.*;
import de.polocloud.plugin.bootstrap.command.TestCloudCommand;
import de.polocloud.plugin.bootstrap.listener.TestCloudListener;
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
        getServer().getPluginManager().registerEvents(new TestCloudListener(), this);


    }

    @Override
    public void executeCommand(String command) {
        Bukkit.getScheduler().runTask(this, () ->
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
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

        EventRegistry.registerListener((EventHandler<ChannelActiveEvent>) event -> {

            CloudExecutor.getInstance().getPubSubManager().subscribe("polo:event:serverStarted", packet -> {
                String serverName = packet.getData();
                CloudExecutor.getInstance().getGameServerManager().getGameServerByName(serverName).thenAccept(server ->
                    Bukkit.getPluginManager().callEvent(new CloudServerStartedEvent(server)));
            });

            CloudExecutor.getInstance().getPubSubManager().subscribe("polo:event:serverStopped", packet -> {
                String serverName = packet.getData();
                CloudExecutor.getInstance().getGameServerManager().getGameServerByName(serverName).thenAccept(server ->
                    Bukkit.getPluginManager().callEvent(new CloudServerStoppedEvent(server)));
            });

            CloudExecutor.getInstance().getPubSubManager().subscribe("polo:event:serverUpdated", packet -> {
                String serverName = packet.getData();
                CloudExecutor.getInstance().getGameServerManager().getGameServerByName(serverName).thenAccept(server ->
                    Bukkit.getPluginManager().callEvent(new CloudServerUpdatedEvent(server)));
            });


            CloudExecutor.getInstance().getPubSubManager().subscribe("polo:event:playerJoin", packet -> {
                String playerName = packet.getData();
                System.out.println("call join for player " + playerName);

                Bukkit.getPluginManager().callEvent(new CloudPlayerJoinNetworkEvent(playerName));
            });

            CloudExecutor.getInstance().getPubSubManager().subscribe("polo:event:playerQuit", packet -> {
                String playerName = packet.getData();
                System.out.println("call quit for player " + playerName);
                Bukkit.getPluginManager().callEvent(new CloudPlayerQuitNetworkEvent(playerName));
            });

            CloudExecutor.getInstance().getPubSubManager().subscribe("polo:event:playerSwitch", packet -> {

                System.out.println("call switch for player " + packet.getData());

                String[] data = packet.getData().split(",");
                String playerName = data[0];
                String to = data[1];
                String from = data[2];

                Bukkit.getPluginManager().callEvent(new CloudPlayerSwitchServerEvent(playerName, from, to));
            });


        }, ChannelActiveEvent.class);

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
