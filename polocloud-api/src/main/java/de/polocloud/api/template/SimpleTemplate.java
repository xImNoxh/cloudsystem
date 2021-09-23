package de.polocloud.api.template;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.event.impl.template.TemplateMaintenanceChangeEvent;
import de.polocloud.api.event.impl.template.TemplateUpdateEvent;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.packets.gameserver.TemplateUpdatePacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.helper.GameServerVersion;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.helper.TemplateType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter @NoArgsConstructor
public class SimpleTemplate implements ITemplate {

    private String name;

    private boolean maintenance;
    private boolean staticServer;

    private int maxServerCount;
    private int minServerCount;

    private int memory;
    private int maxPlayers;

    private TemplateType templateType;

    private GameServerVersion version;

    private String motd;

    private int serverCreateThreshold;

    private String[] wrapperNames;


    private boolean changedMaintenance;


    public SimpleTemplate(String name, boolean staticServer, int maxServerCount, int minServerCount, TemplateType templateType, GameServerVersion version, int maxPlayers, int memory, boolean maintenance, String motd, int serverCreateThreshold, String[] wrapperNames) {
        this.name = name;
        this.staticServer = staticServer;
        this.maxServerCount = maxServerCount;
        this.minServerCount = minServerCount;
        this.templateType = templateType;
        this.version = version;
        this.serverCreateThreshold = serverCreateThreshold;
        this.wrapperNames = wrapperNames;
        this.maxPlayers = maxPlayers;
        this.memory = memory;
        this.maintenance = maintenance;
        this.motd = motd;
    }

    @Override
    public String getMotd() {
        return motd;
    }

    @Override
    public GameServerVersion getVersion() {
        return version;
    }

    @Override
    public int getServerCreateThreshold() {
        return serverCreateThreshold;
    }

    @Override
    public String[] getWrapperNames() {
        return this.wrapperNames;
    }

    @Override
    public boolean isStatic() {
        return this.staticServer;
    }

    @Override
    public int getMinServerCount() {
        return this.minServerCount;
    }

    @Override
    public int getMaxServerCount() {
        return this.maxServerCount;
    }

    @Override
    public TemplateType getTemplateType() {
        return this.templateType;
    }

    @Override
    public boolean isMaintenance() {
        return maintenance;
    }

    @Override
    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
        this.changedMaintenance = true;
    }


    @Override
    public ITemplate sync() {
        if (PoloCloudAPI.getInstance() == null || PoloCloudAPI.getInstance().getTemplateManager() == null) {
            return this;
        }
        return PoloCloudAPI.getInstance().getTemplateManager().getTemplate(this.name);
    }


    @Override
    public void update() {

        if (this.changedMaintenance) {
            this.changedMaintenance = false;
            PoloCloudAPI.getInstance().getEventManager().fireEvent(new TemplateMaintenanceChangeEvent(this, maintenance));
        }

        PoloCloudAPI.getInstance().getEventManager().fireEvent(new TemplateUpdateEvent(this)); //Calling event

        SimpleCachedTemplateManager templateManager = (SimpleCachedTemplateManager) PoloCloudAPI.getInstance().getTemplateManager();
        templateManager.update(this);

        if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {

            PoloCloudAPI.getInstance().getTemplateManager().getTemplateSaver().save(this);
            for (IGameServer gameServer : this.getServers()) {
                gameServer.update();
            }

        } else {
            PoloCloudAPI.getInstance().sendPacket(new TemplateUpdatePacket(this));
        }
    }

    @Override
    public List<IGameServer> getServers() {
        return PoloCloudAPI.getInstance().getGameServerManager().stream().filter(gameServer -> gameServer.getTemplate() != null && gameServer.getTemplate().getName().equalsIgnoreCase(this.name)).collect(Collectors.toList());
    }
    @Override
    public List<ICloudPlayer> getPlayers() {
        return PoloCloudAPI.getInstance().getCloudPlayerManager().stream().filter(cloudPlayer -> (cloudPlayer.getMinecraftServer() != null && cloudPlayer.getMinecraftServer().getTemplate().getName().equalsIgnoreCase(this.name)) || (cloudPlayer.getProxyServer() != null && cloudPlayer.getProxyServer().getTemplate().getName().equalsIgnoreCase(this.name))).collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isLobby() {
        if (PoloCloudAPI.getInstance().getFallbackManager() == null) {
            return false;
        }
        if (PoloCloudAPI.getInstance().getFallbackManager().getAvailableFallbacks().isEmpty()) {
            return false;
        }
        return PoloCloudAPI.getInstance().getFallbackManager().getAvailableFallbacks().stream().anyMatch(fallback -> fallback != null && fallback.getTemplateName().equalsIgnoreCase(this.getName()));
    }

    @Override
    public boolean isDynamic() {
        return !isStatic();
    }

    @Override
    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    @Override
    public int getMaxMemory() {
        return memory;
    }
}
