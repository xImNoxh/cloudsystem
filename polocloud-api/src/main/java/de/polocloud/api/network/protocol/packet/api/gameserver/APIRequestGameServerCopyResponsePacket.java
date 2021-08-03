package de.polocloud.api.network.protocol.packet.api.gameserver;

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
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, gameservername);
        writeString(byteBuf, String.valueOf(failed));
        writeString(byteBuf, errorMessage);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        this.gameservername = readString(byteBuf);
        this.failed = Boolean.parseBoolean(readString(byteBuf));
        this.errorMessage = readString(byteBuf);
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

