package de.polocloud.api.template.base;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.pool.PoloObject;
import de.polocloud.api.template.helper.GameServerVersion;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.api.util.Snowflake;
import de.polocloud.api.wrapper.base.IWrapper;

import java.util.List;

public interface ITemplate extends PoloObject<ITemplate> {

    /**
     * The minimum amount of servers that
     * must always be online
     */
    int getMinServerCount();

    default long getSnowflake() {
        return Snowflake.getInstance().nextId();
    }

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

    /**
     * The percent for a new service of this template to start
     */
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

    /**
     * Checks if this {@link ITemplate} is a {@link de.polocloud.api.fallback.base.IFallback} instance
     */
    default boolean isLobby() {
        return PoloCloudAPI.getInstance().getFallbackManager().getAvailableFallbacks().stream().anyMatch(fallback -> fallback.getTemplateName().equalsIgnoreCase(this.getName()));
    }

    /**
     * List of all online {@link IGameServer}
     */
    List<IGameServer> getServers();

    default boolean isDynamic() {
        return !isStatic();
    }

}
