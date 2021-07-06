package de.polocloud.api.groups;

public interface Group {

    String getName();

    int getMemory();

    int getMaxPlayers();

    String getMotd();

    int getMinServices();

    int getMaxServices();

    int getPercentageToStartNewService();

    boolean isMaintenance();

}
