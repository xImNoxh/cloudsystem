package de.polocloud.api.event.impl.server;

import de.polocloud.api.event.base.EventData;
import de.polocloud.api.event.base.CloudEvent;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.template.SimpleTemplate;
import de.polocloud.api.template.base.ITemplate;

import java.io.IOException;

@EventData(nettyFire = true)
public class CloudGameServerMaintenanceUpdateEvent extends CloudEvent {

    private SimpleTemplate template;

    public CloudGameServerMaintenanceUpdateEvent(ITemplate Template) {
        this.template = (SimpleTemplate) Template;
    }

    public ITemplate getTemplate() {
        return template;
    }

}
