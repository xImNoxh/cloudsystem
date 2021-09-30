package de.polocloud.api.network.protocol.packet.base.response.def;

import de.polocloud.api.config.JsonData;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.common.AutoRegistry;

import java.io.IOException;

@AutoRegistry
public class Request extends Packet {

    private String key;
    private JsonData data;

    public Request(String key, JsonData data) {
        this.key = key;
        this.data = data;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(key);
        buf.writeString(data.toString());
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        key = buf.readString();
        data = new JsonData(buf.readString());
    }

    public String getKey() {
        return key;
    }

    public JsonData getData() {
        return data;
    }
}
