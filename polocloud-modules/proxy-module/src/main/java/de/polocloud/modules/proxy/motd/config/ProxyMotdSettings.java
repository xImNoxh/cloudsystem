package de.polocloud.modules.proxy.motd.config;

public class ProxyMotdSettings {

    private boolean use = true;

    private ProxyMotd maintenanceMotd;
    private ProxyMotd onlineMotd;

    public ProxyMotdSettings(ProxyMotd maintenance, ProxyMotd onlineMotd) {
        this.maintenanceMotd = maintenance;
        this.onlineMotd = onlineMotd;
    }

    public ProxyMotd getMaintenanceMotd() {
        return maintenanceMotd;
    }

    public ProxyMotd getOnlineMotd() {
        return onlineMotd;
    }


}
