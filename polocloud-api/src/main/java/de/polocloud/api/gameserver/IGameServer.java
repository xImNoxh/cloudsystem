package de.polocloud.api.gameserver;

import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.template.ITemplate;

public interface IGameServer {

    String getName();

    GameServerStatus getStatus();

    long getSnowflake();

    ITemplate getTemplate();

    void setStatus(GameServerStatus status);

    int getPort();

    long getStartTime();

    void stop();

    void sendPacket(IPacket packet);

}
