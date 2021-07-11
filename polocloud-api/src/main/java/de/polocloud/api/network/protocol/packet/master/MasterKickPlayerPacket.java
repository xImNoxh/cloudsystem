package de.polocloud.api.network.protocol.packet.master;

import de.polocloud.api.network.protocol.packet.IPacket;

import java.util.UUID;

public class MasterKickPlayerPacket implements IPacket {

    private UUID uuid;
    private String message;

    public MasterKickPlayerPacket() {

    }

    public MasterKickPlayerPacket(UUID uuid, String message) {
        this.uuid = uuid;
        this.message = message;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getMessage() {
        return message;
    }
}
