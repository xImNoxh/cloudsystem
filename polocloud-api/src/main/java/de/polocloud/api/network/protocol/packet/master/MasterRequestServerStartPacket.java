package de.polocloud.api.network.protocol.packet.master;

import de.polocloud.api.network.protocol.packet.IPacket;

public class MasterRequestServerStartPacket implements IPacket {

    private String template;
    private long snowflake;
    private boolean isProxy;

    public MasterRequestServerStartPacket() {
    }

    public MasterRequestServerStartPacket(String template, long snowflake, boolean isProxy) {
        this.template = template;
        this.snowflake = snowflake;
        this.isProxy = isProxy;
    }

    public String getTemplate() {
        return template;
    }

    public boolean isProxy() {
        return isProxy;
    }

    public long getSnowflake() {
        return snowflake;
    }
}
