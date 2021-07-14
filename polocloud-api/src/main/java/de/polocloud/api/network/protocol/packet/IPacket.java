package de.polocloud.api.network.protocol.packet;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public abstract class IPacket {

    public abstract void write(ByteBuf byteBuf) throws IOException;

    public abstract void read(ByteBuf byteBuf) throws IOException;

    public void writeString(ByteBuf byteBuf, String s) {
        byte[] bArr = s.getBytes();
        byteBuf.writeInt(bArr.length);
        byteBuf.writeBytes(bArr);
    }

    public String readString(ByteBuf byteBuf) {
        byte[] bArr = new byte[byteBuf.readInt()];
        byteBuf.readBytes(bArr);
        return new String(bArr);
    }


}
