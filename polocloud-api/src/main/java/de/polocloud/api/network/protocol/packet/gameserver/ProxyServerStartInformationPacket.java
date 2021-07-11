package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.packet.IPacket;

public class ProxyServerStartInformationPacket implements IPacket {

    private String motd;

    private int maxPlayers;

    private boolean maintenance;
    private String maintenanceMessage;

    public ProxyServerStartInformationPacket(){

    }

    public ProxyServerStartInformationPacket(String motd, int maxPlayers, boolean maintenance, String maintenanceMessage) {
        this.motd = motd;
        this.maxPlayers = maxPlayers;
        this.maintenance = maintenance;
        this.maintenanceMessage = maintenanceMessage;
    }

    public String getMaintenanceMessage() {
        return maintenanceMessage;
    }

    public boolean isMaintenance() {
        return maintenance;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getMotd() {
        return motd;
    }

}


