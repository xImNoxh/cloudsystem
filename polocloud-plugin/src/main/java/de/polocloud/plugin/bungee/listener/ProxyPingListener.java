package de.polocloud.plugin.bungee.listener;

import de.polocloud.plugin.bungee.PoloCloudPlugin;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ProxyPingListener implements Listener {

    private PoloCloudPlugin plugin;

    public ProxyPingListener(PoloCloudPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void handlePing(ProxyPingEvent event){
        event.getResponse().setDescription(plugin.getFirstMotd() + plugin.getSecondMotd());
    }

}
