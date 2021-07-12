package de.polocloud.api.network.protocol.packet.master;

import de.polocloud.api.network.protocol.packet.IPacket;

import java.util.UUID;

public class MasterPlayerRequestResponsePacket  implements IPacket {

    private UUID uuid;

    private String serviceName;
    private long snowflake;

    public MasterPlayerRequestResponsePacket() {
    }
    public MasterPlayerRequestResponsePacket(UUID uuid, String serviceName, long snowflake) {
        this.uuid = uuid;
        this.serviceName = serviceName;
        this.snowflake = snowflake;
    }

    public String getServiceName() {
        return serviceName;
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getSnowflake() {
        return snowflake;
    }
}
