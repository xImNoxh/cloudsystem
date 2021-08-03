package de.polocloud.plugin.bootstrap;

import de.polocloud.api.event.EventHandler;
import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.event.channel.ChannelActiveEvent;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.api.CloudExecutor;
import de.polocloud.plugin.api.spigot.event.*;
import de.polocloud.plugin.commands.CommandReader;
import de.polocloud.plugin.function.BootstrapFunction;
import de.polocloud.plugin.function.NetworkRegisterFunction;
import de.polocloud.plugin.listener.CollectiveSpigotEvents;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.register.NetworkPluginRegister;
import de.polocloud.plugin.protocol.register.NetworkSpigotRegister;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotBootstrap extends JavaPlugin implements BootstrapFunction, NetworkRegisterFunction {

    private CommandReader commandReader;

    @Override
    public void onEnable() {
        this.commandReader = new CommandReader();
        new CloudPlugin(this, this);
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
        new NetworkSpigotRegister(networkClient, this);
        new NetworkPluginRegister(networkClient, this);
    }


    @Override
    public void registerEvents(NetworkClient networkClient) {

        EventRegistry.registerListener((EventHandler<ChannelActiveEvent>) event -> {

            CloudExecutor.getInstance().getPubSubManager().subscribe("polo:event:serverStarted", packet -> Bukkit.getScheduler().runTask(this, () -> {
                String serverName = packet.getData();
                CloudExecutor.getInstance().getGameServerManager().getGameServerByName(serverName).thenAccept(server ->
                    Bukkit.getPluginManager().callEvent(new CloudServerStartedEvent(server)));
            }));

            CloudExecutor.getInstance().getPubSubManager().subscribe("polo:event:templateMaintenanceUpdate", packet -> Bukkit.getScheduler().runTask(this, () -> {
                String templateName = packet.getData();
                CloudExecutor.getInstance().getTemplateService().getTemplateByName(templateName).thenAccept(template ->
                    Bukkit.getPluginManager().callEvent(new TemplateMaintenanceUpdateEvent(template)));
            }));

            CloudExecutor.getInstance().getPubSubManager().subscribe("polo:event:serverStopped", packet -> Bukkit.getScheduler().runTask(this, () -> {
                String serverName = packet.getData();
                CloudExecutor.getInstance().getGameServerManager().getGameServerByName(serverName).thenAccept(server ->
                    Bukkit.getPluginManager().callEvent(new CloudServerStoppedEvent(server)));
            }));

            CloudExecutor.getInstance().getPubSubManager().subscribe("polo:event:serverUpdated", packet -> Bukkit.getScheduler().runTask(this, () -> {
                String serverName = packet.getData();
                CloudExecutor.getInstance().getGameServerManager().getGameServerByName(serverName).thenAccept(server ->
                    Bukkit.getPluginManager().callEvent(new CloudServerUpdatedEvent(server)));
            }));


            CloudExecutor.getInstance().getPubSubManager().subscribe("polo:event:playerJoin", packet -> Bukkit.getScheduler().runTask(this, () -> {
                String playerName = packet.getData();
                Bukkit.getPluginManager().callEvent(new CloudPlayerJoinNetworkEvent(playerName));
            }));

            CloudExecutor.getInstance().getPubSubManager().subscribe("polo:event:playerQuit", packet -> Bukkit.getScheduler().runTask(this, () -> {
                String playerName = packet.getData();
                Bukkit.getPluginManager().callEvent(new CloudPlayerQuitNetworkEvent(playerName));
            }));

            CloudExecutor.getInstance().getPubSubManager().subscribe("polo:event:playerSwitch", packet -> Bukkit.getScheduler().runTask(this, () -> {
                String[] data = packet.getData().split(",");
                String playerName = data[0];
                String to = data[1];
                String from = data[2];
                Bukkit.getPluginManager().callEvent(new CloudPlayerSwitchServerEvent(playerName, from, to));
            }));


        }, ChannelActiveEvent.class);

        new CollectiveSpigotEvents(this, networkClient, this);
    }

    @Override
    public void shutdown() {
        Bukkit.shutdown();
    }

    public CommandReader getCommandReader() {
        return commandReader;
    }

}
