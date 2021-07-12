package de.polocloud.plugin.listener;

import de.polocloud.api.network.protocol.packet.gameserver.GameServerControlPlayerPacket;
import de.polocloud.plugin.protocol.NetworkClient;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;

public class CollectiveSpigotEvents implements Listener {

    private NetworkClient networkClient;

    public CollectiveSpigotEvents(Plugin plugin, NetworkClient networkClient) {
        this.networkClient = networkClient;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void handle(PlayerLoginEvent event) {
        networkClient.sendPacket(new GameServerControlPlayerPacket(event.getPlayer().getUniqueId()));
    }

}
