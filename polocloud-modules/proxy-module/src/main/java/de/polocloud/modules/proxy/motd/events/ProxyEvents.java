package de.polocloud.modules.proxy.motd.events;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.modules.proxy.motd.MotdProxyService;
import de.polocloud.modules.proxy.motd.properties.MotdVersionProperty;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ProxyEvents implements Listener {

    @EventHandler (priority = EventPriority.HIGH)
    public void handle(ProxyPingEvent event){

        MotdVersionProperty property = MotdProxyService.getInstance().getProperty();
        if(property == null) return;

        ServerPing response = event.getResponse();

        if(PoloCloudAPI.getInstance().getGameServerManager().getThisService().getTemplate().isMaintenance()){
            if(property.getMaintenanceVersionInfo() != null)
            response.setVersion(new ServerPing.Protocol(property.getMaintenanceVersionInfo(), -1));
        }else{
            if(property.getVersionInfo() != null)
            response.setVersion(new ServerPing.Protocol(property.getVersionInfo(), -1));
        }
        event.setResponse(response);
    }

}
