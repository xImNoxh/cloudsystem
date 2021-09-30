package de.polocloud.api.network.packets.cloudplayer;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.common.AutoRegistry;

import java.io.IOException;

@AutoRegistry
public class CloudPlayerUpdatePacket extends Packet {

    private ICloudPlayer cloudPlayer;

    public CloudPlayerUpdatePacket() {
    }

    public CloudPlayerUpdatePacket(ICloudPlayer cloudPlayer) {
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
