package de.polocloud.api.network.protocol.packet.api.gameserver;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class APIRequestGameServerCopyResponsePacket extends Packet {

    private String gameservername;
    private boolean failed;
    private String errorMessage;

    public APIRequestGameServerCopyResponsePacket() {
    }

    public APIRequestGameServerCopyResponsePacket(String gameservername, boolean failed, String errorMessage) {
        this.gameservername = gameservername;
        this.failed = failed;
        this.errorMessage = errorMessage;
    }


    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(gameservername);
        buf.writeString(String.valueOf(failed));
        buf.writeString(errorMessage);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        this.gameservername = buf.readString();
        this.failed = Boolean.parseBoolean(buf.readString());
        this.errorMessage = buf.readString();
    }

    public String getGameservername() {
        return gameservername;
    }

    public boolean isFailed() {
        return failed;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

