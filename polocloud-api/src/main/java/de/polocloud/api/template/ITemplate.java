package de.polocloud.api.template;

import de.polocloud.api.common.INamable;

import java.io.Serializable;

public interface ITemplate extends INamable, Serializable {

    /**
     * The minimum amount of servers that
     * must always be online
     */
    int getMinServerCount();

    /**
     * The maximum amount of servers that
     * may at maximum be online
     */
    int getMaxServerCount();

    /**
     * The amount of players that may be online
     * at maximum on one of the servers of this template
     */
    int getMaxPlayers();

    /**
     * Sets the amount of maxPlayers
     *
     * @param maxPlayers the amount
     */
    void setMaxPlayers(int maxPlayers);

    /**
     * The amount of memory this template may use
     */
    int getMaxMemory();

    /**
     * The default motd of this template
     */
    String getMotd();

    /**
     * If this template is in maintenance
     */
    boolean isMaintenance();

    /**
     * The {@link TemplateType} of this template
     * to identify whether it's a proxy or spigot instance
     */
    TemplateType getTemplateType();

    /**
     * The {@link GameServerVersion} to get the version
     * Does only work if it's a spigot template
     */
    GameServerVersion getVersion();

    //TODO: DOCUMENTATION
    int getServerCreateThreshold();

    /**
     * Sets the maintenance state of this template
     *
     * @param state the maintenance state
     */
    void setMaintenance(boolean state);

    /**
     * Gets all Wrapper names that are allowed
     * to start servers from this template
     *
     * @return string array of names
     */
    String[] getWrapperNames();

    /**
     * If this group is static or dynamic
     *
     * 'STATIC' -> All files will be saved in the static folder
     *             and the server gets loaded again from the old
     *             cache when the server stops
     *
     * 'DYNAMIC' -> All files will be deleted after the server stops
     *              and if it starts again, all files will be generated
     *              from the template or will be new generated
     *              Nothing is saved here if not saved in the template
     */
    boolean isStatic();

}
