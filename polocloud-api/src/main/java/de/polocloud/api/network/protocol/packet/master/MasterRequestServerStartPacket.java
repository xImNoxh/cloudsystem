package de.polocloud.api.network.protocol.packet.master;

import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.template.GameServerVersion;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class MasterRequestServerStartPacket extends Packet {

    private String template;
    private GameServerVersion version;
    private long snowflake;
    private boolean isProxy;
    private int memory, maxPlayers;
    private String serverName;
    private String motd;
    private boolean isStatic;

    public MasterRequestServerStartPacket() {
        
    }

    public MasterRequestServerStartPacket(String template, GameServerVersion version, long snowflake, boolean isProxy, int memory, int maxPlayers, String serverName, String motd, boolean isStatic) {
        this.template = template;
        this.version = version;
        this.snowflake = snowflake;
        this.isProxy = isProxy;
        this.memory = memory;
        this.maxPlayers = maxPlayers;
        this.serverName = serverName;
        this.motd = motd;
        this.isStatic = isStatic;
    }

    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, template);
        writeString(byteBuf, version.getTitle());
        byteBuf.writeLong(snowflake);
        byteBuf.writeBoolean(isProxy);
        byteBuf.writeInt(memory);
        byteBuf.writeInt(maxPlayers);
        writeString(byteBuf, serverName);
        writeString(byteBuf, motd);
        byteBuf.writeBoolean(this.isStatic);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        template = readString(byteBuf);
        version = GameServerVersion.getVersion(readString(byteBuf));
        snowflake = byteBuf.readLong();
        isProxy = byteBuf.readBoolean();
        memory = byteBuf.readInt();
        maxPlayers = byteBuf.readInt();
        serverName = readString(byteBuf);
        motd = readString(byteBuf);
        this.isStatic = byteBuf.readBoolean();
    }

    public boolean isStatic() {
        return isStatic;
    }

    public String getMotd() {
        return motd;
    }

    public int getMemory() {
        return memory;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public GameServerVersion getVersion() {
        return version;
    }

    public String getTemplate() {
        return template;
    }

    public String getServerName() {
        return serverName;
    }

    public boolean isProxy() {
        return isProxy;
    }

    public long getSnowflake() {
        return snowflake;
    }

}
