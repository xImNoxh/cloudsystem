package de.polocloud.signs.config.messages;

public class SignMessages {

    private String alreadySameService = "§7Du bist mit dem gleichen Service schon verbunden§8.";
    private String connectedIfServiceIsFull = "§cThis service is already full!";
    private String maintenanceConnected = "§cThis Template is in maintenance§8...";
    private String noSignDetected = "§7This block is not a instance of a Sign§8.";
    private String alreadySignDetected = "§7This Sign is already in use§8.";
    private String setSign = "§7You adding the sign for template §b%template%§8.";
    private String removeSign = "§7You remove the sign§8.";
    private String noSignInput = "§7This Sign is not a Game Sign§8.";
    private String commandHelp = "§7Use following command: §bcloudsigns add (template) §7or§8: §bcloudsigns remove";

    public String getNoSignDetected() {
        return noSignDetected;
    }

    public String getCommandHelp() {
        return commandHelp;
    }

    public String getNoSignInput() {
        return noSignInput;
    }

    public String getSetSign() {
        return setSign;
    }

    public String getAlreadySignDetected() {
        return alreadySignDetected;
    }

    public String getAlreadySameService() {
        return alreadySameService;
    }

    public String getConnectedIfServiceIsFull() {
        return connectedIfServiceIsFull;
    }

    public String getMaintenanceConnected() {
        return maintenanceConnected;
    }

    public String getRemoveSign() {
        return removeSign;
    }
}
