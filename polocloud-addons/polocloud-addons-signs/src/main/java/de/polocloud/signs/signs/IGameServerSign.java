package de.polocloud.signs.signs;

import de.polocloud.api.gameserver.IGameServer;

public class IGameServerSign {

    private IGameServer gameServer;
    private String template;
    private ConfigSignLocation configSignLocation;

    public IGameServerSign(ConfigSignLocation configSignLocation) {
        this.configSignLocation = configSignLocation;
    }

    public ConfigSignLocation getConfigSignLocation() {
        return configSignLocation;
    }

    public IGameServer getGameServer() {
        return gameServer;
    }

    public String getTemplate() {
        return template;
    }
}
