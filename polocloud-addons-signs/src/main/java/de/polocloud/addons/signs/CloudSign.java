package de.polocloud.addons.signs;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.template.ITemplate;
import org.bukkit.Location;
import org.bukkit.block.Sign;

public class CloudSign {

    private ITemplate template;

    private IGameServer gameServer;
    private Location location;

    private Sign sign;

    public CloudSign(ITemplate template, Location location) {
        this.template = template;
        this.location = location;

        this.sign = (Sign) location.getBlock().getState();
    }

    public Sign getSign() {
        return sign;
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
