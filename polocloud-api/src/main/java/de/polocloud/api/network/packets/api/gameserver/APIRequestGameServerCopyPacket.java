package de.polocloud.api.network.packets.api.gameserver;

import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;

@AutoRegistry
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
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(copyType.toString());
        buf.writeString(gameservername);
        buf.writeString(snowflakeID);
        buf.writeString(template);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        this.copyType = APIRequestGameServerCopyPacket.Type.valueOf(buf.readString());
        this.gameservername = buf.readString();
        this.snowflakeID = buf.readString();
        this.template = buf.readString();
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
