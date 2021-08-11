package de.polocloud.api.network.protocol.packet.cloudplayer;

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
    public void write(ByteBuf byteBuf) throws IOException {
        writeCloudPlayer(byteBuf, cloudPlayer);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        cloudPlayer = readCloudPlayer(byteBuf);
    }

    public ICloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }

}
