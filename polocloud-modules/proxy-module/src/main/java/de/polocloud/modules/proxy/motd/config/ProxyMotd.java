package de.polocloud.modules.proxy.motd.config;

public class ProxyMotd {

    private final String versionTag;
    private final String firstLine;
    private final String secondLine;
    private final String[] playerInfo;

    public ProxyMotd(String firstLine, String secondLine, String versionTag, String[] playerInfo) {
        this.firstLine = firstLine;
        this.secondLine = secondLine;
        this.versionTag = versionTag;
        this.playerInfo = playerInfo;
    }

    public String[] getPlayerInfo() {
        return playerInfo;
    }

    public String getVersionTag() {
        return versionTag;
    }

    public String getFirstLine() {
        return firstLine;
    }

    public String getSecondLine() {
        return secondLine;
    }
}
