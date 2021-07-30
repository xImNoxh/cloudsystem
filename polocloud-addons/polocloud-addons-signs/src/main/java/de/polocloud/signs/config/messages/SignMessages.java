package de.polocloud.signs.config.messages;

public class SignMessages {

    private String alreadySameService = "§7Du bist mit dem gleichen Service schon verbunden§8.";
    private String connectedIfServiceIsFull = "§cThis service is already full!";
    private String maintenanceConnected = "§cThis Template is in maintenance§8...";

    public String getAlreadySameService() {
        return alreadySameService;
    }

    public String getConnectedIfServiceIsFull() {
        return connectedIfServiceIsFull;
    }

    public String getMaintenanceConnected() {
        return maintenanceConnected;
    }
}
