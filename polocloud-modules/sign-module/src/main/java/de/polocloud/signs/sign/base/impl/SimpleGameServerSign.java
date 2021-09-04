package de.polocloud.signs.sign.base.impl;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.signs.bootstraps.PluginBootstrap;
import de.polocloud.signs.sign.base.IGameServerSign;
import de.polocloud.signs.sign.enumeration.SignState;
import de.polocloud.signs.sign.layout.Layout;
import de.polocloud.signs.sign.layout.converter.SignConverter;
import de.polocloud.signs.sign.location.SignLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

public class SimpleGameServerSign implements IGameServerSign {

    private IGameServer gameServer;
    private ITemplate template;
    private SignLocation signLocation;

    private Location location;
    private SignState signState = SignState.LOADING;

    private Material defaultBlock;

    private String[] lastInput;

    private Sign sign;

    public SimpleGameServerSign(SignLocation signLocation) {
        this.signLocation = signLocation;

        this.template = PoloCloudAPI.getInstance().getTemplateManager().getTemplate(signLocation.getTemplate());


        this.location = new Location(Bukkit.getWorld(signLocation.getWorld()), signLocation.getX(), signLocation.getY(), signLocation.getZ());
        this.sign = (Sign) location.getBlock().getState();

        this.defaultBlock = PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getBlockBehindSign(sign.getBlock()).getType();


        if (!sign.getLocation().getChunk().isLoaded()) sign.getLocation().getChunk().load();


        writeSign(false);
    }

    @Override
    public void writeSign(boolean writeClean) {
            if (writeClean){
                for (int i = 0; i < 4; i++) {
                    sign.setLine(i, SignConverter.convertSignLayout(this, " "));
                }
                sign.update();
                return;
            }
            updateSignState();

            Layout layout = PluginBootstrap.getInstance().getSignService().getCurrentGlobalConfig().getSignLayouts().get(this.signState)[0];
            String[] content = layout.getLines();

            for (int i = 0; i < content.length; i++) {
                sign.setLine(i, SignConverter.convertSignLayout(this, content[i]));
            }

            this.lastInput = content;
            sign.update();

            if (!layout.getBlockLayout().isUse()) return;
            BlockState blockState = PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getBlockBehindSign(sign.getBlock()).getState();
            Material material = Material.getMaterial(layout.getBlockLayout().getId());

            if (material == null) return;

            blockState.setType(material);

            if (layout.getBlockLayout().getSubId() > -1) {
                blockState.setData(new MaterialData(material, (byte) layout.getBlockLayout().getSubId()));
            }
        new BukkitRunnable() {
            public void run() {
                blockState.update(true);
            }
        }.runTask(PluginBootstrap.getInstance());

    }

    @Override
    public void cleanUp() {
        PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getBlockBehindSign(sign.getBlock()).setType(defaultBlock);
        writeSign(true);
    }

    @Override
    public void updateSignState() {
        if(template.isMaintenance()){
            this.signState = SignState.MAINTENANCE;
            return;
        }

        if (gameServer == null) {
            this.signState = SignState.LOADING;
            return;
        }

        if (gameServer.getOnlinePlayers() >= gameServer.getTemplate().getMaxPlayers()) {
            this.signState = SignState.FULL;
            return;
        }

        if (gameServer.getOnlinePlayers() <= 0) {
            this.signState = SignState.ONLINE;
            return;
        }

        if (gameServer.getOnlinePlayers() >= 1) {
            this.signState = SignState.PLAYERS;
            return;
        }
        System.err.println("Error while updating SignState for gameserver: " + gameServer.getName() + "! No option was found.");
    }

    @Override
    public void updateSign() {
        if (this.lastInput == null) return;
        writeSign(false);
    }

    @Override
    public void reloadSign(Sign sign) {
        setSign(sign);
        writeSign(true);
    }

    @Override
    public IGameServer getGameServer() {
        return this.gameServer;
    }

    @Override
    public ITemplate getTemplate() {
        return this.template;
    }

    @Override
    public SignState getSignState() {
        return this.signState;
    }

    @Override
    public Sign getSign() {
        return this.sign;
    }

    @Override
    public SignLocation getSignLocation() {
        return this.signLocation;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public void setGameServer(IGameServer gameServer) {
        this.gameServer = gameServer;
    }

    @Override
    public void setTemplate(ITemplate template) {
        this.template = template;
    }

    @Override
    public void setSignLocation(SignLocation location) {
        this.signLocation = location;
    }

    @Override
    public void setSignState(SignState signState) {
        this.signState = signState;
    }

    @Override
    public void setSign(Sign sign) {
        this.sign = sign;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }
}
