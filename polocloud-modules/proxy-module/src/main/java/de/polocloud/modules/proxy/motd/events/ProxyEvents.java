package de.polocloud.modules.proxy.motd.events;

import de.polocloud.modules.proxy.motd.MotdProxyService;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ProxyEvents implements Listener {

    @EventHandler (priority = EventPriority.HIGH)
    public void handle(ProxyPingEvent event){

        if(MotdProxyService.getInstance().getProperty() == null) return;

        ServerPing response = event.getResponse();
        response.setVersion(new ServerPing.Protocol(MotdProxyService.getInstance().getProperty().getVersionInfo(), -1));
        event.setResponse(response);
    }

}
