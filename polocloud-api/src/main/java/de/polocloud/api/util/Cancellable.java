package de.polocloud.api.util;

public class Cancellable {

    private boolean cancelled;

    public Cancellable() {
        this.cancelled = false;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
