package de.polocloud.plugin.bootstrap.spigot.events;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.network.packets.gameserver.GameServerCloudCommandExecutePacket;
import de.polocloud.api.network.packets.gameserver.GameServerControlPlayerPacket;
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
import org.bukkit.plugin.Plugin;

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

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void handle(ServerListPingEvent event) {
        event.setMaxPlayers(PoloCloudAPI.getInstance().getGameServerManager().getThisService().getMaxPlayers());
        event.setMotd(PoloCloudAPI.getInstance().getGameServerManager().getThisService().getMotd());
    }

    @EventHandler
    public void handle(PlayerLoginEvent event) {
        if (event.getResult().equals(PlayerLoginEvent.Result.KICK_FULL)) event.allow();

        Player player = event.getPlayer();

        if (PoloCloudAPI.getInstance().getGameServerManager().getThisService().getTemplate().isMaintenance() && !player.hasPermission("*") && !player.hasPermission("cloud.maintenance")) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, property.getGameServerMaintenanceMessage());
            return;
        }

        if (Bukkit.getOnlinePlayers().size() >= PoloCloudAPI.getInstance().getGameServerManager().getThisService().getMaxPlayers() && !player.hasPermission("*") && !player.hasPermission("cloud.fulljoin")) {
            event.disallow(PlayerLoginEvent.Result.KICK_FULL, property.getGameServerMaxPlayersMessage());
            return;
        }

        networkClient.sendPacket(new GameServerControlPlayerPacket(player.getUniqueId()));
    }

    @EventHandler
    public void onCmd(PlayerCommandPreprocessEvent event) {

        try {
            if (PoloCloudAPI.getInstance().getCommandManager().runCommand(event.getMessage(), PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(event.getPlayer().getName()))) {
                event.setCancelled(true);
                networkClient.sendPacket(new GameServerCloudCommandExecutePacket(event.getPlayer().getUniqueId(), event.getMessage().substring(1)));
            } else {
                event.setCancelled(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
