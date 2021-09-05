package de.polocloud.modules.proxy.config.notify;

public class NotifyConfig {

    private boolean use = true;
    private String permission;
    private String startingMessage, startedMessage, stoppedMessage;

    public NotifyConfig(boolean use, String permission, String startingMessage, String startedMessage, String stoppedMessage) {
        this.use = use;
        this.permission = permission;
        this.startingMessage = startingMessage;
        this.startedMessage = startedMessage;
        this.stoppedMessage = stoppedMessage;
    }

    public String getPermission() {
        return permission;
    }

    public boolean isUse() {
        return use;
    }

    public String getStartingMessage() {
        return startingMessage;
    }

    public String getStartedMessage() {
        return startedMessage;
    }

    public String getStoppedMessage() {
        return stoppedMessage;
    }
}
