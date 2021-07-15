package de.polocloud.plugin.listener;

import de.polocloud.api.network.protocol.packet.gameserver.GameServerControlPlayerPacket;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.protocol.NetworkClient;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.Plugin;

public class CollectiveSpigotEvents implements Listener {

    private NetworkClient networkClient;

    public CollectiveSpigotEvents(Plugin plugin, NetworkClient networkClient) {
        this.networkClient = networkClient;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void handle(ServerListPingEvent event){
        event.setMaxPlayers(CloudPlugin.getInstance().getMaxPlayerProperty().getMaxPlayers());
    }

    @EventHandler
    public void handle(PlayerLoginEvent event) {

        if(event.getResult().equals(PlayerLoginEvent.Result.KICK_FULL)) event.allow();

        if (CloudPlugin.getInstance().getState() == null) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "");
            return;
        }

        Player player = event.getPlayer();

        if (CloudPlugin.getInstance().getState().isMaintenance() && !player.hasPermission("*") && !player.hasPermission("cloud.maintenance")) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, CloudPlugin.getInstance().getState().getKickMessage());
            return;
        }

        if(Bukkit.getOnlinePlayers().size() >= CloudPlugin.getInstance().getMaxPlayerProperty().getMaxPlayers() && !player.hasPermission("*") && !player.hasPermission("cloud.fulljoin")){
            event.disallow(PlayerLoginEvent.Result.KICK_FULL, CloudPlugin.getInstance().getMaxPlayerProperty().getMessage());
        }

        networkClient.sendPacket(new GameServerControlPlayerPacket(player.getUniqueId()));
    }

}
