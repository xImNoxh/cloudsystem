package de.polocloud.api.network.protocol.buffer;

import com.google.common.base.Charsets;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.fallback.base.IFallback;
import de.polocloud.api.fallback.base.SimpleFallback;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.base.SimpleGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.network.protocol.IProtocolObject;
import de.polocloud.api.network.protocol.packet.PacketFactory;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.extras.IPlayerConnection;
import de.polocloud.api.player.def.SimpleCloudPlayer;
import de.polocloud.api.player.def.SimplePlayerConnection;
import de.polocloud.api.property.IProperty;
import de.polocloud.api.property.def.SimpleProperty;
import de.polocloud.api.template.SimpleTemplate;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.helper.GameServerVersion;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.api.util.MinecraftProtocol;
import de.polocloud.api.util.gson.PoloHelper;
import de.polocloud.api.wrapper.base.IWrapper;
import de.polocloud.api.wrapper.base.SimpleWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public File readFile(File dest) throws IOException {
        String name = readString();
        int size = readInt();
        ByteBuf part = buf.readBytes(size);
        byte[] bytes = new byte[size];
        part.readBytes(bytes);
        part.release();

        if (!dest.exists()) {
            dest.mkdirs();
        }

        return Files.write(new File(dest.getPath() + "/" + name).toPath(), bytes).toFile();
    }

    @Override
    public int[] readInts() throws IOException {
        int length = this.readInt();
        int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = readInt();
        }
        return array;
    }

    @Override
    public void writeInts(int[] ints) throws IOException {
        this.writeInt(ints.length);
        for (int i : ints) {
            writeInt(i);
        }
    }

    @Override
    public void writeFile(File f) throws IOException {
        writeString(f.getName());
        byte[] fileContent = Files.readAllBytes(f.toPath());
        writeInt(fileContent.length);
        writeBytes(fileContent);
    }

    @Override
    public void writePacket(Packet packet) throws IOException {
        this.writeInt(PacketFactory.getPacketId(packet.getClass()));
        packet.write(this);
    }

    @Override
    public <T extends Packet> T readPacket() throws IOException {
        int id = this.readInt();
        T packet = (T) PacketFactory.createPacket(id);
        packet.read(this);
        return packet;
    }

    @Override
    public void writeStrings(String[] arr) throws IOException {
        this.writeBoolean(arr == null);
        if (arr !=null) {
            this.writeInt(arr.length);
            for (String s : arr) {
                writeString(s);
            }
        }
    }

    @Override
    public String[] readStrings() throws IOException {
        boolean nulled = this.readBoolean();
        if (!nulled) {
            int length = this.readInt();
            String[] array = new String[length];
            for (int i = 0; i < length; i++) {
                array[i] = readString();
            }
            return array;
        }
        return new String[0];
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
        boolean nulled = this.readBoolean();
        if (nulled) {
            return null;
        }
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
        this.writeBoolean(val == null);
        if (val != null) {
            this.writeString(val.getDeclaringClass().getName());
            this.writeVarInt(val.ordinal());
        }
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
        String template = this.readString();

        //Name and snowflake and port
        String name = this.readString();
        long snowflake = this.readLong();
        int port = this.readInt();
        int id = this.readInt();

        //Extra values
        String motd = this.readString();
        String versionString = this.readString();
        String[] playerInfo = this.readStrings();
        GameServerStatus status = this.readEnum();

        //Memory
        long memory = this.readLong();
        long startTime = this.readLong();
        int maxPlayers = this.readInt();
        int onlinePlayers = this.readInt();
        boolean serviceVisibility = this.readBoolean();
        boolean registered = this.readBoolean();

        //Properties
        int size = this.readInt();
        List<IProperty> properties = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            properties.add(PoloHelper.GSON_INSTANCE.fromJson(this.readString(), SimpleProperty.class));
        }

        IGameServer gameServer = new SimpleGameServer(id, motd, serviceVisibility, status, snowflake, startTime, memory, port, maxPlayers, template);
        gameServer.setRegistered(registered);
        gameServer.setProperties(properties);
        gameServer.setServerPing(motd, maxPlayers, onlinePlayers, versionString, playerInfo);
        return gameServer;
    }

    @Override
    public void writeGameServer(IGameServer gameServer) throws IOException {

        //Template
        this.writeString(gameServer.getTemplate() == null ? "no_template_provided" : gameServer.getTemplate().getName());

        //Name and snowflake and port
        this.writeString(gameServer.getName());
        this.writeLong(gameServer.getSnowflake());
        this.writeInt(gameServer.getPort());
        this.writeInt(gameServer.getId());

        //Extra values
        this.writeString(gameServer.getMotd());
        this.writeString(((SimpleGameServer)gameServer).getVersionString());
        this.writeStrings(((SimpleGameServer)gameServer).getPlayerInfo());
        this.writeEnum(gameServer.getStatus());

        //Memory
        this.writeLong(gameServer.getTotalMemory());

        //Other values
        this.writeLong(gameServer.getStartTime());
        this.writeInt(gameServer.getMaxPlayers());
        this.writeInt(gameServer.getOnlinePlayers());
        this.writeBoolean(gameServer.getServiceVisibility());
        this.writeBoolean(gameServer.isRegistered());

        //Properties
        this.writeInt(gameServer.getProperties() == null ? 0 : gameServer.getProperties().size());
        if (gameServer.getProperties() != null) {
            for (IProperty property : gameServer.getProperties()) {
                this.writeString(PoloHelper.GSON_INSTANCE.toJson(property));
            }
        }
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
        boolean nulled = this.readBoolean();

        if (nulled) {
            return null;
        }

        String name = this.readString();
        String perm = this.readString();

        int priority = this.readInt();
        boolean forcedJoin = this.readBoolean();

        return new SimpleFallback(name, perm, forcedJoin, priority);
    }

    @Override
    public void writeFallback(IFallback fallback) throws IOException {

        this.writeBoolean(fallback == null);
        if (fallback == null) {
            return;
        }

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

        //Connection
        String host = this.readString();
        int port = this.readInt();
        int version = this.readInt();
        InetSocketAddress address = PoloHelper.getAddress(this.readString());
        boolean onlineMode = this.readBoolean();
        boolean legacy = this.readBoolean();

        IPlayerConnection connection = new SimplePlayerConnection(address, uuid, name, host, port, MinecraftProtocol.valueOf(version), onlineMode, legacy);

        //Creating player and setting values
        SimpleCloudPlayer cloudPlayer = new SimpleCloudPlayer(name, uuid, connection);

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

        //Connection
        this.writeString(cloudPlayer.getConnection().getHost());
        this.writeInt(cloudPlayer.getConnection().getPort());
        this.writeInt(cloudPlayer.getConnection().getVersion().getProtocolId());
        this.writeString(cloudPlayer.getConnection().getAddress().toString());
        this.writeBoolean(cloudPlayer.getConnection().isOnlineMode());
        this.writeBoolean(cloudPlayer.getConnection().isLegacy());
    }

    @Override
    public <T extends IProtocolObject> T readProtocol() throws IOException {

        String cls = this.readString();
        try {
            Class<? extends IProtocolObject> clazz = (Class<? extends IProtocolObject>) Class.forName(cls);

            IProtocolObject protocolObject;
            try {
                protocolObject = clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                protocolObject = PoloHelper.getInstance(clazz);
            }

            if (protocolObject != null) {
                protocolObject.read(this);
                return (T) protocolObject;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void writeProtocol(IProtocolObject object) throws IOException {
        this.writeString(object.getClass().getName());
        object.write(this);
    }
}
