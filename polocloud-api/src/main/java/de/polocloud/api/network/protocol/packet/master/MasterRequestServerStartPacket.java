package de.polocloud.api.network.protocol.packet.master;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
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
    private int port;

    public MasterRequestServerStartPacket() {
        
    }

    public MasterRequestServerStartPacket(int port, String template, GameServerVersion version, long snowflake, boolean isProxy, int memory, int maxPlayers, String serverName, String motd, boolean isStatic) {
        this.template = template;
        this.version = version;
        this.snowflake = snowflake;
        this.isProxy = isProxy;
        this.memory = memory;
        this.maxPlayers = maxPlayers;
        this.serverName = serverName;
        this.motd = motd;
        this.isStatic = isStatic;
        this.port = port;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(template);
        buf.writeString(version.getTitle());
        buf.writeLong(snowflake);
        buf.writeBoolean(isProxy);
        buf.writeInt(memory);
        buf.writeInt(maxPlayers);
        buf.writeString(serverName);
        buf.writeString(motd);
        buf.writeBoolean(this.isStatic);
        buf.writeInt(port);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        template = buf.readString();
        version = GameServerVersion.getVersion(buf.readString());
        snowflake = buf.readLong();
        isProxy = buf.readBoolean();
        memory = buf.readInt();
        maxPlayers = buf.readInt();
        serverName = buf.readString();
        motd = buf.readString();
        isStatic = buf.readBoolean();
        port = buf.readInt();
    }

    public int getPort() {
        return port;
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
