package de.polocloud.plugin;

import com.google.inject.Injector;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.packet.api.other.CacheRequestPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerSuccessfullyStartedPacket;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.pubsub.IPubSubManager;
import de.polocloud.api.pubsub.SimplePubSubManager;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.plugin.api.player.APICloudPlayerManager;
import de.polocloud.plugin.api.server.APIGameServerManager;
import de.polocloud.plugin.api.server.SimpleGameServer;
import de.polocloud.plugin.api.template.APITemplateManager;
import de.polocloud.plugin.bootstrap.IBootstrap;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.property.GameServerProperty;

public class CloudPlugin extends PoloCloudAPI {

    private final GameServerProperty gameServerProperty;
    private final IPubSubManager pubSubManager;
    private SimpleGameServer gameServer;
    private IBootstrap bootstrap;
    private NetworkClient networkClient;
    private ICloudPlayerManager cloudPlayerManager;
    private IGameServerManager gameServerManager;
    private ITemplateService templateService;


    public CloudPlugin(IBootstrap bootstrap) {
        super(bootstrap.getType());

        this.bootstrap = bootstrap;
        this.networkClient = new NetworkClient(bootstrap);
        this.gameServerProperty = new GameServerProperty();

        this.cloudPlayerManager = new APICloudPlayerManager();
        this.gameServerManager = new APIGameServerManager();
        this.templateService = new APITemplateManager();

        this.pubSubManager = new SimplePubSubManager(networkClient);

    }

    @Override
    public INetworkConnection getConnection() {
        return this.networkClient;
    }

    public static CloudPlugin getCloudPluginInstance() {
        return (CloudPlugin) PoloCloudAPI.getInstance();
    }

    public void onEnable() {
        this.networkClient.connect(bootstrap.getPort(), nettyClient -> {
            System.out.println("[CloudPlugin] " + gameServer.getName() + " successfully connected to CloudSystem! (" + nettyClient.getConnectedAddress() + ")");

            bootstrap.registerListeners();

            gameServer.setStatus(GameServerStatus.RUNNING);
            networkClient.sendPacket(new GameServerSuccessfullyStartedPacket(gameServer.getName(), gameServer.getSnowflake()));

        });
    }

    @Override
    public void updateCache() {
        sendPacket(new CacheRequestPacket());
    }

    public NetworkClient getNetworkClient() {
        return networkClient;
    }

    public IBootstrap getBootstrap() {
        return bootstrap;
    }

    public GameServerProperty getGameServerProperty() {
        return gameServerProperty;
    }

    public IGameServer thisService() {
        return gameServer;
    }

    public void setGameServer(SimpleGameServer gameServer) {
        this.gameServer = gameServer;
    }

    @Override
    public ITemplateService getTemplateService() {
        return this.templateService;
    }

    @Override
    public CommandExecutor getCommandExecutor() {
        return null;
    }

    @Override
    public IGameServerManager getGameServerManager() {
        return gameServerManager;
    }

    @Override
    public ICloudPlayerManager getCloudPlayerManager() {
        return cloudPlayerManager;
    }

    @Override
    public IPubSubManager getPubSubManager() {
        return pubSubManager;
    }


    @Override
    public Injector getGuice() {
        return null;
    }

}
