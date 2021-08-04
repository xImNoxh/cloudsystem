package de.polocloud.plugin.bootstrap;

import de.polocloud.api.event.EventHandler;
import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.event.channel.ChannelActiveEvent;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.api.PublishPacket;
import de.polocloud.api.template.ITemplateService;
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
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

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
    public void registerEvents(CloudPlugin cloudPlugin) {

        IGameServerManager gameServerManager = CloudExecutor.getInstance().getGameServerManager();
        ITemplateService templateService = CloudExecutor.getInstance().getTemplateService();
        PluginManager manager = Bukkit.getPluginManager();

        EventRegistry.registerListener((EventHandler<ChannelActiveEvent>) event -> {

            subscribe("polo:event:serverStarted", packet -> gameServerManager.getGameServerByName(packet.getData()).thenAccept(server ->
                manager.callEvent(new CloudServerStartedEvent(server))));

            subscribe("polo:event:templateMaintenanceUpdate", packet -> templateService.getTemplateByName(packet.getData()).thenAccept(template ->
                manager.callEvent(new TemplateMaintenanceUpdateEvent(template))));

            subscribe("polo:event:serverStopped", packet -> gameServerManager.getGameServerByName(packet.getData()).thenAccept(server ->
                manager.callEvent(new CloudServerStoppedEvent(server))));

            subscribe("polo:event:serverUpdated", packet -> gameServerManager.getGameServerByName(packet.getData()).thenAccept(server ->
                manager.callEvent(new CloudServerUpdatedEvent(server))));

            subscribe("polo:event:playerJoin", packet -> Bukkit.getPluginManager().callEvent(new CloudPlayerJoinNetworkEvent(packet.getData())));

            subscribe("polo:event:playerQuit", packet -> Bukkit.getPluginManager().callEvent(new CloudPlayerQuitNetworkEvent(packet.getData())));

            //data = 0(playername), data = 1(to), data = 2(from)
            subscribe("polo:event:playerSwitch", packet -> {
                String[] data = packet.getData().split(",");
                Bukkit.getPluginManager().callEvent(new CloudPlayerSwitchServerEvent(data[0], data[2], data[1]));
            });
        }, ChannelActiveEvent.class);

        new CollectiveSpigotEvents(this, cloudPlugin, this);
    }

    public void subscribe(String id, Consumer<PublishPacket> call){
        CloudExecutor.getInstance().getPubSubManager().subscribe(id, publishPacket -> Bukkit.getScheduler().runTask(this, () -> call.accept(publishPacket)));
    }

    @Override
    public void shutdown() {
        Bukkit.shutdown();
    }

    public CommandReader getCommandReader() {
        return commandReader;
    }

}
