package de.polocloud.api.player.def;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.network.packets.cloudplayer.APIGetPlayerByNamePacket;
import de.polocloud.api.network.packets.cloudplayer.APIGetPlayerByUUIDPacket;
import de.polocloud.api.network.protocol.packet.base.response.PacketMessenger;
import de.polocloud.api.network.protocol.packet.base.response.extra.INetworkPromise;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public INetworkPromise<ICloudPlayer> get(String name) {
        INetworkPromise<ICloudPlayer> element = PacketMessenger.createElement(new APIGetPlayerByNamePacket(name), ICloudPlayer.class, SimpleCloudPlayer.class);
        if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {
            element.dummy(getCached(name));
        }
        return element;
    }

    @Override
    public INetworkPromise<ICloudPlayer> get(UUID uniqueId) {
        INetworkPromise<ICloudPlayer> element = PacketMessenger.createElement(new APIGetPlayerByUUIDPacket(uniqueId), ICloudPlayer.class, SimpleCloudPlayer.class);
        if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {
            element.dummy(getCached(uniqueId));
        }
        return element;
    }

    @Override
    public void register(ICloudPlayer cloudPlayer) {
        if (this.getCached(cloudPlayer.getName()) == null) {
            this.cachedObjects.add(cloudPlayer);
        }
    }


    @Override
    public void unregister(ICloudPlayer cloudPlayer) {
        ICloudPlayer cachedObject = getCached(cloudPlayer.getName());
        if (cachedObject != null) {
            this.cachedObjects.remove(cachedObject);
        }
    }

}
