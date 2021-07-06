package de.polocloud.master.group;

import de.polocloud.api.groups.Group;
import de.polocloud.database.repository.annotations.Column;
import de.polocloud.database.repository.annotations.Entity;
import de.polocloud.database.repository.annotations.PrimaryKey;

@Entity
public class GroupImpl implements Group {

    @Column
    @PrimaryKey
    private String name;

    @Column
    private int memory;

    @Column
    private int maxPlayers;

    @Column
    private String motd;

    @Column
    private int minServices;

    @Column
    private int maxServices;

    @Column
    private int percentageToStartNewService;

    @Column
    private boolean maintenance;

    public GroupImpl() { }

    public GroupImpl(String name, int memory, int maxPlayers, String motd, int minServices, int maxServices, int percentageToStartNewService, boolean maintenance) {
        this.name = name;
        this.memory = memory;
        this.maxPlayers = maxPlayers;
        this.motd = motd;
        this.minServices = minServices;
        this.maxServices = maxServices;
        this.percentageToStartNewService = percentageToStartNewService;
        this.maintenance = maintenance;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getMemory() {
        return memory;
    }

    @Override
    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public String getMotd() {
        return motd;
    }

    @Override
    public int getMinServices() {
        return minServices;
    }

    @Override
    public int getMaxServices() {
        return maxServices;
    }

    @Override
    public int getPercentageToStartNewService() {
        return percentageToStartNewService;
    }

    @Override
    public boolean isMaintenance() {
        return maintenance;
    }
}
