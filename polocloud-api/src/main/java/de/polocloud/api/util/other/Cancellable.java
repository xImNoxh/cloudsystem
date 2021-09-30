package de.polocloud.api.util.other;

public class Cancellable {

    /**
     * If this instance (has been) cancelled
     */
    private boolean cancelled;

    /**
     * Constructs this object
     * and sets the default value to false
     */
    public Cancellable() {
        this.cancelled = false;
    }

    /**
     * Sets the state of this {@link Cancellable}
     *
     * @param cancelled the state
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Checks if this {@link Cancellable} has been cancelled
     */
    public boolean isCancelled() {
        return cancelled;
    }
}
