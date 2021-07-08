package de.polocloud.api.network.protocol.packet;

public class StartServerPacket implements IPacket {

    private String templateName;

    public StartServerPacket() {

    }

    public StartServerPacket(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return this.templateName;
    }

}
