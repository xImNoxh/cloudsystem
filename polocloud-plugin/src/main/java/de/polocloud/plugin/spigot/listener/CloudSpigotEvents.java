package de.polocloud.plugin.spigot.listener;

import de.polocloud.plugin.spigot.PoloCloudPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class CloudSpigotEvents implements Listener {

    @EventHandler
    public void handle(PlayerLoginEvent event) {

        synchronized (PoloCloudPlugin.proxyListLOCK){
            if(!PoloCloudPlugin.allowedProxies.contains(event.getAddress().getHostName())){
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cPlease connect to the Proxy!");
                return;
            }
        }


    }

}
