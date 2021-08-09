package de.polocloud.plugin.bootstrap.spigot.events;

import de.polocloud.api.network.protocol.packet.gameserver.GameServerCloudCommandExecutePacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerControlPlayerPacket;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.property.GameServerProperty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.help.HelpTopic;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class CollectiveSpigotEvents implements Listener {

    private CloudPlugin cloudPlugin;
    private Plugin plugin;

    private GameServerProperty property;
    private NetworkClient networkClient;

    public CollectiveSpigotEvents(Plugin plugin) {
        this.plugin = plugin;
        this.cloudPlugin = CloudPlugin.getCloudPluginInstance();

        this.property = CloudPlugin.getCloudPluginInstance().getGameServerProperty();
        this.networkClient = CloudPlugin.getCloudPluginInstance().getNetworkClient();

        Bukkit.getPluginManager().registerEvents(this,plugin);
    }

    @EventHandler
    public void handle(ServerListPingEvent event) {
        event.setMaxPlayers(cloudPlugin.thisService().getMaxPlayers());
        event.setMotd(cloudPlugin.thisService().getMotd());
    }

    @EventHandler
    public void handle(PlayerLoginEvent event) {
        if (event.getResult().equals(PlayerLoginEvent.Result.KICK_FULL)) event.allow();

        Player player = event.getPlayer();

        if (cloudPlugin.thisService().getTemplate().isMaintenance() && !player.hasPermission("*") && !player.hasPermission("cloud.maintenance")) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, property.getGameServerMaintenanceMessage());
            return;
        }

        if (Bukkit.getOnlinePlayers().size() >= cloudPlugin.thisService().getMaxPlayers() && !player.hasPermission("*") && !player.hasPermission("cloud.fulljoin")) {
            event.disallow(PlayerLoginEvent.Result.KICK_FULL,property.getGameServerMaxPlayersMessage());
            return;
        }

        networkClient.sendPacket(new GameServerControlPlayerPacket(player.getUniqueId()));
    }

    @EventHandler
    public void onCmd(PlayerCommandPreprocessEvent event) {
        List<String> firstArgs = new ArrayList<>();
        for (HelpTopic t : plugin.getServer().getHelpMap().getHelpTopics()) {
            firstArgs.add(t.getName().split(" ")[0].toLowerCase());
        }
        if (firstArgs.contains(event.getMessage().split(" ")[0].toLowerCase())) return;
        if (CloudPlugin.getCloudPluginInstance().getCommandReader().getAllowedCommands().stream().noneMatch(key ->
            key.equalsIgnoreCase(event.getMessage().substring(1).split(" ")[0]))) {
            return;
        }
        event.setCancelled(true);
        networkClient.sendPacket(new GameServerCloudCommandExecutePacket(event.getPlayer().getUniqueId(), event.getMessage().substring(1)));
    }

}
