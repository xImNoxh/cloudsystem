package de.polocloud.addons.signs;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.template.ITemplate;
import org.bukkit.Location;

public class Sign {

    private ITemplate template;

    private IGameServer gameServer;
    private Location location;

    public Sign(ITemplate template, Location location) {
        this.template = template;
        this.location = location;
    }

    public boolean hasServer(){
        return gameServer != null;
    }

    public ITemplate getTemplate() {
        return template;
    }

    public IGameServer getGameServer() {
        return gameServer;
    }

    public Location getLocation() {
        return location;
    }

    public void setGameServer(IGameServer gameServer) {
        this.gameServer = gameServer;
    }
}
