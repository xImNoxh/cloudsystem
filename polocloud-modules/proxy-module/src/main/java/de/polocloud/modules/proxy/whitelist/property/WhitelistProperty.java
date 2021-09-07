package de.polocloud.modules.proxy.whitelist.property;


import java.util.List;

public class WhitelistProperty {

    private List<String> whitelistPlayers;

    public WhitelistProperty(List<String> players) {
        this.whitelistPlayers = players;
    }

    public List<String> getWhitelistPlayers() {
        return whitelistPlayers;
    }
}
