package de.polocloud.signs.plugin.listeners;

import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.player.CloudPlayerDisconnectEvent;
import de.polocloud.api.event.impl.player.CloudPlayerSwitchServerEvent;
import de.polocloud.api.event.impl.server.CloudGameServerMaintenanceUpdateEvent;
import de.polocloud.api.event.impl.server.CloudGameServerStatusChangeEvent;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.signs.bootstraps.PluginBootstrap;
import de.polocloud.signs.sign.base.IGameServerSign;

import java.util.List;
import java.util.stream.Collectors;

public class CollectivesCloudListener implements IListener {

    @EventHandler
    public void handleChange(CloudGameServerStatusChangeEvent event) {
        if(event.getStatus().equals(GameServerStatus.AVAILABLE)){
            System.out.println("new start " + event.getGameServer().getName());
            if(PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getSignByGameServer(event.getGameServer()) == null){
                PluginBootstrap.getInstance().getSignService().getGameServerSignManager().updateSignsGameServer(PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getFreeGameServerSign(event.getGameServer()), event.getGameServer());
            }
        }else if(event.getStatus().equals(GameServerStatus.STOPPING)){
            PluginBootstrap.getInstance().getSignService().getGameServerSignManager().setSignToStopped(event.getGameServer());
        }
    }

    @EventHandler
    public void handle(CloudPlayerSwitchServerEvent event) {
        if(event.getTarget() != null){
            System.out.println("update to: " + event.getFrom().getName());
            PluginBootstrap.getInstance().getSignService().getGameServerSignManager().updateSignsGameServer(event.getTarget());
        }else{
            System.out.println("null");
        }
        if(event.getFrom() != null){
            System.out.println("update from: " + event.getFrom().getName());
            PluginBootstrap.getInstance().getSignService().getGameServerSignManager().updateSignsGameServer(event.getFrom());
        }else{
            System.out.println("null");
        }
    }

    @EventHandler
    public void handle(CloudGameServerMaintenanceUpdateEvent event) {
        List<IGameServerSign> toUpdate = PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getLoadedSigns().stream().filter(sign -> sign.getTemplate().getName().equals(event.getTemplate().getName())).collect(Collectors.toList());
        for (IGameServerSign gameServerSign : toUpdate) {
            gameServerSign.setTemplate(event.getTemplate());
            gameServerSign.writeSign(false);
        }
    }

    @EventHandler
    public void handle(CloudPlayerDisconnectEvent event) {
        if(event.getPlayer() != null && event.getPlayer().getMinecraftServer() != null){
            PluginBootstrap.getInstance().getSignService().getGameServerSignManager().updateSignsGameServer(event.getPlayer().getMinecraftServer());
        }
    }

}
