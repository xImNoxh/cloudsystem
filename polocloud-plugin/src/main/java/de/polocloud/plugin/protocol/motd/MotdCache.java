package de.polocloud.plugin.protocol.motd;

public class MotdCache {

    public String[] input;

    public MotdCache(String[] input) {
        this.input = input;
    }

    public String[] getInput() {
        return input;
    }

    public void setInput(String[] input) {
        this.input = input;
    }
}
