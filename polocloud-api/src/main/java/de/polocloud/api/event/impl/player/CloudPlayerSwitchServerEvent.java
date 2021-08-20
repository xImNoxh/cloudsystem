package de.polocloud.api.event.impl.player;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.player.ICloudPlayer;

import java.io.IOException;

public class CloudPlayerSwitchServerEvent extends CloudPlayerEvent {

    private IGameServer to;

    public CloudPlayerSwitchServerEvent(ICloudPlayer player, IGameServer to) {
        super(player);
        this.to = to;
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        super.read(buf);
        to = buf.readGameServer();
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        super.write(buf);
        buf.writeGameServer(to);
    }

    public IGameServer getTo() {
        return to;
    }

    public void setTo(IGameServer to) {
        this.to = to;
    }
}
