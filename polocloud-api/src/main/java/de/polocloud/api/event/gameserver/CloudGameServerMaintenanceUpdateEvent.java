package de.polocloud.api.event.gameserver;

import de.polocloud.api.event.CloudEvent;
import de.polocloud.api.template.ITemplate;

public class CloudGameServerMaintenanceUpdateEvent implements CloudEvent {

    private ITemplate Template;

    public CloudGameServerMaintenanceUpdateEvent(ITemplate Template) {
        this.Template = Template;
    }

    public ITemplate getTemplate() {
        return Template;
    }

}
