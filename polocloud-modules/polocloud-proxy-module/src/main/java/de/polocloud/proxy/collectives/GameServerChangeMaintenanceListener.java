package de.polocloud.proxy.collectives;

import de.polocloud.api.event.EventHandler;
import de.polocloud.api.event.gameserver.CloudGameServerMaintenanceUpdateEvent;
import de.polocloud.api.template.TemplateType;
import de.polocloud.proxy.ProxyModule;

public class GameServerChangeMaintenanceListener implements EventHandler<CloudGameServerMaintenanceUpdateEvent> {

    @Override
    public void handleEvent(CloudGameServerMaintenanceUpdateEvent event) {
        if (event.getTemplate().getTemplateType() == TemplateType.MINECRAFT) return;
        ProxyModule.getInstance().getCache().stream().filter(key -> key.getTemplate().getName().equalsIgnoreCase(event.getTemplate().getName())).forEach(it -> {
            if (it.getTemplate().isMaintenance()) {
                it.setMotd(ProxyModule.getInstance().getMotdInfoService().getMaintenanceMotd());
                return;
            }
            it.setMotd(ProxyModule.getInstance().getMotdInfoService().getCurrentMotd());
        });
    }
}
