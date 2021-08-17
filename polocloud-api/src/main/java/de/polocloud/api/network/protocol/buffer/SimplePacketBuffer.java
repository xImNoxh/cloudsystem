package de.polocloud.api.network.protocol.buffer;

import com.google.common.base.Charsets;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerShutdownPacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.GameServerVersion;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.TemplateType;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SimplePacketBuffer implements IPacketBuffer {

    private final ByteBuf buf;

    public SimplePacketBuffer(ByteBuf byteBuf) {
        this.buf = byteBuf;
    }

    @Override
    public ByteBuf getBase() {
        return this.buf;
    }

    @Override
    public int readVarInt() {
        int i = 0;
        int j = 0;

        while (true) {
            byte b0 = buf.readByte();
            i |= (b0 & 127) << j++ * 7;

            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
            if ((b0 & 128) != 128) {
                break;
            }
        }

        return i;
    }

    @Override
    public String readString() throws IOException {
        int maxLength = 32767;
        int i = this.readVarInt();

        if (i > maxLength * 4 || i < 0) {
            throw new DecoderException("The received encoded string buffer length is not allowed!");
        } else {
            ByteBuf part = buf.readBytes(i);
            String s = part.toString(Charsets.UTF_8);

            if (s.length() > maxLength) {
                throw new DecoderException("The received string length is longer than maximum allowed (" + i + " > " + maxLength + ")");
            } else {
                return s;
            }
        }
    }

    @Override
    public int readInt() throws IOException {
        return buf.readInt();
    }

    @Override
    public boolean readBoolean() throws IOException {
        return buf.readBoolean();
    }

    @Override
    public double readDouble() throws IOException {
        return buf.readDouble();
    }

    @Override
    public float readFloat() throws IOException {
        return buf.readFloat();
    }

    @Override
    public long readLong() throws IOException {
        return buf.readLong();
    }

    @Override
    public byte readByte() throws IOException {
        return buf.readByte();
    }

    @Override
    public ICloudPlayer readCloudPlayer() throws IOException {
        String name = this.readString();
        UUID uuid = UUID.fromString(this.readString());
        IGameServer proxyServer = this.readGameServer();
        IGameServer minecraftServer = this.readGameServer();

        return new ICloudPlayer() {
            @Override
            public UUID getUUID() {
                return uuid;
            }

            @Override
            public IGameServer getProxyServer() {
                return proxyServer;
            }

            @Override
            public IGameServer getMinecraftServer() {
                return minecraftServer;
            }

            @Override
            public void sendMessage(String message) {
                //TODO
            }

            @Override
            public void sendTo(IGameServer gameServer) {
                //TODO
            }

            @Override
            public void kick(String message) {
                //TODO
            }

            @Override
            public void sendToFallback() {
                //TODO
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public CompletableFuture<Boolean> hasPermissions(String permission) {
                //TODO
                return null;
            }

            @Override
            public void sendTabList(String header, String footer) {
                //TODO
            }
        };
    }

    @Override
    public void writeCloudPlayer(ICloudPlayer cloudPlayer) throws IOException {

        this.writeString(cloudPlayer.getName());
        this.writeString(cloudPlayer.getUUID().toString());
        this.writeGameServer(cloudPlayer.getProxyServer());
        this.writeGameServer(cloudPlayer.getMinecraftServer());
    }

    @Override
    public IGameServer readGameServer() throws IOException {
        String name = readString();
        String motd = readString();
        GameServerStatus status = GameServerStatus.valueOf(readString());
        long snowflake = this.readLong();

        ITemplate template = readTemplate();

        long totalMemory = this.readLong();
        int onlinePlayers = this.readInt();
        int port = this.readInt();
        long ping = this.readLong();

        long startTime = this.readLong();
        int maxPlayers = this.readInt();
        boolean serviceVisibility = this.readBoolean();

        return new IGameServer() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public GameServerStatus getStatus() {
                return status;
            }

            @Override
            public void setStatus(GameServerStatus status) {
                //TODO
            }

            @Override
            public long getSnowflake() {
                return snowflake;
            }

            @Override
            public ITemplate getTemplate() {
                return template;
            }

            @Override
            public List<ICloudPlayer> getCloudPlayers() {
                //TODO
                return null;
            }

            @Override
            public long getTotalMemory() {
                return totalMemory;
            }

            @Override
            public int getOnlinePlayers() {
                return onlinePlayers;
            }

            @Override
            public int getPort() {
                return port;
            }

            @Override
            public long getPing() {
                return ping;
            }

            @Override
            public long getStartTime() {
                return startTime;
            }

            @Override
            public void stop() {
                sendPacket(new GameServerShutdownPacket(name));
            }

            @Override
            public void terminate() {
                //TODO
            }

            @Override
            public void sendPacket(Packet packet) {
                //TODO
            }

            @Override
            public String getMotd() {
                return motd;
            }

            @Override
            public int getMaxPlayers() {
                return maxPlayers;
            }

            @Override
            public void setMotd(String motd) {
                //TODO
            }


            @Override
            public void setMaxPlayers(int players) {
                //TODO
            }

            @Override
            public void setVisible(boolean serviceVisibility) {
                //TODO
            }

            @Override
            public boolean getServiceVisibility() {
                return serviceVisibility;
            }

            @Override
            public void update() {
                //TODO
            }

        };
    }

    @Override
    public void writeGameServer(IGameServer gameServer) throws IOException {
        writeString(gameServer.getName());
        writeString(gameServer.getMotd());
        writeString(gameServer.getStatus().toString());
        this.writeLong(gameServer.getSnowflake());

        writeTemplate(gameServer.getTemplate());

        this.writeLong(gameServer.getTotalMemory());
        this.writeInt(gameServer.getOnlinePlayers());
        this.writeInt(gameServer.getPort());
        this.writeLong(gameServer.getPing());
        this.writeLong(gameServer.getStartTime());
        this.writeInt(gameServer.getMaxPlayers());
        this.writeBoolean(gameServer.getServiceVisibility());
    }

    @Override
    public void writeTemplate(ITemplate template) throws IOException {

        writeString(template.getName());

        this.writeInt(template.getMinServerCount());
        this.writeInt(template.getMaxServerCount());
        this.writeInt(template.getMaxPlayers());

        this.writeInt(template.getMaxMemory());

        writeString(template.getMotd());

        this.writeBoolean(template.isMaintenance());

        writeString(template.getTemplateType().toString());
        writeString(template.getVersion().toString());

        writeStringArray(template.getWrapperNames());
    }

    @Override
    public ITemplate readTemplate() throws IOException {
        String name = readString();

        int minServers = this.readInt();
        int maxServers = this.readInt();

        int maxPlayers = this.readInt();
        int maxMemory = this.readInt();

        String motd = readString();

        boolean maintenance = this.readBoolean();

        TemplateType templateType = TemplateType.valueOf(readString());
        GameServerVersion version = GameServerVersion.valueOf(readString());

        String[] wrapperNames = readStringArray();

        return new ITemplate() {
            @Override
            public int getMinServerCount() {
                return minServers;
            }

            @Override
            public int getMaxServerCount() {
                return maxServers;
            }

            @Override
            public int getMaxPlayers() {
                return maxPlayers;
            }

            @Override
            public int getMaxMemory() {
                return maxMemory;
            }

            @Override
            public String getMotd() {
                return motd;
            }

            @Override
            public boolean isMaintenance() {
                return maintenance;
            }

            @Override
            public void setMaxPlayers(int maxPlayers) {
                //TODO send packet to server ? or block ?
                //TODO
            }

            @Override
            public void setMaintenance(boolean state) {
                //TODO
            }

            @Override
            public TemplateType getTemplateType() {
                return templateType;
            }

            @Override
            public GameServerVersion getVersion() {
                return version;
            }

            @Override
            public int getServerCreateThreshold() {
                //TODO
                return -1;
            }

            @Override
            public String[] getWrapperNames() {
                return wrapperNames;
            }

            @Override
            public boolean isStatic() {
                return false;
            }

            @Override
            public String getName() {
                return name;
            }


        };
    }

    @Override
    public void writeStringArray(String[] arr) throws IOException {
        this.writeInt(arr.length);
        for (String s : arr) {
            writeString(s);
        }
    }

    @Override
    public String[] readStringArray() throws IOException {
        int length = this.readInt();
        String[] array = new String[length];
        for (int i = 0; i < length; i++) {
            array[i] = readString();
        }
        return array;
    }

    @Override
    public <T> T readCustom(Class<T> wrapperClass) throws IOException {
        String clazz = readString();
        String json = readString();
        return new JsonData(json).getAs(wrapperClass);
    }

    @Override
    public UUID readUUID() throws IOException{
        return new UUID(readLong(), readLong());
    }

    @Override
    public <T extends Enum<T>> T readEnum() throws IOException {
        Class<?> enumClass;
        try {
            enumClass = Class.forName(this.readString());
            return (T) enumClass.getEnumConstants()[this.readVarInt()];
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void writeEnum(Enum<?> val) throws IOException {
        this.writeString(val.getDeclaringClass().getName());
        this.writeVarInt(val.ordinal());
    }

    @Override
    public void writeCustom(Object custom) throws IOException {
        this.writeString(custom.getClass().getName());
        this.writeString(new JsonData(custom).toString());
    }

    @Override
    public void writeVarInt(int input) throws IOException {
        while ((input & -128) != 0){
            buf.writeByte(input & 127 | 128);
            input >>>= 7;
        }

        buf.writeByte(input);
    }

    @Override
    public void writeUUID(UUID uuid) throws IOException{
        this.writeLong(uuid.getMostSignificantBits());
        this.writeLong(uuid.getLeastSignificantBits());
    }

    @Override
    public void writeString(String s) throws IOException {
        if(s == null) {
            s = "";
        }

        byte[] bytes = s.getBytes(Charsets.UTF_8);

        if (bytes.length > 32767) {
            throw new EncoderException("String too big (was " + s.length() + " bytes encoded, slots " + 32767 + ")");
        } else {
            this.writeVarInt(bytes.length);
            buf.writeBytes(bytes);
        }
    }

    @Override
    public void writeInt(int i) throws IOException {
        buf.writeInt(i);
    }

    @Override
    public void writeDouble(double d) throws IOException {
        buf.writeDouble(d);
    }

    @Override
    public void writeShort(short s) throws IOException {
        buf.writeShort(s);
    }

    @Override
    public void writeLong(long l) throws IOException {
        buf.writeLong(l);
    }

    @Override
    public void writeFloat(float f) throws IOException {
        buf.writeFloat(f);
    }

    @Override
    public void writeByte(byte b) throws IOException {
        buf.writeByte(b);
    }

    @Override
    public void writeBytes(byte[] bs) throws IOException {
        buf.writeBytes(bs);
    }

    @Override
    public void writeBoolean(boolean b) throws IOException {
        buf.writeBoolean(b);
    }
}
