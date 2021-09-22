package de.polocloud.api.wrapper.base;

import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.helper.ITerminatable;
import de.polocloud.api.network.packets.wrapper.WrapperRequestUnusedMemory;
import de.polocloud.api.network.packets.wrapper.WrapperRequestUsedMemory;
import de.polocloud.api.network.protocol.packet.IPacketSender;
import de.polocloud.api.network.protocol.packet.base.response.PacketMessenger;
import de.polocloud.api.network.protocol.packet.base.response.base.IResponse;
import de.polocloud.api.network.protocol.packet.base.response.def.Response;
import de.polocloud.api.player.ICloudPlayer;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface IWrapper extends IPacketSender, ITerminatable {

    /**
     * The name of this wrapper
     */
    String getName();

    /**
     * Returns the amount of services this wrapper can start simultaneously
     */
    int getMaxSimultaneouslyStartingServices();

    /**
     * The currently starting services
     */
    int getCurrentlyStartingServices();

    /**
     * Sets the currently starting services amount
     *
     * @param i the amount of services
     */
    void setCurrentlyStartingServices(int i);

    /**
     * Returns the amount of RAM the wrapper uses at the moment in MB
     * This is a query-request and takes some time to process
     * because of packets
     */
    default long getUsedMemory() {
        long memory = 0;
        for (IGameServer server : getServers()) {
            memory += server.getTotalMemory();
        }
        return memory;
    }

    /**
     * Returns the CPU usage of this wrapper.
     * The returned value will be between 0 - 1
     */
    float getCpuUsage();

    /**
     * Returns the amount of RAM the wrapper has left
     */
    default long getUnusedMemory() {
        return getMaxMemory() - getUsedMemory();
    }

    /**
     * If this wrapper is authenticated
     */
    boolean isAuthenticated();

    /**
     * Sets the authentication state of this wrapper
     *
     * @param authenticated the state
     */
    void setAuthenticated(boolean authenticated);

    /**
     * Returns whether this wrapper has the specified memory left
     */
    default boolean hasEnoughMemory(long memory) {
        return getUnusedMemory() >= memory;
    }

    /**
     * The identification id of this wrapper
     */
    long getSnowflake();

    /**
     * The maximum memory this wrapper is allowed to use
     */
    long getMaxMemory();

    /**
     * Checks if the wrapper is (still) connected
     * to the master instance or not
     */
    boolean isStillConnected();

    /**
     * Gets a list of all {@link IGameServer}s running on this wrapper
     */
    List<IGameServer> getServers();

    /**
     * The netty context
     */
    ChannelHandlerContext ctx();

    /**
     * Updates this wrapper all over the network
     */
    void update();

    /**
     * Starts an {@link IGameServer} on this wrapper
     *
     * @param gameServer the server
     */
    void startServer(IGameServer gameServer);

    /**
     * Stops an {@link IGameServer} on this wrapper
     */
    void stopServer(IGameServer gameServer);

    /**
     * All {@link ICloudPlayer} that are on this {@link IWrapper}
     */
    default List<ICloudPlayer> getPlayers() {
        List<ICloudPlayer> cloudPlayers = new ArrayList<>();
        for (IGameServer server : getServers()) {
            for (ICloudPlayer player : server.getPlayers()) {
                if (cloudPlayers.stream().noneMatch(cp -> cp.getName().equalsIgnoreCase(player.getName()))) {
                    cloudPlayers.add(player);
                }
            }
        }
        return cloudPlayers;
    }
}
