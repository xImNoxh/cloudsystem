package de.polocloud.signs.plugin.listeners;

import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.player.CloudPlayerDisconnectEvent;
import de.polocloud.api.event.impl.player.CloudPlayerSwitchServerEvent;
import de.polocloud.api.event.impl.template.TemplateMaintenanceChangeEvent;
import de.polocloud.api.event.impl.server.GameServerStatusChangeEvent;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.signs.bootstraps.PluginBootstrap;
import de.polocloud.signs.sign.base.IGameServerSign;

import java.util.List;
import java.util.stream.Collectors;

public class CollectivesCloudListener implements IListener {

    @EventHandler
    public void handleChange(GameServerStatusChangeEvent event) {
        if(event.getStatus().equals(GameServerStatus.AVAILABLE)){
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
            PluginBootstrap.getInstance().getSignService().getGameServerSignManager().updateSignsGameServer(event.getTarget());
        }
        if(event.getFrom() != null){
            PluginBootstrap.getInstance().getSignService().getGameServerSignManager().updateSignsGameServer(event.getFrom());
        }
    }

    @EventHandler
    public void handle(TemplateMaintenanceChangeEvent event) {
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
