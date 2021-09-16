package de.polocloud.api.gameserver;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.FileConstants;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.config.master.properties.Properties;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.api.wrapper.base.IWrapper;
import de.polocloud.api.wrapper.ex.NoWrapperFoundException;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
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
    public IGameServer[] startServer(ITemplate template, int count) throws NoWrapperFoundException {

        List<IGameServer> gameServer = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Optional<IWrapper> optionalWrapperClient = PoloCloudAPI.getInstance().getWrapperManager().getWrappers().stream().findAny();

            if (!optionalWrapperClient.isPresent()) {
                throw new NoWrapperFoundException();
            }

            IWrapper wrapperClient = optionalWrapperClient.get();

            IGameServer iGameServer = IGameServer.newInstance();
            iGameServer.setTemplate(template); //Memory, motd, maxplayers

            //Setting identifiers
            iGameServer.newIdentification();

            wrapperClient.startServer(iGameServer);
            gameServer.add(iGameServer);
        }
        return gameServer.toArray(new IGameServer[0]);
    }

    @Override
    public int getFreePort(ITemplate template) {
        Properties properties = PoloCloudAPI.getInstance().getMasterConfig().getProperties();
        int port = template.getTemplateType() == TemplateType.PROXY ? properties.getDefaultProxyStartPort() : properties.getDefaultServerStartPort();

        IGameServer gameServer = this.getAllCached(template.getTemplateType()).stream().max(Comparator.comparingInt(IGameServer::getPort)).orElse(null);

        if (gameServer != null) {
            port = (gameServer.getPort() + 1);
        }

        return port;
    }

    @Override
    public int getFreeId(ITemplate template) {
        int id = 1;

        IGameServer gameServer = this.getAllCached(template).stream().max(Comparator.comparingInt(IGameServer::getId)).orElse(null);

        if (gameServer != null) {
            id = (gameServer.getId() + 1);
        }

        return id;
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
        for (IGameServer server : new ArrayList<>(getAllCached(template))) {
            stopServer(server);
        }
    }

    @Override
    public void register(IGameServer gameServer) {
        if (this.getCached(gameServer.getName()) == null) {
            this.cachedObjects.add(gameServer);

            if (PoloCloudAPI.getInstance().getType().isCloud()) {
                PoloCloudAPI.getInstance().updateCache();
            }
        }
    }

    @Override
    public void unregister(IGameServer gameServer) {
        if (gameServer == null) {
            return;
        }

        if (gameServer.getStatus() != GameServerStatus.STOPPING) {
            gameServer.setStatus(GameServerStatus.STOPPING);
            gameServer.updateInternally();
        }

        cachedObjects.removeIf(gameServer1 -> gameServer1.getName().equalsIgnoreCase(gameServer.getName()));

        if (PoloCloudAPI.getInstance().getType().isCloud()) {
            PoloCloudAPI.getInstance().updateCache();
        }
    }

    @Override
    public void update(IGameServer object) {

        IGameServer cachedGameServer = this.getCached(object.getName());

        //Server is not cached yet.... registering
        if (cachedGameServer == null) {
            this.register(object);
            return;
        }

        IGameServerManager.super.update(object);

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
