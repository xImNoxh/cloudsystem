package de.polocloud.api.gameserver;

import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.ITemplate;

import java.io.Serializable;
import java.util.List;

public interface IGameServer extends Serializable {

    String getName();

    GameServerStatus getStatus();

    long getSnowflake();

    ITemplate getTemplate();

    List<ICloudPlayer> getCloudPlayers();

    void setStatus(GameServerStatus status);

    long getTotalMemory();

    int getOnlinePlayers();

    int getPort();

    long getPing();

    long getStartTime();

    void stop();

    void sendPacket(IPacket packet);



}
