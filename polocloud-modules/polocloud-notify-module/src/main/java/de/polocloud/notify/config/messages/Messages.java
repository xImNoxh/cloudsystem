package de.polocloud.notify.config.messages;

public class Messages {

    private String startingMessage = "The service %service% is now starting...";
    private String runningMessage = "The service %service% is now started.";
    private String stoppedMessage = "The service %service% is now started.";


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
