package de.polocloud.api.network.protocol.packet.wrapper;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.wrapper.IWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WrapperCachePacket extends Packet {

    private Map<String, Long> wrappers;

    public WrapperCachePacket() {
        this.wrappers = new HashMap<>();
        for (IWrapper wrapper : PoloCloudAPI.getInstance().getWrapperManager().getWrappers()) {
            this.wrappers.put(wrapper.getName(), wrapper.getSnowflake());
        }
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeInt(wrappers.size());
        for (String s : wrappers.keySet()) {
            long l = wrappers.get(s);
            buf.writeString(s);
            buf.writeLong(l);
        }
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        int size = buf.readInt();
        this.wrappers = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            String name = buf.readString();
            long snowflake = buf.readLong();
            this.wrappers.put(name, snowflake);
        }
    }

    public Map<String, Long> getWrappers() {
        return wrappers;
    }
}
