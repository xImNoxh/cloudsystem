package de.polocloud.api.event.impl.player;

import de.polocloud.api.event.base.EventData;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.base.SimpleGameServer;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.player.ICloudPlayer;

import java.io.IOException;

@EventData(nettyFire = true)
public class CloudPlayerSwitchServerEvent extends CloudPlayerEvent {

    private SimpleGameServer to;

    public CloudPlayerSwitchServerEvent(ICloudPlayer player, IGameServer to) {
        super(player);
        this.to = (SimpleGameServer) to;
    }

    public IGameServer getTo() {
        return to;
    }

    public void setTo(IGameServer to) {
        this.to = (SimpleGameServer) to;
    }
}
