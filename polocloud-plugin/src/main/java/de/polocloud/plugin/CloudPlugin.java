package de.polocloud.plugin;

import com.google.inject.Injector;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.commands.ICommandPool;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.event.IEventHandler;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.pubsub.IPubSubManager;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.plugin.api.player.APICloudPlayerManager;
import de.polocloud.plugin.api.server.APIGameServerManager;
import de.polocloud.plugin.api.template.APITemplateManager;
import de.polocloud.plugin.function.BootstrapFunction;
import de.polocloud.plugin.function.NetworkRegisterFunction;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.property.GameServerProperty;

public class CloudPlugin extends PoloCloudAPI {

    private static CloudPlugin instance;

    private IPubSubManager pubSubManager;
    private IGameServerManager gameServerManager;
    private ICloudPlayerManager cloudPlayerManager;
    private ITemplateService templateService;
    private IProtocol protocol;

    private GameServerProperty property;

    private BootstrapFunction bootstrapFunction;
    private NetworkClient networkClient;

    public CloudPlugin(BootstrapFunction bootstrapFunction) {

        instance = this;

        this.bootstrapFunction = bootstrapFunction;
        this.property = new GameServerProperty();

        this.gameServerManager = new APIGameServerManager();
        this.cloudPlayerManager = new APICloudPlayerManager();
        this.templateService = new APITemplateManager();

        this.networkClient = new NetworkClient();
        this.networkClient.connect(bootstrapFunction.getNetworkPort());

    }

    public static CloudPlugin getInstance() {
        return instance;
    }

    public void callListeners(NetworkRegisterFunction networkRegisterFunction) {
        networkRegisterFunction.callNetwork(networkClient);
        bootstrapFunction.registerEvents(this);
    }

    @Override
    public ITemplateService getTemplateService() {
        return templateService;
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
        return null;
    }

    @Override
    public IConfigSaver getConfigSaver() {
        return null;
    }

    @Override
    public IPubSubManager getPubSubManager() {
        return this.pubSubManager;
    }

    @Override
    public IProtocol getCloudProtocol() {
        return this.protocol;
    }

    @Override
    public IEventHandler getEventHandler() {
        return null;
    }

    @Override
    public Injector getGuice() {
        return null;
    }

    public GameServerProperty getProperty() {
        return property;
    }

    public NetworkClient getNetworkClient() {
        return networkClient;
    }

    public BootstrapFunction getBootstrapFunction() {
        return bootstrapFunction;
    }

    public void setProtocol(IProtocol protocol) {
        this.protocol = protocol;
    }

    public void setPubSubManager(IPubSubManager pubSubManager) {
        this.pubSubManager = pubSubManager;
    }
}
