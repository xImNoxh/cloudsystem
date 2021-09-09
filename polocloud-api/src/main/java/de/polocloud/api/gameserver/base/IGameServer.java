package de.polocloud.api.gameserver.base;

import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.network.protocol.packet.IPacketReceiver;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.pool.PoloObject;
import de.polocloud.api.property.IProperty;
import de.polocloud.api.property.IPropertyHolder;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.wrapper.base.IWrapper;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.function.Consumer;

public interface IGameServer extends PoloObject<IGameServer>, IPacketReceiver, IPropertyHolder {

    /**
     * Creates a new empty {@link IGameServer}
     * where every field is null and has to be set
     */
    static IGameServer create() {
        return new SimpleGameServer();
    }

    /**
     * The {@link GameServerStatus} of this game server to identify
     * the state the server currently is in
     */
    GameServerStatus getStatus();

    /**
     * Checks if the server is registered on the cloud master
     */
    boolean isRegistered();

    /**
     * Sets the authentication state
     *
     * @param b the boolean
     */
    void setRegistered(boolean b);

    /**
     * Gets the motd of this server as String
     */
    String getMotd();

    /**
     * The host of this server
     */
    String getHost();

    /**
     * Sets the host of this server
     *
     * @param host the address as string
     */
    void setHost(String host);
    //TODO CHECK HOST SETTING WITH MULTI ROOT

    /**
     * Gets the amount of players that are allowed
     * to be on this game server as maximum
     */
    int getMaxPlayers();

    /**
     * The netty context
     */
    ChannelHandlerContext ctx();

    /**
     * The visibility state of this server
     */
    boolean getServiceVisibility();

    /**
     * The {@link ITemplate} of this server to get all infos
     * about the parent-group of this server
     */
    ITemplate getTemplate();

    /**
     * The {@link IWrapper} this server is running on
     */
    IWrapper getWrapper();

    /**
     * Gets all {@link IWrapper} for this {@link IGameServer}
     *
     * @return wrapper list
     */
    IWrapper[] getAllWrappers();

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
     * The id of this server (e.g. Lobby-1 <- "1" would be id)
     */
    int getId();

    /**
     * Gets the time the server started at
     *
     * @return time as millis (long)
     */
    long getStartTime();

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

    /**
     * Updates this server in current cache instance
     * but does not send a packet to all instances
     * to declare that this service got updated
     */
    void updateInternally();

    /**
     * Sets the port of this server
     *
     * @param port the port
     */
    void setPort(int port);

    /**
     * Sets the memory of this server
     *
     * @param memory the port
     */
    void setMemory(long memory);

    /**
     * Sets the snowflake of this server
     *
     * @param snowflake the snowflake
     */
    void setSnowflake(long snowflake);

    /**
     * Sets the started time of this server
     *
     * @param ms the time in millis
     */
    void setStartedTime(long ms);

    /**
     * Sets a new random snowflake
     */
    void newSnowflake();

    /**
     * Sets the {@link ITemplate} of this server
     *
     * @param template the template
     */
    void setTemplate(String template);

    /**
     * Sets the id of this server
     *
     * @param id the id
     */
    void setId(int id);

    /**
     * Sets the {@link IProperty}s of this server
     *
     * @param properties the properties
     */
    void setProperties(List<IProperty> properties);

    /**
     * Copies this gameserver 1:1 and accepts the consumer after
     * to modify the copied version and handle it
     *
     * @param consumer the handler
     */
    void clone(Consumer<IGameServer> consumer);

    default void applyTemplate(ITemplate template) {
        this.setTemplate(template.getName());
        this.setMotd(template.getMotd());
        this.setMaxPlayers(template.getMaxPlayers());
        this.setMemory(template.getMaxMemory());
    }

}
