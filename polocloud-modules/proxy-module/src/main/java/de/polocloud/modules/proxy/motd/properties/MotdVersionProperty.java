package de.polocloud.modules.proxy.motd.properties;

public class MotdVersionProperty {

    private final String versionInfo;
    private final String maintenanceVersionInfo;
    private final String[] playerInfo;
    private final String[] maintenancePlayerInfo;

    public MotdVersionProperty(String versionInfo, String maintenanceVersionInfo, String[] playerInfo, String[] maintenancePlayerInfo) {
        this.versionInfo = versionInfo;
        this.maintenanceVersionInfo = maintenanceVersionInfo;
        this.playerInfo = playerInfo;
        this.maintenancePlayerInfo = maintenancePlayerInfo;
    }

    public String[] getMaintenancePlayerInfo() {
        return maintenancePlayerInfo;
    }

    public String[] getPlayerInfo() {
        return playerInfo;
    }

    public String getVersionInfo() {
        return versionInfo;
    }

    public String getMaintenanceVersionInfo() {
        return maintenanceVersionInfo;
    }
}
