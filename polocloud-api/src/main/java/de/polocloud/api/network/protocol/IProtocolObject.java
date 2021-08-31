package de.polocloud.api.network.protocol;

import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public interface IProtocolObject {


    /**
     * This method is called when parsing the packet to the channel-flow of netty
     * and writing all its data into a {@link ByteBuf} which is the object netty contains
     * its data in when sending it to other clients/servers
     * A {@link ByteBuf} does not store values under a key or something it just "stacks" them on
     * top of the last value.
     * So if you put a name, an Integer and a boolean for example it would look like that if you would imagine:
     *
     * -> "Hans"
     * -> 56
     * -> true
     *
     * And later on when using {@link Packet#read(IPacketBuffer)} you have to first read the name
     * then read the integer and then the boolean
     * If you first try to read the integer it will not work because the first value is a String and not an integer
     *
     * @param buf the buffer
     * @throws IOException if something while writing goes wrong
     */
    void write(IPacketBuffer buf) throws IOException;

    /**
     * This method is called when reading the packet from the channel-flow of netty
     * and re-writing all its data from a {@link ByteBuf} into this {@link Packet}
     * As already in {@link Packet#write(IPacketBuffer)} described you should pay attention
     * to the order you wrote your data in and not confuse the order and try to read a String
     * where actually an Integer is stored
     *
     * @param buf the buffer
     * @throws IOException if something while reading goes wrong
     */
    void read(IPacketBuffer buf) throws IOException;

}
