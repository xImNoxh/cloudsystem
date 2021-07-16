package de.polocloud.addons.signs.collectives;

import de.polocloud.addons.signs.bootstrap.Bootstrap;
import de.polocloud.plugin.api.spigot.event.CloudServerStartedEvent;
import de.polocloud.plugin.api.spigot.event.CloudServerStoppedEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CollectiveSignEvents implements Listener {

    public CollectiveSignEvents() {
        Bukkit.getPluginManager().registerEvents(this, Bootstrap.getInstance());
    }

    @EventHandler
    public void handle(CloudServerStoppedEvent event){

    }

    @EventHandler
    public void handle(CloudServerStartedEvent event){

    }

}
