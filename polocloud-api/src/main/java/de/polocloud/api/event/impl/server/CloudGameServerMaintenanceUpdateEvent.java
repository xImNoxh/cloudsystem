package de.polocloud.api.event.impl.server;

import de.polocloud.api.event.base.IEvent;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.template.ITemplate;

import java.io.IOException;

public class CloudGameServerMaintenanceUpdateEvent implements IEvent {

    private ITemplate template;

    public CloudGameServerMaintenanceUpdateEvent(ITemplate Template) {
        this.template = Template;
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        this.template = buf.readTemplate();
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeTemplate(template);
    }

    public ITemplate getTemplate() {
        return template;
    }

}
