package de.polocloud.api.network.packets.wrapper;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.common.AutoRegistry;
import de.polocloud.api.wrapper.base.IWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;

@AutoRegistry @NoArgsConstructor @AllArgsConstructor @Getter
public class WrapperUpdatePacket extends Packet {

    private IWrapper wrapper;

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeWrapper(wrapper);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        wrapper = buf.readWrapper();
    }
}
