package de.polocloud.modules.proxy.api.motd;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class ProxyMotd {

    private final boolean enabled;
    private final String versionTag;
    private final String firstLine;
    private final String secondLine;
    private final String[] playerInfo;


    /**
     * Formats the two lines into one line
     * separated by a new line
     */
    public String getDescription() {
        return firstLine + "\n" + secondLine;
    }
}
