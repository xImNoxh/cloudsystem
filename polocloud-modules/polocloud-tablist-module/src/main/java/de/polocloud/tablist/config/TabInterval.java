package de.polocloud.tablist.config;

public class TabInterval {

    private boolean use = true;
    private int seconds = 3;

    public int getTicks() {
        return seconds;
    }

    public boolean isUse() {
        return use;
    }
}
