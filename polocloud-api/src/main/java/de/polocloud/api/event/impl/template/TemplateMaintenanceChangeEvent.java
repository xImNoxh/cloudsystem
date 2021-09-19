package de.polocloud.api.event.impl.template;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.base.EventData;
import de.polocloud.api.event.base.CloudEvent;
import de.polocloud.api.template.base.ITemplate;
import lombok.AllArgsConstructor;
import lombok.Data;

@EventData(nettyFire = true)
public class TemplateMaintenanceChangeEvent extends CloudEvent {

    private final String template;
    private final boolean maintenance;

    public TemplateMaintenanceChangeEvent(ITemplate template, boolean maintenance) {
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
