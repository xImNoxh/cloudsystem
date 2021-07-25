package de.polocloud.api.event;

public abstract class Cancelable {

    private boolean canceled;
    private String cancelMessage = "";

    public String getCancelMessage() {
        return cancelMessage;
    }

    public void setCancelMessage(String cancelMessage) {
        this.cancelMessage = cancelMessage;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public boolean isCanceled() {
        return canceled;
    }
}
