package de.polocloud.api.player;

import de.polocloud.api.network.request.base.future.PoloFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

    //TODO
    @Override
    public PoloFuture<ICloudPlayer> get(String name) {
        return null;
    }

    //TODO
    @Override
    public PoloFuture<ICloudPlayer> get(long snowflake) {
        return null;
    }

    @Override
    public void registerPlayer(ICloudPlayer cloudPlayer) {
        this.cachedObjects.add(cloudPlayer);
    }

    @Override
    public void unregisterPlayer(ICloudPlayer cloudPlayer) {
        ICloudPlayer cachedObject = getCached(cloudPlayer.getName());
        if (cachedObject != null) {
            this.cachedObjects.remove(cachedObject);
        }
    }

}
