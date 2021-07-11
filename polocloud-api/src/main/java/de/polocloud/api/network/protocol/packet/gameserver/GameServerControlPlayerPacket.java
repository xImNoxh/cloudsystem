package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.packet.IPacket;

import java.util.UUID;

public class GameServerControlPlayerPacket implements IPacket {

    private UUID uuid;

    public GameServerControlPlayerPacket(){

    }

    public GameServerControlPlayerPacket(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}
