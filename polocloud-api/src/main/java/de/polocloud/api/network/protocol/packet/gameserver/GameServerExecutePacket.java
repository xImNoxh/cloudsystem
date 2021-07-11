package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.packet.IPacket;

public class GameServerExecutePacket implements IPacket {

    private String command;

    public GameServerExecutePacket() {
    }

    public GameServerExecutePacket(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
