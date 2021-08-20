package de.polocloud.api.event.impl.server;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;

import java.io.IOException;

public class CloudGameServerStatusChangeEvent extends GameServerEvent {

    private Status status;

    public CloudGameServerStatusChangeEvent(IGameServer gameServer, Status status) {
        super(gameServer);
        this.status = status;
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        super.read(buf);

        status = Status.valueOf(buf.readString());
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        super.write(buf);

        buf.writeString(status.name());
    }

    public Status getStatus() {
        return status;
    }

    public enum Status {
        STARTING,
        RUNNING,
        STOPPING,
    }

}
