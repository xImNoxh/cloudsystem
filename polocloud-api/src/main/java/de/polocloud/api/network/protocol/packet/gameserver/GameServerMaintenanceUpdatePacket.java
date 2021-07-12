package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.packet.IPacket;

public class GameServerMaintenanceUpdatePacket implements IPacket {

    private boolean state;
    private String message;

    public GameServerMaintenanceUpdatePacket() {
    }

    public GameServerMaintenanceUpdatePacket(boolean state, String message) {
        this.state = state;
        this.message = message;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isState() {
        return state;
    }
}
