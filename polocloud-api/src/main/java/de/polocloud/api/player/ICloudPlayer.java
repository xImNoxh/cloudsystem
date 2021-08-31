package de.polocloud.api.player;

import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.common.INamable;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.pool.PoloObject;
import de.polocloud.api.property.IProperty;
import de.polocloud.api.util.Snowflake;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface ICloudPlayer extends PoloObject<ICloudPlayer>, CommandExecutor {

    @Override
    default long getSnowflake() {
        return Snowflake.getInstance().nextId();
    }

    /**
     * Gets the {@link UUID} of this player
     */
    UUID getUUID();

    /**
     * Gets the current proxy-Server as {@link IGameServer}
     * of this player
     */
    IGameServer getProxyServer();

    /**
     * Gets the current spigot-Server as {@link IGameServer}
     * of this player
     */
    IGameServer getMinecraftServer();

    /**
     * Connects this player to a {@link IGameServer}
     *
     * @param gameServer the server to connect to
     */
    void sendTo(IGameServer gameServer);

    /**
     * Sends the tabList header and footer to this player
     *
     * @param header the header
     * @param footer the footer
     */
    void sendTabList(String header, String footer);

    /**
     * Checks if this player has a given permission
     *
     * @param permission the permission
     */
    CompletableFuture<Boolean> hasPermissions(String permission);

    /**
     * Updates this player all over the network
     */
    void update();

    /**
     * Kicks this player from the network
     * with a given reason to display
     *
     * @param reason the reason
     */
    void kick(String reason);

    /**
     * Sends this player to a fallback
     */
    void sendToFallback();

    /**
     * Gets a list of all {@link IProperty} this player has
     *
     * @return list of properties
     */
    List<IProperty> getProperties();

    /**
     * Gets an {@link IProperty} by its name
     *
     * @param name the name
     * @return property or null if not found
     */
    IProperty getProperty(String name);

    /**
     * Adds or overrides an {@link IProperty}
     *
     * @param consumer the consumer
     */
    void insertProperty(Consumer<IProperty> consumer);

    /**
     * Deletes an {@link IProperty} of this player
     *
     * @param name the name of the property
     */
    void deleteProperty(String name);

}
