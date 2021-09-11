package de.polocloud.api.event.impl.other;

import de.polocloud.api.event.base.CloudEvent;
import de.polocloud.api.event.base.EventData;

@EventData(nettyFire = false)
public class ProxyServerRequestPingEvent extends CloudEvent {

    private int onlinePlayers;
    private int maxPlayers;
    private String motd;
    private String versionString;
    private String[] playerInfo;

    public ProxyServerRequestPingEvent() {
        this.onlinePlayers = 0;
        this.maxPlayers = 20;
        this.motd = null;
        this.versionString = null;
        this.playerInfo = new String[0];
    }

    public void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public void setVersionString(String versionString) {
        this.versionString = versionString;
    }

    public void setPlayerInfo(String[] playerInfo) {
        this.playerInfo = playerInfo;
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getMotd() {
        return motd;
    }

    public String getVersionString() {
        return versionString;
    }

    public String[] getPlayerInfo() {
        return playerInfo;
    }
}
