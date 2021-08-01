package de.polocloud.api.event.player;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.player.ICloudPlayer;

public class CloudPlayerSwitchServerEvent extends CloudPlayerEvent {

    private IGameServer to;

    public CloudPlayerSwitchServerEvent(ICloudPlayer player, IGameServer to) {
        super(player);
        this.to = to;
    }

    public IGameServer getTo() {
        return to;
    }

    public void setTo(IGameServer to) {
        this.to = to;
    }
}
