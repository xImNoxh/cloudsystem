package de.polocloud.plugin.listener;

import de.polocloud.api.network.protocol.packet.gameserver.GameServerCloudCommandExecutePacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerControlPlayerPacket;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.bootstrap.SpigotBootstrap;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.property.GameServerProperty;
import de.polocloud.plugin.protocol.property.Property;
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

    private SpigotBootstrap spigotBootstrap;

    private GameServerProperty property;
    private NetworkClient networkClient;


    private Plugin plugin;

    public CollectiveSpigotEvents(Plugin plugin, CloudPlugin cloudPlugin, SpigotBootstrap spigotBootstrap) {
        this.plugin = plugin;
        this.spigotBootstrap = spigotBootstrap;
        this.property = cloudPlugin.getProperty();
        this.networkClient = cloudPlugin.getNetworkClient();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void handle(ServerListPingEvent event) {
        event.setMaxPlayers(property.getGameServerMaxPlayers());
        event.setMotd(property.getGameServerMotd());
    }

    @EventHandler
    public void handle(PlayerLoginEvent event) {

        if (event.getResult().equals(PlayerLoginEvent.Result.KICK_FULL)) event.allow();


        Player player = event.getPlayer();

        if (property.isGameServerInMaintenance() && !player.hasPermission("*") && !player.hasPermission("cloud.maintenance")) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, property.getGameServerMaintenanceMessage());
            return;
        }

        if (Bukkit.getOnlinePlayers().size() >= property.getGameServerMaxPlayers() && !player.hasPermission("*") && !player.hasPermission("cloud.fulljoin")) {
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
        if (spigotBootstrap.getCommandReader().getAllowedCommands().stream().noneMatch(key -> key.equalsIgnoreCase(event.getMessage().substring(1).split(" ")[0]))) {
            return;
        }
        event.setCancelled(true);
        networkClient.sendPacket(new GameServerCloudCommandExecutePacket(event.getPlayer().getUniqueId(), event.getMessage().substring(1)));
    }
}
