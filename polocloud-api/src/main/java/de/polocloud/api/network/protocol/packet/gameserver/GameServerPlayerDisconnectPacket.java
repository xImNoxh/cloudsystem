package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.packet.IPacket;

import java.util.UUID;

public class GameServerPlayerDisconnectPacket implements IPacket {

    private UUID uuid;
    private String name;

    public GameServerPlayerDisconnectPacket(){

    }

    public GameServerPlayerDisconnectPacket(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
