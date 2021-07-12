package de.polocloud.api.network.protocol.packet.master;

import de.polocloud.api.network.protocol.packet.IPacket;

public class MasterRequestServerListUpdatePacket implements IPacket {

    private String name;
    private String host;
    private int port;
    private long snowflake;
    public MasterRequestServerListUpdatePacket() {
    }
    public MasterRequestServerListUpdatePacket(String name, String host, int port, long snowflake) {
        this.host = host;
        this.port = port;
        this.snowflake = snowflake;
        this.name = name;
    }

    public String getName() {
        return name;
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
