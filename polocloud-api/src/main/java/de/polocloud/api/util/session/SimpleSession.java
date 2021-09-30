package de.polocloud.api.util.session;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

@Getter @AllArgsConstructor @NoArgsConstructor
public class SimpleSession implements ISession {

    private byte[] bytes;
    private long snowflake;
    private UUID uuid;
    private String identification;

    @Override
    public UUID sessionUUID() {
        return uuid;
    }

    @Override
    public void randomize() {

        this.uuid = UUID.randomUUID();

        long lsb = uuid.getLeastSignificantBits();
        long msb = uuid.getMostSignificantBits();

        this.bytes = ByteBuffer.allocate(16).putLong(msb).putLong(lsb).array();
        this.identification = Base64.encode(this.bytes);
        this.snowflake = lsb + msb;
    }

    @Override
    public boolean equals(ISession other) {
        if (other.getBytes().length != getBytes().length) {
            return false;
        } else if (!other.getIdentification().equalsIgnoreCase(getIdentification())) {
            return false;
        } else if (!other.sessionUUID().equals(sessionUUID())) {
            return false;
        } else if (other.getSnowflake() != getSnowflake()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String toString() {
        return identification;
    }


    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeInt(bytes.length);
        for (byte aByte : bytes) {
            buf.writeByte(aByte);
        }

        buf.writeLong(snowflake);
        buf.writeUUID(uuid);
        buf.writeString(identification);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        int length = buf.readInt();
        this.bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = buf.readByte();
        }

        this.snowflake = buf.readLong();
        this.uuid = buf.readUUID();
        this.identification = buf.readString();
    }
}
