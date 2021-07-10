package de.polocloud.api.network.protocol.packet.master;

import de.polocloud.api.network.protocol.packet.IPacket;

import java.util.UUID;

public class MasterPlayerRequestResponsePacket  implements IPacket {

    private UUID uuid;
    private long snowflake;

    public MasterPlayerRequestResponsePacket() {
    }
    public MasterPlayerRequestResponsePacket(UUID uuid, long snowflake) {
        this.uuid = uuid;
        this.snowflake = snowflake;
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getSnowflake() {
        return snowflake;
    }
}
