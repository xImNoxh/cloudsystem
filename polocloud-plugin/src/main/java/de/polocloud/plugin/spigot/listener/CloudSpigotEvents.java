package de.polocloud.plugin.spigot.listener;

import de.polocloud.api.network.protocol.packet.gameserver.GameServerControlPlayerPacket;
import de.polocloud.plugin.CloudBootstrap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class CloudSpigotEvents implements Listener {

    private CloudBootstrap bootstrap;

    public CloudSpigotEvents(CloudBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }


    @EventHandler
    public void handle(PlayerLoginEvent event) {

        bootstrap.sendPacket(new GameServerControlPlayerPacket(event.getPlayer().getUniqueId()));


    }

}
