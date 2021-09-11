package de.polocloud.modules.proxy.motd.events;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.modules.proxy.motd.MotdProxyService;
import de.polocloud.modules.proxy.motd.properties.MotdVersionProperty;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.UUID;

public class ProxyEvents implements Listener {

    @EventHandler (priority = EventPriority.HIGH)
    public void handle(ProxyPingEvent event){

        MotdVersionProperty property = MotdProxyService.getInstance().getProperty();
        IGameServer thisService = PoloCloudAPI.getInstance().getGameServerManager().getThisService();
        if (property == null || thisService == null) {
            return;
        }

        ServerPing response = event.getResponse();

        if (thisService.getTemplate().isMaintenance()) {
            if (property.getMaintenanceVersionInfo() != null) {
                response.setVersion(new ServerPing.Protocol(property.getMaintenanceVersionInfo(), -1));
            }

            if (property.getMaintenancePlayerInfo() != null && property.getMaintenancePlayerInfo().length > 0) {

                String[] maintenancePlayerInfo = property.getMaintenancePlayerInfo();
                ServerPing.Players players = response.getPlayers();
                ServerPing.PlayerInfo[] sample = new ServerPing.PlayerInfo[maintenancePlayerInfo.length];

                for (int i = 0; i < maintenancePlayerInfo.length; i++) {
                    sample[i] = new ServerPing.PlayerInfo(maintenancePlayerInfo[i], UUID.randomUUID());
                }

                players.setSample(sample);

                response.setPlayers(players);
            }

        } else{
            if (property.getVersionInfo() != null) {
                response.setVersion(new ServerPing.Protocol(property.getVersionInfo(), -1));
            }
        }


        event.setResponse(response);
    }

}
