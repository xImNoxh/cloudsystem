package de.polocloud.plugin;

import com.google.inject.Injector;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.ICommandManager;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.SimpleCommandManager;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.event.IEventHandler;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerSuccessfullyStartedPacket;
import de.polocloud.api.network.request.base.component.PoloComponent;
import de.polocloud.api.network.request.base.future.PoloFuture;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.pubsub.IPubSubManager;
import de.polocloud.api.pubsub.SimplePubSubManager;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.wrapper.IWrapper;
import de.polocloud.api.wrapper.IWrapperManager;
import de.polocloud.api.wrapper.SimpleCachedWrapperManager;
import de.polocloud.plugin.api.player.APICloudPlayerManager;
import de.polocloud.plugin.api.server.APIGameServerManager;
import de.polocloud.plugin.api.server.SimpleGameServer;
import de.polocloud.plugin.api.template.APITemplateManager;
import de.polocloud.plugin.bootstrap.IBootstrap;
import de.polocloud.plugin.commands.CommandReader;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.property.GameServerProperty;

public class CloudPlugin extends PoloCloudAPI {

    private static CloudPlugin instance;
    private final GameServerProperty gameServerProperty;
    private final CommandReader commandReader;
    private final IProtocol iProtocol;
    private final IPubSubManager pubSubManager;
    private final IConfigLoader configLoader = new SimpleConfigLoader();
    private final IConfigSaver configSaver = new SimpleConfigSaver();
    private SimpleGameServer gameServer;
    private IBootstrap bootstrap;
    private NetworkClient networkClient;
    private ICloudPlayerManager cloudPlayerManager;
    private IGameServerManager gameServerManager;
    private ITemplateService templateService;
    private ICommandManager commandManager;
    private final IWrapperManager wrapperManager;

    private PoloType poloType;

    public CloudPlugin(IBootstrap bootstrap) {

        instance = this;
        setInstance(this);

        this.bootstrap = bootstrap;
        this.networkClient = new NetworkClient(bootstrap);
        this.gameServerProperty = new GameServerProperty();
        this.commandReader = new CommandReader();
        this.iProtocol = networkClient.getClient().getProtocol();

        this.cloudPlayerManager = new APICloudPlayerManager();
        this.gameServerManager = new APIGameServerManager();
        this.templateService = new APITemplateManager();
        this.commandManager = new SimpleCommandManager();
        this.wrapperManager = new SimpleCachedWrapperManager();
        this.poloType = bootstrap.getType();

        this.pubSubManager = new SimplePubSubManager(networkClient);

    }

    @Override
    public INetworkConnection getConnection() {
        return this.networkClient;
    }

    @Override
    public PoloType getType() {
        return this.poloType;
    }

    public static CloudPlugin getCloudPluginInstance() {
        return instance;
    }

    public void onEnable() {
        this.networkClient.connect(bootstrap.getPort(), nettyClient -> {
            System.out.println("[CloudPlugin] " + gameServer.getName() + " successfully connected to CloudSystem! (" + nettyClient.getConnectedAddress() + ")");

            bootstrap.registerListeners();

            gameServer.setStatus(GameServerStatus.RUNNING);
            networkClient.sendPacket(new GameServerSuccessfullyStartedPacket(gameServer.getName(), gameServer.getSnowflake()));

        });
    }

    public NetworkClient getNetworkClient() {
        return networkClient;
    }

    @Override
    public IWrapperManager getWrapperManager() {
        return wrapperManager;
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
    public ICommandManager getCommandManager() {
        return commandManager;
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
    public IConfigLoader getConfigLoader() {
        return configLoader;
    }

    @Override
    public IConfigSaver getConfigSaver() {
        return configSaver;
    }

    @Override
    public IPubSubManager getPubSubManager() {
        return pubSubManager;
    }

    @Override
    public IProtocol getCloudProtocol() {
        return iProtocol;
    }

    @Override
    public IEventHandler getEventHandler() {
        return null;
    }

    @Override
    public Injector getGuice() {
        return null;
    }

    public CommandReader getCommandReader() {
        return commandReader;
    }


}
