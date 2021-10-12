package de.polocloud.api.network.protocol.packet.handler;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.network.protocol.packet.base.Packet;
import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class ConsumingPacketHandler<T extends Packet> implements IPacketHandler<T> {

    private final Class<T> typeClass;
    private final Consumer<T> consumer;

    @Setter @Getter
    private boolean destroyAfterHandle;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, T packet) {
        this.consumer.accept(packet);
        if (destroyAfterHandle) {
            PoloCloudAPI.getInstance().getConnection().getProtocol().unregisterPacketHandler(this);
        }
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return typeClass;
    }
}
