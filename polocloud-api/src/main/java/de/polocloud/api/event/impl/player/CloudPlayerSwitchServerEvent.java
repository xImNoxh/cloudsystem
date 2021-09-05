package de.polocloud.api.event.impl.player;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.base.EventData;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.base.SimpleGameServer;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.player.ICloudPlayer;

import java.io.IOException;

@EventData(nettyFire = true)
public class CloudPlayerSwitchServerEvent extends CloudPlayerEvent {

    private String to;
    private String from;

    public CloudPlayerSwitchServerEvent(ICloudPlayer player, IGameServer to, IGameServer from) {
        super(player);
        this.to = to == null ? "Null" : to.getName();
        this.from = from == null ? "Null" : from.getName();
    }

    public IGameServer getTo() {
        return PoloCloudAPI.getInstance().getGameServerManager().getCached(to);
    }

    public void setTo(IGameServer to) {
        this.to = to.getName();
    }

    public IGameServer getFrom() {
        return PoloCloudAPI.getInstance().getGameServerManager().getCached(from);
    }

    public void setFrom(IGameServer from) {
        this.from = from.getName();
    }
}
