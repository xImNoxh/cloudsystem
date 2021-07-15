package de.polocloud.plugin.bootstrap.events;

import de.polocloud.plugin.api.spigot.event.CloudServerStartedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TestcloudEvents implements Listener {

    @EventHandler
    public void handle(CloudServerStartedEvent event){
        System.out.println("Server " + event.getGameServer().getName() + "/" + event.getGameServer().getSnowflake() + " started successfully!");
    }

}
