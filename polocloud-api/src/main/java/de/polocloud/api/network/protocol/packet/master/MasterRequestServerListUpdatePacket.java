package de.polocloud.api.network.protocol.packet.master;

import de.polocloud.api.network.protocol.packet.IPacket;

public class MasterRequestServerListUpdatePacket implements IPacket {

    private String host;
    private int port;
    private long snowflake;
    public MasterRequestServerListUpdatePacket() {
    }
    public MasterRequestServerListUpdatePacket(String host, int port, long snowflake) {
        this.host = host;
        this.port = port;
        this.snowflake = snowflake;
    }

    public long getSnowflake() {
        return snowflake;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }
}
