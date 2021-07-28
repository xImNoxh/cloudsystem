package de.polocloud.signs.signs;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.template.ITemplate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;

public class IGameServerSign {

    private IGameServer gameServer;
    private ITemplate template;
    private ConfigSignLocation configSignLocation;

    private Location location;

    private String[] lastInput;

    private Sign sign;

    public IGameServerSign(ConfigSignLocation config, ITemplate template) {
        this.configSignLocation = config;
        this.template = template;

        location = new Location(Bukkit.getWorld(config.getWorld()), config.getX(), config.getY(), config.getZ());
        sign = (Sign) location.getBlock().getState();

        if(!sign.getLocation().getChunk().isLoaded()) sign.getLocation().getChunk().load();
        writeSign("---", "sign", "active", "---");
    }

    public void writeSign(String... lines){
        for(int i = 0; i < 4; i++) {
            sign.setLine(i, lines[i]);
        }
        lastInput = lines;
        sign.update();
    }

    public void updateSign(){
        if(lastInput == null) return;
        writeSign(lastInput);
    }

    public void displayService(){
        writeSign(gameServer.getName(), "players » §b" + gameServer.getOnlinePlayers(), gameServer.getTemplate().getMotd(), gameServer.getTemplate().getName());
    }

    public void reloadSign(Sign sign){
        this.sign = sign;
    }

    public ConfigSignLocation getConfigSignLocation() {
        return configSignLocation;
    }

    public IGameServer getGameServer() {
        return gameServer;
    }

    public ITemplate getTemplate() {
        return template;
    }

    public void setGameServer(IGameServer gameServer) {
        this.gameServer = gameServer;
    }

    public void setConfigSignLocation(ConfigSignLocation configSignLocation) {
        this.configSignLocation = configSignLocation;
    }

    public Location getLocation() {
        return location;
    }

    public void setTemplate(ITemplate template) {
        this.template = template;
    }

    public Sign getSign() {
        return sign;
    }
}
