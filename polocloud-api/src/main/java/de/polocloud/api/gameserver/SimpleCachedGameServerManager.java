package de.polocloud.api.gameserver;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.FileConstants;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.event.impl.server.CloudGameServerStatusChangeEvent;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.wrapper.base.IWrapper;
import de.polocloud.api.wrapper.ex.NoWrapperFoundException;
import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class SimpleCachedGameServerManager implements IGameServerManager {

    private List<IGameServer> cachedObjects;

    public SimpleCachedGameServerManager() {
        this.cachedObjects = new ArrayList<>();
    }

    @Override
    public void setCached(List<IGameServer> cachedObjects) {
        this.cachedObjects = cachedObjects;
    }

    @Override
    public List<IGameServer> getAllCached() {
        return cachedObjects;
    }

    @Override
    public IGameServer getCached(ChannelHandlerContext ctx) {
        return this.cachedObjects.stream().filter(iGameServer -> iGameServer.ctx().equals(ctx)).findFirst().orElse(null);
    }

    @Override
    public void startServer(ITemplate template, int count) throws NoWrapperFoundException {

        List<IGameServer> gameServersByTemplate = getCached(template);
        for (int i = 0; i < count; i++) {
            Optional<IWrapper> optionalWrapperClient = PoloCloudAPI.getInstance().getWrapperManager().getWrappers().stream().findAny();

            if (!optionalWrapperClient.isPresent()) {
                throw new NoWrapperFoundException();
            }

            IWrapper wrapperClient = optionalWrapperClient.get();

            IGameServer iGameServer = IGameServer.create();
            iGameServer.applyTemplate(template); //Memory, motd, maxplayers
            iGameServer.setName(template.getName() + "-" + (gameServersByTemplate.size() + (i + 1)));
            iGameServer.newSnowflake();
            iGameServer.setStartedTime(System.currentTimeMillis());
            iGameServer.setPort(PoloCloudAPI.getInstance().getPortManager().getPort(template));
            iGameServer.setVisible(false);

            wrapperClient.startServer(iGameServer);
        }
    }

    @Override
    public void stopServer(IGameServer gameServer) throws NoWrapperFoundException {
        IWrapper wrapper = gameServer.getWrapper();
        if (wrapper == null) {
            throw new NoWrapperFoundException();
        }
        wrapper.stopServer(gameServer);
    }

    @Override
    public void stopServers(ITemplate template) throws NoWrapperFoundException{
        for (IGameServer server : new ArrayList<>(getCached(template))) {
            stopServer(server);
        }
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
        if (gameServer == null) {
            return;
        }

        gameServer.setStatus(GameServerStatus.STOPPING);
        gameServer.updateInternally();
        cachedObjects.removeIf(gameServer1 -> gameServer1.getName().equalsIgnoreCase(gameServer.getName()));

        if (PoloCloudAPI.getInstance().getType().isCloud()) {
            PoloCloudAPI.getInstance().updateCache();
        }
    }

    @Override
    public void updateObject(IGameServer object) {

        IGameServer cachedGameServer = this.getCached(object.getName());

        //Server is not cached yet.... registering
        if (cachedGameServer == null) {
            this.registerGameServer(object);
            return;
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
                JsonData jsonData = new JsonData(new File(FileConstants.CLOUD_JSON_NAME));
                return this.getCached(jsonData.getString("GameServer-Name"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
