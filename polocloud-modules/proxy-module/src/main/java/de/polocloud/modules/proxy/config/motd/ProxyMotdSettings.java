package de.polocloud.modules.proxy.config.motd;

public class ProxyMotdSettings {

    private boolean use = true;

    private ProxyMotd maintenanceMotd;
    private ProxyMotd onlineMotd;

    public ProxyMotdSettings() {
        this.maintenanceMotd = new ProxyMotd("maintence","polocloud");
        this.onlineMotd = new ProxyMotd("online","polocloud");
    }

    public ProxyMotd getMaintenanceMotd() {
        return maintenanceMotd;
    }

    public ProxyMotd getOnlineMotd() {
        return onlineMotd;
    }


}
