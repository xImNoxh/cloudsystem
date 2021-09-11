package de.polocloud.api.template;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.impl.server.CloudGameServerMaintenanceUpdateEvent;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.template.helper.GameServerVersion;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.helper.TemplateType;

import java.util.List;
import java.util.stream.Collectors;

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

        PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudGameServerMaintenanceUpdateEvent(this, maintenance));
    }

    @Override
    public List<IGameServer> getServers() {
        return PoloCloudAPI.getInstance().getGameServerManager().stream().filter(gameServer -> gameServer.getTemplate() != null && gameServer.getTemplate().getName().equalsIgnoreCase(this.name)).collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return this.name;
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
