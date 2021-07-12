package de.polocloud.plugin.protocol.maintenance;

public class MaintenanceState {

    private boolean maintenance;
    private String kickMessage;

    public MaintenanceState(boolean maintenance, String kickMessage) {
        this.maintenance = maintenance;
        this.kickMessage = kickMessage;
    }

    public String getKickMessage() {
        return kickMessage;
    }

    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
    }

    public boolean isMaintenance() {
        return maintenance;
    }

    public void setKickMessage(String kickMessage) {
        this.kickMessage = kickMessage;
    }
}
