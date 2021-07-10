package de.polocloud.api.network.protocol.packet.master;

import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.template.GameServerVersion;

public class MasterRequestServerStartPacket implements IPacket {

    private String template;
    private GameServerVersion version;
    private long snowflake;
    private boolean isProxy;

    public MasterRequestServerStartPacket() {
    }

    public MasterRequestServerStartPacket(String template, GameServerVersion version, long snowflake, boolean isProxy) {
        this.template = template;
        this.version = version;
        this.snowflake = snowflake;
        this.isProxy = isProxy;
    }

    public GameServerVersion getVersion() {
        return version;
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
