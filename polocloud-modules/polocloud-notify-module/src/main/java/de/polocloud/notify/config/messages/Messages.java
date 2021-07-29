package de.polocloud.notify.config.messages;

public class Messages {

    private String startingMessage = "§7The service §e§l%service% §7is now §6starting§8...";
    private String runningMessage = "§7The service §e§l%service% §7is now §astarted§8.";
    private String stoppedMessage = "§7The service §e§l%service% §7is now §cstopped§8.";


    public String getStartingMessage() {
        return startingMessage;
    }

    public String getRunningMessage() {
        return runningMessage;
    }

    public String getStoppedMessage() {
        return stoppedMessage;
    }
}
