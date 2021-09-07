package de.polocloud.modules.proxy.motd.properties;

public class MotdVersionProperty {

    private String versionInfo;
    private String maintenanceVersionInfo;

    public MotdVersionProperty(String versionInfo, String maintenanceVersionInfo) {
        this.versionInfo = versionInfo;
        this.maintenanceVersionInfo = maintenanceVersionInfo;
    }

    public String getVersionInfo() {
        return versionInfo;
    }

    public String getMaintenanceVersionInfo() {
        return maintenanceVersionInfo;
    }
}
