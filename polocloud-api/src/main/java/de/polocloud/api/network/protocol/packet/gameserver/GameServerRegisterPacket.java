package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.packet.IPacket;

public class GameServerRegisterPacket implements IPacket {

    private long snowflake;
    private int port;

    public GameServerRegisterPacket(long snowflake, int port) {
        this.snowflake = snowflake;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public long getSnowflake() {
        return snowflake;
    }
}
