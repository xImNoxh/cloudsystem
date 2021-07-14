package de.polocloud.api.template;

import de.polocloud.api.common.INamable;

import java.io.Serializable;

public interface ITemplate extends INamable, Serializable {

    int getMinServerCount();

    int getMaxServerCount();

    int getMaxPlayers();

    int getMaxMemory();

    String getMotd();

    boolean isMaintenance();

    TemplateType getTemplateType();

    GameServerVersion getVersion();

    void setMaintenance(boolean state);

    String[] getWrapperNames();

}
