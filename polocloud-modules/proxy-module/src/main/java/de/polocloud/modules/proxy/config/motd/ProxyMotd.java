package de.polocloud.modules.proxy.config.motd;

public class ProxyMotd {

    private String firstLine;
    private String secondLine;

    public ProxyMotd(String firstLine, String secondLine) {
        this.firstLine = firstLine;
        this.secondLine = secondLine;
    }

    public String getFirstLine() {
        return firstLine;
    }

    public String getSecondLine() {
        return secondLine;
    }
}
