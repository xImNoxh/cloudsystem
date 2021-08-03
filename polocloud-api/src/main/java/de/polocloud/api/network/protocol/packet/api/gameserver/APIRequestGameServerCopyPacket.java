package de.polocloud.api.network.protocol.packet.api.gameserver;

import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class APIRequestGameServerCopyPacket extends Packet {

    private APIRequestGameServerCopyPacket.Type copyType;
    private String gameservername;
    private String snowflakeID;
    private String template;

    public APIRequestGameServerCopyPacket() {
    }

    public APIRequestGameServerCopyPacket(APIRequestGameServerCopyPacket.Type copyType, String gameservername, String snowflakeID, String template) {
        this.copyType = copyType;
        this.gameservername = gameservername;
        this.snowflakeID = snowflakeID;
        this.template = template;
    }

    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, copyType.toString());
        writeString(byteBuf, gameservername);
        writeString(byteBuf, snowflakeID);
        writeString(byteBuf, template);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        this.copyType = APIRequestGameServerCopyPacket.Type.valueOf(readString(byteBuf));
        this.gameservername = readString(byteBuf);
        this.snowflakeID = readString(byteBuf);
        this.template = readString(byteBuf);
    }

    public APIRequestGameServerCopyPacket.Type getCopyType() {
        return copyType;
    }

    public String getGameservername() {
        return gameservername;
    }

    public String getSnowflakeID() {
        return snowflakeID;
    }

    public String getTemplate() {
        return template;
    }

    public enum Type {
        WORLD,
        ENTIRE
    }

}
