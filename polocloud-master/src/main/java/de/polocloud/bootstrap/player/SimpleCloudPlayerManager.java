package de.polocloud.bootstrap.player;

import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SimpleCloudPlayerManager implements ICloudPlayerManager {

    private List<ICloudPlayer> cloudPlayers = new ArrayList<>();

    @Override
    public void register(ICloudPlayer cloudPlayer) {
        cloudPlayers.add(cloudPlayer);
    }

    @Override
    public void unregister(ICloudPlayer cloudPlayer) {
        cloudPlayers.remove(cloudPlayer);
    }

    @Override
    public List<ICloudPlayer> getAllOnlinePlayers() {
        return this.cloudPlayers;
    }

    @Override
    public ICloudPlayer getOnlinePlayer(String name) {

        for (ICloudPlayer cloudPlayer : this.cloudPlayers) {
            if(cloudPlayer.getName().equalsIgnoreCase(name)){
                return cloudPlayer;
            }
        }

        return null;
    }

    @Override
    public ICloudPlayer getOnlinePlayer(UUID uuid) {

        for (ICloudPlayer cloudPlayer : this.cloudPlayers) {
            if(cloudPlayer.getUUID().toString().equalsIgnoreCase(uuid.toString())){
                return cloudPlayer;
            }
        }

        return null;
    }

    @Override
    public boolean isPlayerOnline(String name) {
        return getOnlinePlayer(name) != null;
    }

    @Override
    public boolean isPlayerOnline(UUID uuid) {
        return getOnlinePlayer(uuid) != null;

    }
}
