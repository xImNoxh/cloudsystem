package de.polocloud.proxy.info;

import de.polocloud.proxy.ProxyModule;
import de.polocloud.proxy.config.motd.MotdLayout;

public class MotdInfoService {

    private String currentMotd;
    private String maintenanceMotd;

    public MotdInfoService() {
        this.currentMotd = buildMotd(ProxyModule.getInstance().getProxyConfig().getMotds().getDefaultLayouts()[0]);
        this.maintenanceMotd = buildMotd(ProxyModule.getInstance().getProxyConfig().getMotds().getMaintenanceLayouts()[0]);
    }

    public String buildMotd(MotdLayout motdLayout) {
        return motdLayout.getFirstLine() + "\n" + motdLayout.getSecondLine();
    }

    public String getCurrentMotd() {
        return currentMotd;
    }

    public String getMaintenanceMotd() {
        return maintenanceMotd;
    }
}
