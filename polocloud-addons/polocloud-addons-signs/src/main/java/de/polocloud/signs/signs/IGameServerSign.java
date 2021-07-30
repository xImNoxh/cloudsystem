package de.polocloud.signs.signs;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.template.ITemplate;
import de.polocloud.signs.SignService;
import de.polocloud.signs.bootstrap.SignBootstrap;
import de.polocloud.signs.config.layout.Layout;
import de.polocloud.signs.converter.SignConverter;
import de.polocloud.signs.enumeration.SignState;
import de.polocloud.signs.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.material.MaterialData;

public class IGameServerSign {

    private IGameServer gameServer;
    private ITemplate template;
    private ConfigSignLocation configSignLocation;

    private Location location;
    private SignState signState = SignState.LOADING;

    private String[] lastInput;

    private Sign sign;

    public IGameServerSign(ConfigSignLocation config, ITemplate template) {
        this.configSignLocation = config;
        this.template = template;

        location = new Location(Bukkit.getWorld(config.getWorld()), config.getX(), config.getY(), config.getZ());
        sign = (Sign) location.getBlock().getState();

        if(!sign.getLocation().getChunk().isLoaded()) sign.getLocation().getChunk().load();

        writeSign();
    }

    public void writeSign(){
        Bukkit.getScheduler().runTask(SignBootstrap.getInstance(), () -> {
            updateState();
            Layout layout = SignService.getInstance().getSignConfig().getSignLayouts().getSignLayouts().get(signState)[0];
            String[] content = layout.getLines();
            for(int i = 0; i < content.length; i++) {
                sign.setLine(i, SignConverter.convertSignLayout(gameServer, content[i]));
            }
            lastInput = content;
            sign.update();

            if(!layout.getBlockLayout().isUse()) return;
            BlockState blockState = PlayerUtils.getBlockSignAttachedTo(sign.getBlock()).getState();
            Material material = Material.getMaterial(layout.getBlockLayout().getId());

            if(material == null) return;

            blockState.setType(material);

            if(layout.getBlockLayout().getSubId() > -1) {
                blockState.setData(new MaterialData(material, (byte) layout.getBlockLayout().getSubId()));
            }

            /*
            System.out.println(signState.toString());
            System.out.println(gameServer.getName());
            System.out.println(gameServer.getOnlinePlayers());
            System.out.println(material);
            System.out.println(layout.getBlockLayout().getSubId());

             */
            blockState.update(true);
        });
    }

    public void updateState(){
        if(template.isMaintenance()){
            signState = SignState.MAINTENANCE;
            return;
        }

        if(gameServer == null){
            signState = SignState.LOADING;
            return;
        }

        if(gameServer.getOnlinePlayers() <= 0){
            System.out.println(gameServer.getOnlinePlayers());
            signState = SignState.ONLINE;
            return;
        }

        if(template.getMaxPlayers() <= gameServer.getOnlinePlayers()){
            signState = SignState.FULL;
            return;
        }

        if(gameServer.getOnlinePlayers() >= 1) {
            System.out.println(gameServer.getOnlinePlayers());
            signState = SignState.PLAYERS;
        }
        System.out.println("no sign information founded");
    }

    public void updateSign(){
        if(lastInput == null) return;
        writeSign();
    }


    public void displayService(){
        signState = SignState.ONLINE;
        writeSign();
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

    public SignState getSignState() {
        return signState;
    }
}
