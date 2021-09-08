package de.polocloud.signs.sign.enumeration;

/**
 * Enum class for declaring the current state
 * of the {@link de.polocloud.signs.sign.base.IGameServerSign}
 */
public enum SignState {

    /**
     *  The {@link de.polocloud.api.template.base.ITemplate template} of the {@link de.polocloud.signs.sign.base.IGameServerSign}
     *  is in maintenance
     */
    MAINTENANCE(),

    /**
     * The {@link de.polocloud.signs.sign.base.IGameServerSign sign} has no set {@link de.polocloud.api.gameserver.base.IGameServer}
     * (Also the default value)
     */
    LOADING(),

    /**
     * The {@link de.polocloud.api.gameserver.base.IGameServer} has reached the maximum player count
     * of the {@link de.polocloud.api.template.base.ITemplate}
     */
    FULL(),

    /**
     * The {@link de.polocloud.api.gameserver.base.IGameServer} of the {@link de.polocloud.signs.sign.base.IGameServerSign} is online
     * and has no players on it
     */
    ONLINE(),

    /**
     * The {@link de.polocloud.api.gameserver.base.IGameServer} of the {@link de.polocloud.signs.sign.base.IGameServerSign} is online
     * and has one or more players on it
     */
    PLAYERS()

}
