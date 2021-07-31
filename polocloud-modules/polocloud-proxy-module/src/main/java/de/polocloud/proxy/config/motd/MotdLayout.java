package de.polocloud.proxy.config.motd;

public class MotdLayout {

    private String info;
    private String firstLine;
    private String secondLine;

    public MotdLayout(String firstLine, String secondLine, String info) {
        this.firstLine = firstLine;
        this.secondLine = secondLine;
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public String getFirstLine() {
        return firstLine;
    }

    public void setFirstLine(String firstLine) {
        this.firstLine = firstLine;
    }

    public String getSecondLine() {
        return secondLine;
    }

    public void setSecondLine(String secondLine) {
        this.secondLine = secondLine;
    }
}
