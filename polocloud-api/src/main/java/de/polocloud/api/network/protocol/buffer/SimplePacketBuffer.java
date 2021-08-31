package de.polocloud.api.network.protocol.buffer;

import com.google.common.base.Charsets;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.fallback.base.IFallback;
import de.polocloud.api.fallback.base.SimpleFallback;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.base.SimpleGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.network.protocol.packet.PacketFactory;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.SimpleCloudPlayer;
import de.polocloud.api.template.SimpleTemplate;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.helper.GameServerVersion;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.api.wrapper.base.IWrapper;
import de.polocloud.api.wrapper.base.SimpleWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

import java.io.IOException;
import java.util.UUID;

public class SimplePacketBuffer implements IPacketBuffer {

    private final ByteBuf buf;

    public SimplePacketBuffer(ByteBuf byteBuf) {
        this.buf = byteBuf;
    }

    @Override
    public IPacketBuffer avoidNulls() {
        //TODO
        return this;
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
            part.release();

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
    public void writePacket(Packet packet) throws IOException {
        this.writeInt(PacketFactory.getPacketId(packet.getClass()));
        packet.write(this);
    }

    @Override
    public <T extends Packet> T readPacket() throws IOException {
        int id = this.readInt();
        return (T) PacketFactory.createPacket(id);
    }

    @Override
    public void writeStrings(String[] arr) throws IOException {
        this.writeInt(arr.length);
        for (String s : arr) {
            writeString(s);
        }
    }

    @Override
    public String[] readStrings() throws IOException {
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
            String classString = this.readString();
            enumClass = Class.forName(classString);
            int varInt = this.readVarInt();
            return (T) enumClass.getEnumConstants()[varInt];
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


    @Override
    public IGameServer readGameServer() throws IOException {

        //Template
        boolean nulled = this.readBoolean();
        ITemplate template;
        if (nulled) {
            template = this.readTemplate();
        } else {
            template = null;
        }

        //Name and snowflake and port
        String name = this.readString();

        long snowflake = this.readLong();

        int port = this.readInt();

        //Extra values
        String motd = this.readString();

        GameServerStatus status = this.readEnum();

        //Memory and ping
        long memory = this.readLong();

        long ping = this.readLong();

        long startTime = this.readLong();

        int maxPlayers = this.readInt();

        boolean serviceVisibility = this.readBoolean();

        boolean registered = this.readBoolean();

        IGameServer gameServer = new SimpleGameServer(name, motd, serviceVisibility, status, snowflake, ping, startTime, memory, port, maxPlayers, template);
        gameServer.setRegistered(registered);
        return gameServer;
    }

    @Override
    public void writeGameServer(IGameServer gameServer) throws IOException {

        //Template
        this.writeBoolean(gameServer.getTemplate() != null);
        if (gameServer.getTemplate() != null) {
            this.writeTemplate(gameServer.getTemplate());
        }

        //Name and snowflake and port
        this.writeString(gameServer.getName());
        this.writeLong(gameServer.getSnowflake());
        this.writeInt(gameServer.getPort());

        //Extra values
        this.writeString(gameServer.getMotd());
        this.writeEnum(gameServer.getStatus());

        //Memory and ping
        this.writeLong(gameServer.getTotalMemory());
        this.writeLong(gameServer.getPing());

        //Other values
        this.writeLong(gameServer.getStartTime());
        this.writeInt(gameServer.getMaxPlayers());
        this.writeBoolean(gameServer.getServiceVisibility());
        this.writeBoolean(gameServer.isRegistered());

    }

    @Override
    public void writeTemplate(ITemplate template) throws IOException {

        //Name of template
        this.writeString(template.getName());

        //Server counts
        this.writeInt(template.getMinServerCount());
        this.writeInt(template.getMaxServerCount());

        //Other settings
        this.writeInt(template.getMaxPlayers());
        this.writeInt(template.getMaxMemory());
        this.writeInt(template.getServerCreateThreshold());

        //Maintenance and mode (static or dynamic)
        this.writeBoolean(template.isMaintenance());
        this.writeBoolean(template.isStatic());

        //Template type and version
        this.writeEnum(template.getTemplateType());
        this.writeEnum(template.getVersion());

        //The motd and allowed wrappers
        this.writeString(template.getMotd() == null ? "" : template.getMotd());
        this.writeStrings(template.getWrapperNames() == null ? new String[0] : template.getWrapperNames());

    }

    @Override
    public ITemplate readTemplate() throws IOException {

        //Name of template
        String name = readString();

        //Server counts
        int minServers = this.readInt();
        int maxServers = this.readInt();

        //Other settings
        int maxPlayers = this.readInt();

        int maxMemory = this.readInt();

        int createThreshold = this.readInt();

        //Maintenance and mode (static or dynamic)
        boolean maintenance = this.readBoolean();

        boolean staticServer = this.readBoolean();

        //Template type and version
        TemplateType templateType = this.readEnum();

        GameServerVersion version = this.readEnum();

        //The motd and allowed wrappers
        String motd = readString();

        String[] wrapperNames = readStrings();

        return new SimpleTemplate(name, staticServer, maxServers, minServers, templateType, version, maxPlayers, maxMemory, maintenance, motd, createThreshold, wrapperNames);
    }


    @Override
    public IWrapper readWrapper() throws IOException {

        long snowflake = readLong();
        String name = readString();
        return new SimpleWrapper(name, snowflake, PoloCloudAPI.getInstance().getConnection() == null ? null : PoloCloudAPI.getInstance().getConnection().ctx());
    }

    @Override
    public void writeWrapper(IWrapper wrapper) throws IOException {
        this.writeLong(wrapper.getSnowflake());
        this.writeString(wrapper.getName());
    }


    @Override
    public IFallback readFallback() throws IOException {
        String name = this.readString();
        String perm = this.readString();

        int priority = this.readInt();
        boolean forcedJoin = this.readBoolean();

        return new SimpleFallback(name, perm, forcedJoin, priority);
    }

    @Override
    public void writeFallback(IFallback fallback) throws IOException {

        //Name and permission
        this.writeString(fallback.getTemplateName());
        this.writeString(fallback.getFallbackPermission());

        //Other settings
        this.writeInt(fallback.getPriority());
        this.writeBoolean(fallback.isForcedJoin());
    }

    @Override
    public ICloudPlayer readCloudPlayer() throws IOException {

        //Name and UUID
        String name = this.readString();
        UUID uuid = readUUID();

        //Proxy and server

        String s1 = this.readString();
        String s2 = this.readString();

        String proxy = s1.trim().isEmpty() ? null : s1;
        String minecraft = s2.trim().isEmpty() ? null : s2;

        //Creating player and setting values
        SimpleCloudPlayer cloudPlayer = new SimpleCloudPlayer(name, uuid);

        cloudPlayer.setProxyServer(proxy);
        cloudPlayer.setMinecraftServer(minecraft);

        return cloudPlayer;
    }

    @Override
    public void writeCloudPlayer(ICloudPlayer cloudPlayer) throws IOException {

        //Name and UUID
        this.writeString(cloudPlayer.getName());
        this.writeUUID(cloudPlayer.getUUID());

        //Proxy and server
        this.writeString(cloudPlayer.getProxyServer() == null ? "" : cloudPlayer.getProxyServer().getName());
        this.writeString(cloudPlayer.getMinecraftServer() == null ? "" : cloudPlayer.getMinecraftServer().getName());
    }
}
