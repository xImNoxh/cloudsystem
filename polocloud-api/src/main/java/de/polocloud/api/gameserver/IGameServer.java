package de.polocloud.api.gameserver;

import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.ITemplate;
import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;
import java.util.List;

public interface IGameServer extends Serializable {

    /**
     * The name of this game server
     */
    String getName();

    /**
     * The {@link GameServerStatus} of this game server to identify
     * the state the server currently is in
     */
    GameServerStatus getStatus();

    /**
     * Gets the motd of this server as String
     */
    String getMotd();

    /**
     * Gets the amount of players that are allowed
     * to be on this game server as maximum
     */
    int getMaxPlayers();

    /**
     * The visibility state of this server
     */
    boolean getServiceVisibility();

    /**
     * The snowflake of this game server to identify
     * this server out of other servers
     * Its like a {@link java.util.UUID}
     *
     * @return snowflake as long
     */
    long getSnowflake();

    /**
     * The {@link ITemplate} of this server to get all infos
     * about the parent-group of this server
     */
    ITemplate getTemplate();

    /**
     * All {@link ICloudPlayer}s that are on this game server
     * currently online in a {@link List}
     */
    List<ICloudPlayer> getCloudPlayers();

    /**
     * Gets the total memory of this server
     *
     * @return memory as long
     */
    long getTotalMemory();

    /**
     * Gets the amount of online players on this server
     */
    int getOnlinePlayers();

    /**
     * Gets the port where this server is running on
     */
    int getPort();

    /**
     * Gets the ping of the server
     *
     * @return ping as long
     */
    long getPing();

    /**
     * Gets the time the server started at
     *
     * @return time as millis (long)
     */
    long getStartTime();

    /**
     * Stops this server
     *
     * This should only be done if the {@link GameServerStatus} of this server
     * is {@link GameServerStatus#RUNNING} otherwise you should use
     * {@link IGameServer#terminate()} to terminate the process of this server
     */
    void stop();

    /**
     * Terminates the process of this server
     * and deletes all files of it
     */
    void terminate();

    /**
     * Sends a {@link Packet} from this server
     *
     * @param packet the packet to send
     */
    void sendPacket(Packet packet);

    /**
     * Sets the {@link GameServerStatus} of this game server
     *
     * @param status the status to set
     */
    void setStatus(GameServerStatus status);

    /**
     * Sets the motd of this game server
     *
     * @param motd the motd to set
     */
    void setMotd(String motd);

    /**
     * Sets the amount of maxplayers on this server
     *
     * @param players the amount
     */
    void setMaxPlayers(int players);

    /**
     * Makes this server visible to appear on signs for example
     *
     * @param serviceVisibility the visibility state
     */
    void setVisible(boolean serviceVisibility);

    /**
     * Updates this server and syncs it all over the network
     * to update the changes in every cache
     */
    void update();

}
