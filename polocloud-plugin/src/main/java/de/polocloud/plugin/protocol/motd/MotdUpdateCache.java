package de.polocloud.plugin.protocol.motd;

public class MotdUpdateCache {

    private String motd;

    public MotdUpdateCache() {
        motd = "default";
    }

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }
}

