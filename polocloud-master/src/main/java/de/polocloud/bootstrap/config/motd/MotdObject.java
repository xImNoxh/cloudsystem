package de.polocloud.bootstrap.config.motd;

public class MotdObject {

    private String key = "default";
    private String firstLine = "§7A default §b§lPoloCloud §7service§8.";
    private String secondLine = "§7§oDon't touch a running system§8...";

    public String getKey() {
        return key;
    }

    public String getFirstLine() {
        return firstLine;
    }

    public String getSecondLine() {
        return secondLine;
    }
}
