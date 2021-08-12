package de.polocloud.plugin;

import com.google.inject.Injector;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.commands.ICommandPool;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.event.IEventHandler;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.IProtocol;
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
import de.polocloud.plugin.commands.CommandReader;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.property.GameServerProperty;

public class CloudPlugin extends PoloCloudAPI {

    private static CloudPlugin instance;

    private SimpleGameServer gameServer;

    private IBootstrap bootstrap;

    private NetworkClient networkClient;
    private final GameServerProperty gameServerProperty;
    private final CommandReader commandReader;
    private final IProtocol iProtocol;
    private final IPubSubManager pubSubManager;

    private ICloudPlayerManager cloudPlayerManager;
    private IGameServerManager gameServerManager;
    private ITemplateService templateService;

    private final IConfigLoader configLoader = new SimpleConfigLoader();
    private final IConfigSaver configSaver = new SimpleConfigSaver();

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

        this.pubSubManager = new SimplePubSubManager(networkClient, iProtocol);

        this.networkClient.connect(bootstrap.getPort());


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void onEnable() {
        bootstrap.registerListeners();

        gameServer.setStatus(GameServerStatus.RUNNING);
        networkClient.sendPacket(new GameServerSuccessfullyStartedPacket(gameServer.getName(), gameServer.getSnowflake()));
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

    public static CloudPlugin getCloudPluginInstance() {
        return instance;
    }

    @Override
    public ITemplateService getTemplateService() {
        return this.templateService;
    }

    @Override
    public ICommandExecutor getCommandExecutor() {
        return null;
    }

    @Override
    public ICommandPool getCommandPool() {
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
