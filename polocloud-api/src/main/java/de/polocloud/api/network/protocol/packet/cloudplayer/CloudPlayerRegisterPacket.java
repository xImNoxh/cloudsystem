package de.polocloud.api.network.protocol.packet.cloudplayer;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.player.ICloudPlayer;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class CloudPlayerRegisterPacket extends Packet {

    private ICloudPlayer cloudPlayer;

    public CloudPlayerRegisterPacket() {
    }

    public CloudPlayerRegisterPacket(ICloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeCloudPlayer(cloudPlayer);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        cloudPlayer = buf.readCloudPlayer();
    }

    public ICloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }

}
