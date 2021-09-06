package de.polocloud.modules.proxy.motd.config;

public class ProxyMotd {

    private String versionTag;
    private String firstLine;
    private String secondLine;

    public ProxyMotd(String firstLine, String secondLine, String versionTag) {
        this.firstLine = firstLine;
        this.secondLine = secondLine;
        this.versionTag = versionTag;
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
