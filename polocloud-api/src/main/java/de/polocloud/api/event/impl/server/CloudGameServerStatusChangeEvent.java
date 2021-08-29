package de.polocloud.api.event.impl.server;

import de.polocloud.api.event.base.EventData;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;

import java.io.IOException;

@EventData(nettyFire = true)
public class CloudGameServerStatusChangeEvent extends GameServerEvent {

    private GameServerStatus status;

    public CloudGameServerStatusChangeEvent(IGameServer gameServer, GameServerStatus status) {
        super(gameServer);
        this.status = status;
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        super.read(buf);

        status = buf.readEnum();
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        super.write(buf);

        buf.writeEnum(status);
    }

    public GameServerStatus getStatus() {
        return status;
    }

}
