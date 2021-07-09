package de.polocloud.plugin.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class TestListener implements Listener {

    @EventHandler
    public void handle(ServerConnectEvent event) {
        System.out.println("handle server connect event !!!");
        for (String s : ProxyServer.getInstance().getServers().keySet()) {
            if (!s.equalsIgnoreCase("lobby")) {
                event.setTarget(ProxyServer.getInstance().getServerInfo(s));
                return;
            }
        }

    }

}
