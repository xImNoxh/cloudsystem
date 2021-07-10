package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.packet.IPacket;

import java.util.UUID;

public class GameServerPlayerRequestJoinPacket implements IPacket {

    private UUID uuid;


    public GameServerPlayerRequestJoinPacket() {
    }
    public GameServerPlayerRequestJoinPacket(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}
