package de.polocloud.api.util;


public class Activatable {

    private boolean enabled;

    public Activatable(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
