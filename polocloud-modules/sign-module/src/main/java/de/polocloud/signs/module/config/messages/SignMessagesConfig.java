package de.polocloud.signs.module.config.messages;

public class SignMessagesConfig {

    private final String alreadySameService = "§7You are already connected with this service§8.";
    private final String connectedIfServiceIsFull = "§cThis service is already full!";
    private final String maintenanceConnected = "§cThis Template is in maintenance§8...";
    private final String noSignDetected = "§7This block is not a instance of a Sign§8.";
    private final String alreadySignDetected = "§7This Sign is already in use§8.";
    private final String setSign = "§7You adding the sign for template §b%template%§8.";
    private final String removeSign = "§7You remove the sign§8.";
    private final String noSignInput = "§7This Sign is not a Game Sign§8.";
    private final String commandHelp = "§7Use following command: §bcloudsigns add (template) §7or§8: §bcloudsigns remove";
    private final String invalidTemplate = "§7The template §b%template% §7does not exists!";

    public String getAlreadySameService() {
        return alreadySameService;
    }

    public String getConnectedIfServiceIsFull() {
        return connectedIfServiceIsFull;
    }

    public String getMaintenanceConnected() {
        return maintenanceConnected;
    }

    public String getNoSignDetected() {
        return noSignDetected;
    }

    public String getAlreadySignDetected() {
        return alreadySignDetected;
    }

    public String getSetSign() {
        return setSign;
    }

    public String getRemoveSign() {
        return removeSign;
    }

    public String getNoSignInput() {
        return noSignInput;
    }

    public String getCommandHelp() {
        return commandHelp;
    }

    public String getInvalidTemplate() {
        return invalidTemplate;
    }
}
