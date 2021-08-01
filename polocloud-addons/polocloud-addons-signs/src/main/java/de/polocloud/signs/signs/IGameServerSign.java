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

    private Material defaultBlock;

    private String[] lastInput;

    private Sign sign;

    public IGameServerSign(ConfigSignLocation config, ITemplate template) {
        this.configSignLocation = config;
        this.template = template;

        this.location = new Location(Bukkit.getWorld(config.getWorld()), config.getX(), config.getY(), config.getZ());
        this.sign = (Sign) location.getBlock().getState();

        if (!sign.getLocation().getChunk().isLoaded()) sign.getLocation().getChunk().load();

        this.defaultBlock = PlayerUtils.getBlockSignAttachedTo(sign.getBlock()).getType();

        writeSign(false);
    }

    public void writeSign(boolean clean) {
        Bukkit.getScheduler().runTask(SignBootstrap.getInstance(), () -> {
            if (clean) {
                for (int i = 0; i < 4; i++) {
                    sign.setLine(i, SignConverter.convertSignLayout(this, " "));
                }
                sign.update();
                return;
            }
            updateState();
            Layout layout = SignService.getInstance().getSignConfig().getSignLayouts().getSignLayouts().get(signState)[0];
            String[] content = layout.getLines();
            for (int i = 0; i < content.length; i++) {
                sign.setLine(i, SignConverter.convertSignLayout(this, content[i]));
            }
            this.lastInput = content;
            sign.update();

            if (!layout.getBlockLayout().isUse()) return;
            BlockState blockState = PlayerUtils.getBlockSignAttachedTo(sign.getBlock()).getState();
            Material material = Material.getMaterial(layout.getBlockLayout().getId());

            if (material == null) return;

            blockState.setType(material);

            if (layout.getBlockLayout().getSubId() > -1) {
                blockState.setData(new MaterialData(material, (byte) layout.getBlockLayout().getSubId()));
            }
            blockState.update(true);
        });
    }

    public void clean() {
        PlayerUtils.getBlockSignAttachedTo(sign.getBlock()).setType(defaultBlock);
        writeSign(true);
    }

    public void updateState() {
        if (template.isMaintenance()) {
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
        System.out.println("no sign information founded");
    }

    public void updateSign() {
        if (this.lastInput == null) return;
        writeSign(false);
    }

    public void reloadSign(Sign sign) {
        this.sign = sign;
    }

    public ConfigSignLocation getConfigSignLocation() {
        return configSignLocation;
    }

    public void setConfigSignLocation(ConfigSignLocation configSignLocation) {
        this.configSignLocation = configSignLocation;
    }

    public IGameServer getGameServer() {
        return gameServer;
    }

    public void setGameServer(IGameServer gameServer) {
        this.gameServer = gameServer;
    }

    public ITemplate getTemplate() {
        return template;
    }

    public void setTemplate(ITemplate template) {
        this.template = template;
    }

    public Location getLocation() {
        return location;
    }

    public Sign getSign() {
        return sign;
    }

    public SignState getSignState() {
        return signState;
    }
}
