package de.polocloud.api.network.protocol.buffer;

import de.polocloud.api.fallback.base.IFallback;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.wrapper.base.IWrapper;
import io.netty.buffer.ByteBuf;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public interface IPacketBuffer {

    /**
     * The base {@link ByteBuf} instance of this buffer
     */
    ByteBuf getBase();

    /**
     * Reads a {@link String} from this buffer
     */
    String readString() throws IOException;

    /**
     * Reads a var {@link Integer} from this buffer
     */
    int readVarInt() throws IOException;

    /**
     * Reads an {@link Integer} from this buffer
     */
    int readInt() throws IOException;

    /**
     * Reads an {@link Boolean} from this buffer
     */
    boolean readBoolean() throws IOException;

    /**
     * Reads a {@link Double} from this buffer
     */
    double readDouble() throws IOException;

    /**
     * Reads a {@link Float} from this buffer
     */
    float readFloat() throws IOException;

    /**
     * Reads a {@link Long} from this buffer
     */
    long readLong() throws IOException;

    /**
     * Reads a {@link Byte} from this buffer
     */
    byte readByte() throws IOException;

    /**
     * Reads a {@link Packet} from this buffer
     */
    <T extends Packet> T readPacket() throws IOException;

    /**
     * Reads an {@link ICloudPlayer} from this buffer
     */
    ICloudPlayer readCloudPlayer() throws IOException;

    /**
     * Reads an {@link IWrapper} from this buffer
     */
    IWrapper readWrapper() throws IOException;

    /**
     * Reads an {@link IGameServer} from this buffer
     */
    IGameServer readGameServer() throws IOException;

    /**
     * Reads an {@link ITemplate} from this buffer
     */
    ITemplate readTemplate() throws IOException;

    /**
     * Writes an {@link IFallback} to this buffer
     *
     * @param fallback the fallback
     * @throws IOException if something goes wrong while writing
     */
    void writeFallback(IFallback fallback) throws IOException;

    /**
     * Reads an {@link IFallback} from this buffer
     */
    IFallback readFallback() throws IOException;

    /**
     * Reads a{@link String[]} from this buffer
     */
    String[] readStrings() throws IOException;

    /**
     * Reads a custom object from this buffer
     *
     * @param <T> the generic
     */
    <T> T readCustom(Class<T> wrapperClass) throws IOException;

    /**
     * Reads a {@link UUID} from this buffer
     */
    UUID readUUID() throws IOException;

    /**
     * Reads a {@link File} from this buffer
     */
    File readFile(File dest) throws IOException;

    /**
     * Reads a {@link int[]} array from this buffer
     */
    int[] readInts() throws IOException;

    /**
     * Reads a {@link Enum} from this buffer
     */
    <T extends Enum<T>> T readEnum() throws IOException;

    /**
     * Writes a custom object to this buffer
     *
     * @param custom the object
     * @throws IOException if something goes wrong
     */
    void writeCustom(Object custom) throws IOException;

    /**
     * Writes a custom packet to this buffer
     *
     * @param packet the packet
     * @throws IOException if something goes wrong
     */
    void writePacket(Packet packet) throws IOException;


    /**
     * Writes a {@link UUID} object to this buffer
     *
     * @param uuid the uuid
     * @throws IOException if something goes wrong
     */
    void writeUUID(UUID uuid) throws IOException;

    /**
     * Writes an {@link Enum} object to this buffer
     *
     * @param e the enum
     * @throws IOException if something goes wrong
     */
    void writeEnum(Enum<?> e) throws IOException;

    /**
     * Writes a varInt to this buffer
     *
     * @param varInt the varInt
     * @throws IOException if something goes wrong
     */
    void writeVarInt(int varInt) throws IOException;

    /**
     * Writes a {@link String} object to this buffer
     *
     * @param s the string
     * @throws IOException if something goes wrong
     */
    void writeString(String s) throws IOException;

    /**
     * Writes an {@link Integer} object to this buffer
     *
     * @param i the integer
     * @throws IOException if something goes wrong
     */
    void writeInt(int i) throws IOException;

    /**
     * Writes a {@link Double} object to this buffer
     *
     * @param d the double
     * @throws IOException if something goes wrong
     */
    void writeDouble(double d) throws IOException;

    /**
     * Writes a {@link Short} object to this buffer
     *
     * @param s the short
     * @throws IOException if something goes wrong
     */
    void writeShort(short s) throws IOException;

    /**
     * Writes a {@link Long} object to this buffer
     *
     * @param l the long
     * @throws IOException if something goes wrong
     */
    void writeLong(long l) throws IOException;

    /**
     * Writes a {@link Float} object to this buffer
     *
     * @param f the float
     * @throws IOException if something goes wrong
     */
    void writeFloat(float f) throws IOException;

    /**
     * Writes a {@link Byte} object to this buffer
     *
     * @param b the byte
     * @throws IOException if something goes wrong
     */
    void writeByte(byte b) throws IOException;

    /**
     * Writes {@link Byte[]} to this buffer
     *
     * @param bs the bytes
     * @throws IOException if something goes wrong
     */
    void writeBytes(byte[] bs) throws IOException;

    /**
     * Writes a {@link Boolean} object to this buffer
     *
     * @param b the boolean
     * @throws IOException if something goes wrong
     */
    void writeBoolean(boolean b) throws IOException;

    /**
     * Writes an {@link IGameServer} object to this buffer
     *
     * @param gameServer the gameServer
     * @throws IOException if something goes wrong
     */
    void writeGameServer(IGameServer gameServer) throws IOException;

    /**
     * Writes an {@link ICloudPlayer} object to this buffer
     *
     * @param cloudPlayer the cloudPlayer
     * @throws IOException if something goes wrong
     */
    void writeCloudPlayer(ICloudPlayer cloudPlayer) throws IOException;

    /**
     * Writes an {@link IWrapper} object to this buffer
     *
     * @param wrapper the wrapper
     * @throws IOException if something goes wrong
     */
    void writeWrapper(IWrapper wrapper) throws IOException;

    /**
     * Writes an {@link ITemplate} object to this buffer
     *
     * @param template the template
     * @throws IOException if something goes wrong
     */
    void writeTemplate(ITemplate template) throws IOException;

    /**
     * Writes a {@link String[]} object to this buffer
     *
     * @param array the array
     * @throws IOException if something goes wrong
     */
    void writeStrings(String[] array) throws IOException;

    /**
     * Writes a {@link int[]} object to this buffer
     *
     * @param ints the array
     * @throws IOException if something goes wrong
     */
    void writeInts(int[] ints) throws IOException;

    /**
     * Writes a {@link File} object to this buffer
     *
     * @param f to write
     * @throws IOException if something goes wrong
     */
    void writeFile(File f) throws IOException;

}
