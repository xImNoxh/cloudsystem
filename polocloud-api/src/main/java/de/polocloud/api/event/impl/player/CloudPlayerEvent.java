package de.polocloud.api.event.impl.player;

import de.polocloud.api.event.base.IEvent;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.player.ICloudPlayer;

import java.io.IOException;

public abstract class CloudPlayerEvent implements IEvent {

    private ICloudPlayer player;

    protected CloudPlayerEvent(ICloudPlayer player) {
        this.player = player;
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        player = buf.readCloudPlayer();
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeCloudPlayer(player);
    }

    public ICloudPlayer getPlayer() {
        return player;
    }
}
