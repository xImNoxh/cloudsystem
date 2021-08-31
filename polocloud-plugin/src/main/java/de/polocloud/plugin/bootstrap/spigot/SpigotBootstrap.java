package de.polocloud.plugin.bootstrap.spigot;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.event.impl.net.ChannelActiveEvent;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.packets.api.PublishPacket;
import de.polocloud.api.template.ITemplateManager;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.api.spigot.events.*;
import de.polocloud.plugin.bootstrap.IBootstrap;
import de.polocloud.plugin.bootstrap.spigot.events.CollectiveSpigotEvents;
import de.polocloud.plugin.bootstrap.spigot.register.SpigotPacketRegister;
import org.bukkit.Bukkit;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.Consumer;

public class SpigotBootstrap extends JavaPlugin implements IBootstrap {

    private CloudPlugin cloudPlugin;

    @Override
    public synchronized void onLoad() {
        this.cloudPlugin = new CloudPlugin(this);
    }

    @Override
    public synchronized void onEnable() {
        this.cloudPlugin.onEnable();
    }

    @Override
    public synchronized void onDisable() {

    }

    @Override
    public void shutdown() {
        Bukkit.shutdown();
    }

    @Override
    public int getPort() {
        return Bukkit.getPort();
    }

    @Override
    public void kick(UUID uuid, String message) {
        Bukkit.getPlayer(uuid).kickPlayer(message);
    }

    @Override
    public void executeCommand(String command) {
        Bukkit.getScheduler().runTask(this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
    }

    @Override
    public void registerListeners() {
        IGameServerManager gameServerManager = PoloCloudAPI.getInstance().getGameServerManager();
        ITemplateManager templateService = PoloCloudAPI.getInstance().getTemplateManager();
        PluginManager manager = Bukkit.getPluginManager();

        PoloCloudAPI.getInstance().getEventManager().registerHandler(ChannelActiveEvent.class, event -> {
            subscribe("polo:event:serverStarted", packet -> gameServerManager.getCached(packet.getData()).thenAccept(server ->
                manager.callEvent(new CloudServerStartedEvent(server))));
            subscribe("polo:event:templateMaintenanceUpdate", packet -> templateService.getTemplate(packet.getData()).thenAccept(template ->
                manager.callEvent(new TemplateMaintenanceUpdateEvent(template))));
            subscribe("polo:event:serverStopped", packet -> gameServerManager.getCached(packet.getData()).thenAccept(server ->
                manager.callEvent(new CloudServerStoppedEvent(server))));
            subscribe("polo:event:serverUpdated", packet -> gameServerManager.getCached(packet.getData()).thenAccept(server ->
                manager.callEvent(new CloudServerUpdatedEvent(server))));
            subscribe("polo:event:playerJoin", packet -> manager.callEvent(new CloudPlayerJoinNetworkEvent(packet.getData())));
            subscribe("polo:event:playerQuit", packet -> manager.callEvent(new CloudPlayerQuitNetworkEvent(packet.getData())));
            subscribe("polo:event:playerSwitch", packet -> {
                String[] data = packet.getData().split(",");
                Bukkit.getPluginManager().callEvent(new CloudPlayerSwitchServerEvent(data[0], data[2], data[1]));
            });
        });
        new CollectiveSpigotEvents(this);
    }

    @Override
    public PoloType getType() {
        return PoloType.PLUGIN_SPIGOT;
    }

    public void subscribe(String id, Consumer<PublishPacket> call) {
        try {
            if (isEnabled()) {
                PoloCloudAPI.getInstance().getPubSubManager().subscribe(id, publishPacket -> Bukkit.getScheduler().runTask(this, () -> call.accept(publishPacket)));
            }
        } catch (IllegalPluginAccessException exception) {
            System.out.println("Event subscribe illegal plugin access exception " + Arrays.toString(exception.getStackTrace()));
            //TODO Bugfix with shutdown
        }
    }

    @Override
    public void registerPacketListening() {
        new SpigotPacketRegister(CloudPlugin.getCloudPluginInstance());
    }
}
