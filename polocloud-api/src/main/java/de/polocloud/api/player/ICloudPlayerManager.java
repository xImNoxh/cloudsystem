package de.polocloud.api.player;

import java.util.List;
import java.util.UUID;

public interface ICloudPlayerManager {

    void register(ICloudPlayer cloudPlayer);

    void unregister(ICloudPlayer cloudPlayer);

    List<ICloudPlayer> getAllOnlinePlayers();

    ICloudPlayer getOnlinePlayer(String name);

    ICloudPlayer getOnlinePlayer(UUID uuid);

    boolean isPlayerOnline(String name);

    boolean isPlayerOnline(UUID uuid);

}
