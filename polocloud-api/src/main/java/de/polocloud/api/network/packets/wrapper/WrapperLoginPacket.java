package de.polocloud.api.network.packets.wrapper;

import de.polocloud.api.common.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.wrapper.base.IWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;

@AutoRegistry @AllArgsConstructor @NoArgsConstructor @Getter
public class WrapperLoginPacket extends Packet {

    private IWrapper wrapper;
    private String key;

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeWrapper(wrapper);
        buf.writeString(key);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        wrapper = buf.readWrapper();
        key = buf.readString();
    }

}
