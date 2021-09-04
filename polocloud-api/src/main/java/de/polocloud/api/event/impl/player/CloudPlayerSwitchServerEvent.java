package de.polocloud.api.event.impl.player;

import de.polocloud.api.event.base.EventData;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.player.ICloudPlayer;

import java.io.IOException;

@EventData(nettyFire = true)
public class CloudPlayerSwitchServerEvent extends CloudPlayerEvent {

    private IGameServer to;
    private IGameServer from;

    public CloudPlayerSwitchServerEvent(ICloudPlayer player, IGameServer to, IGameServer from) {
        super(player);
        this.to = to;
        this.from = from;
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        super.read(buf);
        to = buf.readGameServer();
        from = buf.readGameServer();
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        super.write(buf);
        buf.writeGameServer(to);
        buf.writeGameServer(from);
    }

    public IGameServer getTo() {
        return to;
    }

    public void setTo(IGameServer to) {
        this.to = to;
    }

    public IGameServer getFrom() {
        return from;
    }

    public void setFrom(IGameServer from) {
        this.from = from;
    }
}
