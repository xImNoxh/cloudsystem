package de.polocloud.api.event.impl.template;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.base.CloudEvent;
import de.polocloud.api.event.base.EventData;
import de.polocloud.api.template.base.ITemplate;

@EventData(nettyFire = true)
public class TemplateUpdateEvent extends CloudEvent {

    private final String template;

    public TemplateUpdateEvent(ITemplate template) {
        this.template = template.getName();
    }

    public ITemplate getTemplate() {
        return PoloCloudAPI.getInstance().getTemplateManager().getTemplate(template);
    }

}
