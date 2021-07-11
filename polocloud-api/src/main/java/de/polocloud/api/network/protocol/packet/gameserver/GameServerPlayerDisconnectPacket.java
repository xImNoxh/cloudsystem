package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.packet.IPacket;

import java.util.UUID;

public class GameServerPlayerDisconnectPacket implements IPacket {

    private UUID uuid;


    public GameServerPlayerDisconnectPacket(){

    }
    public GameServerPlayerDisconnectPacket(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}
