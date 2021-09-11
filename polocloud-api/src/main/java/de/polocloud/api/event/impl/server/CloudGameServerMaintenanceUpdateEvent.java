package de.polocloud.api.event.impl.server;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.base.EventData;
import de.polocloud.api.event.base.CloudEvent;
import de.polocloud.api.template.base.ITemplate;

@EventData(nettyFire = true)
public class CloudGameServerMaintenanceUpdateEvent extends CloudEvent {

    private final String template;
    private final boolean maintenance;

    public CloudGameServerMaintenanceUpdateEvent(ITemplate template, boolean maintenance) {
        this.template = template.getName();
        this.maintenance = maintenance;
    }

    public boolean isMaintenance() {
        return maintenance;
    }

    public ITemplate getTemplate() {
        return PoloCloudAPI.getInstance().getTemplateManager().getTemplate(template);
    }

}
