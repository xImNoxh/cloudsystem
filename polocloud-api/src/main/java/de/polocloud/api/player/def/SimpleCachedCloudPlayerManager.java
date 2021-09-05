package de.polocloud.api.player.def;

import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;

import java.util.ArrayList;
import java.util.List;

public class SimpleCachedCloudPlayerManager implements ICloudPlayerManager {

    private List<ICloudPlayer> cachedObjects;

    public SimpleCachedCloudPlayerManager() {
        this.cachedObjects = new ArrayList<>();
    }

    @Override
    public List<ICloudPlayer> getAllCached() {
        return cachedObjects;
    }

    @Override
    public void setCached(List<ICloudPlayer> cachedObjects) {
        this.cachedObjects = cachedObjects;
    }

    @Override
    public void registerPlayer(ICloudPlayer cloudPlayer) {
        if (this.getCached(cloudPlayer.getName()) == null) {
            this.cachedObjects.add(cloudPlayer);
        }
    }

    @Override
    public void unregisterPlayer(ICloudPlayer cloudPlayer) {
        ICloudPlayer cachedObject = getCached(cloudPlayer.getName());
        if (cachedObject != null) {
            this.cachedObjects.remove(cachedObject);
        }
    }

}
