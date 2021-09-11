package de.polocloud.modules.hubcommand.events;

import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.server.CloudGameServerStatusChangeEvent;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.modules.hubcommand.HubCommandModule;

/**
 * General class for handling PoloCloud events
 */
public class CloudCollectiveEvents implements IListener {

    /**
     *  Handles the PoloCloud-{@link CloudGameServerStatusChangeEvent}
     *  when a gameserver with the {@link TemplateType#PROXY} connects
     *  the modules sends the Config to the Proxies
     */
    @EventHandler
    public void handle(CloudGameServerStatusChangeEvent event){
        if(event.getStatus().equals(GameServerStatus.AVAILABLE) && event.getGameServer().getTemplate().getTemplateType().equals(TemplateType.PROXY)) {
            HubCommandModule.getInstance().forceUpdate();
        }
    }
}
