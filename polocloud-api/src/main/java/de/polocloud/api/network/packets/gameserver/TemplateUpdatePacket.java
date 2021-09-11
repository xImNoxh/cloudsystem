package de.polocloud.api.network.packets.gameserver;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.util.AutoRegistry;

import java.io.IOException;

@AutoRegistry
public class TemplateUpdatePacket extends Packet {

    private ITemplate template;

    public TemplateUpdatePacket(ITemplate template) {
        this.template = template;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeTemplate(template);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        template = buf.readTemplate();
    }

    public ITemplate getTemplate() {
        return template;
    }
}
