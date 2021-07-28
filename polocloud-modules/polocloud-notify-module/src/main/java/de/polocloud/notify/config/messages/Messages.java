package de.polocloud.notify.config.messages;

public class Messages {

    private String startingMessage = "§7The service &e%service% &7is now §6starting...";
    private String runningMessage = "§7The service &e%service% &7is now §astarted.";
    private String stoppedMessage = "§7The service &e%service% &7is now §cstopped.";


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
