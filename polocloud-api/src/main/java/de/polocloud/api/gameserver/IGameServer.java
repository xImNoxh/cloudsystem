package de.polocloud.api.gameserver;

import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.ITemplate;

import java.io.Serializable;
import java.util.List;

public interface IGameServer extends Serializable {

    String getName();

    GameServerStatus getStatus();

    void setStatus(GameServerStatus status);

    long getSnowflake();

    ITemplate getTemplate();

    List<ICloudPlayer> getCloudPlayers();

    long getTotalMemory();

    int getOnlinePlayers();

    int getPort();

    long getPing();

    long getStartTime();

    void stop();

    void terminate();

    void sendPacket(Packet packet);

    String getMotd();

    void setMotd(String motd);

    int getMaxPlayers();

    void setMaxPlayers(int players);

    void setVisible(boolean serviceVisibility);

    boolean getServiceVisibility();

    void update();

}
