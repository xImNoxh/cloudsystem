package de.polocloud.proxy.config.motd;

public class MotdLists {

    private MotdLayout[] maintenanceLayouts;
    private MotdLayout[] defaultLayouts;

    public MotdLists() {
        this.maintenanceLayouts = new MotdLayout[]{new MotdLayout("polocloud","maintenance","§cMaintenance")};
        this.defaultLayouts = new MotdLayout[]{new MotdLayout("polocloud","online",null)};
    }

    public MotdLayout[] getMaintenanceLayouts() {
        return maintenanceLayouts;
    }

    public void setMaintenanceLayouts(MotdLayout[] maintenanceLayouts) {
        this.maintenanceLayouts = maintenanceLayouts;
    }

    public MotdLayout[] getDefaultLayouts() {
        return defaultLayouts;
    }

    public void setDefaultLayouts(MotdLayout[] defaultLayouts) {
        this.defaultLayouts = defaultLayouts;
    }
}
