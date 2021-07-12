package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;

public class GameServerExecuteCommandPacket implements IPacket {

    private String command;

    public GameServerExecuteCommandPacket() {
    }

    public GameServerExecuteCommandPacket(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
