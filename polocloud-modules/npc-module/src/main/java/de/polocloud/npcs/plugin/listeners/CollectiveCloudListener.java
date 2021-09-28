package de.polocloud.npcs.plugin.listeners;

import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.player.CloudPlayerDisconnectEvent;
import de.polocloud.api.event.impl.player.CloudPlayerSwitchServerEvent;
import de.polocloud.api.event.impl.server.GameServerStatusChangeEvent;
import de.polocloud.api.util.AutoRegistry;
import de.polocloud.npcs.bootstraps.PluginBootstrap;
import de.polocloud.npcs.npc.base.ICloudNPC;

/**
 * General class for handling PoloCloud events
 */
@AutoRegistry
public class CollectiveCloudListener implements IListener {

    /**
     *  Handles the PoloCloud-{@link GameServerStatusChangeEvent}
     *  when a gameserver stops or starts all NPCs from this gameserver and its template
     *  will be updated
     */
    @EventHandler
    public void handle(GameServerStatusChangeEvent event){
        for (ICloudNPC npc : PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getAllNPCsFromTemplateOrGameServerName(event.getGameServer().getName())) {
            npc.update();
        }
        for (ICloudNPC npc : PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getAllNPCsFromTemplateOrGameServerName(event.getGameServer().getTemplate().getName())) {
            npc.update();
        }
    }

    /**
     *  Handles the PoloCloud-{@link CloudPlayerSwitchServerEvent}
     *  when a player switches or joins a server, all NPCs will be updated
     */
    @EventHandler
    public void handle(CloudPlayerSwitchServerEvent event){
        for (ICloudNPC npc : PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getAllNPCsFromTemplateOrGameServerName(event.getFrom().getName())) {
            npc.update();
        }
        for (ICloudNPC npc : PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getAllNPCsFromTemplateOrGameServerName(event.getTarget().getName())) {
            npc.update();
        }
    }

    /**
     *  Handles the PoloCloud-{@link CloudPlayerDisconnectEvent}
     *  when a player leaves the cloud, all NPCs will be updated
     */
    @EventHandler
    public void handle(CloudPlayerDisconnectEvent event){
        if (event.getPlayer() != null)
            for (ICloudNPC npc : PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getAllNPCsFromTemplateOrGameServerName(event.getPlayer().getMinecraftServer().getName())) {
                npc.update();
            }
    }

}
