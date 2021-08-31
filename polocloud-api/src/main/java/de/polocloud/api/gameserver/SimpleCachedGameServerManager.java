package de.polocloud.api.gameserver;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.event.impl.server.CloudGameServerStatusChangeEvent;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.network.request.base.future.PoloFuture;
import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimpleCachedGameServerManager implements IGameServerManager {

    private List<IGameServer> cachedObjects;

    public SimpleCachedGameServerManager() {
        this.cachedObjects = new ArrayList<>();
    }

    @Override
    public void setCached(List<IGameServer> cachedObjects) {
        this.cachedObjects = cachedObjects;
    }

    //TODO
    @Override
    public PoloFuture<IGameServer> get(String name) {
        return null;
    }
    //TODO
    @Override
    public PoloFuture<IGameServer> get(long snowflake) {
        return null;
    }

    @Override
    public List<IGameServer> getAllCached() {
        return cachedObjects;
    }

    @Override
    public IGameServer getCachedObject(ChannelHandlerContext ctx) {
        return this.cachedObjects.stream().filter(iGameServer -> iGameServer.ctx().equals(ctx)).findFirst().orElse(null);
    }

    @Override
    public void registerGameServer(IGameServer gameServer) {
        if (this.getCached(gameServer.getName()) == null) {
            this.cachedObjects.add(gameServer);

            if (PoloCloudAPI.getInstance().getType().isCloud()) {
                PoloCloudAPI.getInstance().updateCache();
            }
        }
    }

    @Override
    public void unregisterGameServer(IGameServer gameServer) {
        IGameServer cachedObject = this.getCached(gameServer.getName());

        cachedObjects.remove(cachedObject);
        PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudGameServerStatusChangeEvent(gameServer, GameServerStatus.STOPPING));

        if (PoloCloudAPI.getInstance().getType().isCloud()) {
            PoloCloudAPI.getInstance().updateCache();
        }
    }

    @Override
    public void updateObject(IGameServer object) {

        IGameServer cached = this.getCached(object.getName());
        if (cached == null) {
            this.registerGameServer(object);
            PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudGameServerStatusChangeEvent(object, object.getStatus()));
            return;
        }
        //System.out.println(object.getName() + " [" + object.getStatus() + "/" + cached.getStatus() + "]");
        if (!cached.getStatus().equals(object.getStatus())) {
            PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudGameServerStatusChangeEvent(object, object.getStatus()));
        }

        IGameServerManager.super.updateObject(object);

        if (PoloCloudAPI.getInstance().getType().isCloud()) {
            PoloCloudAPI.getInstance().updateCache();
        }
    }

    @Override
    public IGameServer getThisService() {
        if (PoloCloudAPI.getInstance().getType().isPlugin()) {
            try {
                JsonData jsonData = new JsonData(new File("PoloCloud.json"));
                return this.getCached(jsonData.getString("GameServer-Name"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @NotNull
    @Override
    public Iterator<IGameServer> iterator() {
        return this.cachedObjects.iterator();
    }
}
