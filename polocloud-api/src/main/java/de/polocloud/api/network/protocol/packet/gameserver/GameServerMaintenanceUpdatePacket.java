package de.polocloud.api.network.protocol.packet.gameserver;

import com.google.inject.Inject;
import de.polocloud.api.network.protocol.packet.IPacket;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class GameServerMaintenanceUpdatePacket extends IPacket {

    private boolean state;
    private String message;

    public GameServerMaintenanceUpdatePacket() {
    }

    public GameServerMaintenanceUpdatePacket(boolean state, String message) {
        this.state = state;
        this.message = message;
    }

    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        byteBuf.writeBoolean(state);
        writeString(byteBuf, message);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        state = byteBuf.readBoolean();
        message = readString(byteBuf);
    }

    public String getMessage() {
        return message;
    }

    public boolean isState() {
        return state;
    }

}
