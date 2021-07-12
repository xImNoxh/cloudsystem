package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.packet.IPacket;

import java.util.UUID;

public class GameServerPlayerUpdatePacket implements IPacket {

    private UUID uuid;
    private String name;

    private String targetServer;

    public GameServerPlayerUpdatePacket(){

    }

    public GameServerPlayerUpdatePacket(UUID uuid, String name, String targetServer) {
        this.uuid = uuid;
        this.name = name;
        this.targetServer = targetServer;
    }

    public String getName() {
        return name;
    }

    public String getTargetServer() {
        return targetServer;
    }

    public UUID getUuid() {
        return uuid;
    }
}
