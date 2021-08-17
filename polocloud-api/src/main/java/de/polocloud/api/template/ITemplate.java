package de.polocloud.api.template;

import de.polocloud.api.common.INamable;

import java.io.Serializable;

public interface ITemplate extends INamable, Serializable {

    int getMinServerCount();

    int getMaxServerCount();

    int getMaxPlayers();

    void setMaxPlayers(int maxPlayers);

    int getMaxMemory();

    String getMotd();

    boolean isMaintenance();

    void setMaintenance(boolean state);

    TemplateType getTemplateType();

    GameServerVersion getVersion();

    int getServerCreateThreshold();

    String[] getWrapperNames();

    boolean isStatic();

}
