package de.polocloud.api;

import com.google.inject.Injector;
import de.polocloud.api.command.ICommandManager;
import de.polocloud.api.command.SimpleCommandManager;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.event.IEventManager;
import de.polocloud.api.event.SimpleCachedEventManager;
import de.polocloud.api.event.base.IEvent;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.placeholder.IPlaceHolderManager;
import de.polocloud.api.placeholder.SimpleCachedPlaceHolderManager;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.pubsub.IPubSubManager;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.api.wrapper.IWrapperManager;
import de.polocloud.api.wrapper.SimpleCachedWrapperManager;

import java.util.Optional;

public abstract class PoloCloudAPI {

    //Other fields
    private final PoloType type;

    //All manager instances
    protected final ICommandManager commandManager;
    protected final IPlaceHolderManager placeHolderManager;
    protected final IWrapperManager wrapperManager;
    protected final IEventManager eventManager;

    protected PoloCloudAPI(PoloType type) {
        instance = this;

        this.type = type;

        this.eventManager = new SimpleCachedEventManager();
        this.commandManager = new SimpleCommandManager();
        this.placeHolderManager = new SimpleCachedPlaceHolderManager();
        this.wrapperManager = new SimpleCachedWrapperManager();
    }

    /**
     * The current {@link INetworkConnection} to manage
     * all the networking stuff like sending packets
     * and registering packet handlers or
     * creating and sending requests and responses
     */
    public abstract INetworkConnection getConnection();

    /**
     * The current {@link ITemplateService} to manage
     * all the cached {@link de.polocloud.api.template.ITemplate}s
     */
    public abstract ITemplateService getTemplateService();

    /**
     * The current {@link CommandExecutor} (e.g. console)
     */
    public abstract CommandExecutor getCommandExecutor();

    /**
     * The current {@link IGameServerManager} instance
     * to manage all {@link de.polocloud.api.gameserver.IGameServer}s
     */
    public abstract IGameServerManager getGameServerManager();

    /**
     * The current {@link ICloudPlayerManager} instance
     * to manage all {@link de.polocloud.api.player.ICloudPlayer}s
     */
    public abstract ICloudPlayerManager getCloudPlayerManager();

    /**
     * The current {@link IConfigLoader} instance to load {@link de.polocloud.api.config.IConfig}s
     */
    public abstract IConfigLoader getConfigLoader();

    /**
     * The current {@link IConfigSaver} instance to save {@link de.polocloud.api.config.IConfig}s
     */
    public abstract IConfigSaver getConfigSaver();

    /**
     * The current {@link IPubSubManager} to manage all
     * data-flows and register channel-handlers or send data yourself
     */
    public abstract IPubSubManager getPubSubManager();

    /**
     * The current {@link IProtocol} of the cloud
     */
    public abstract IProtocol getCloudProtocol();

    /**
     * The current {@link Injector} instance
     * (Google {@link com.google.inject.Guice})
     */
    public abstract Injector getGuice();

    //========================================

    // Class provided field getters and setters

    //========================================


    /**
     * The instance of the cloud api
     */
    private static PoloCloudAPI instance;

    /**
     * Gets the current {@link PoloCloudAPI}
     */
    public static PoloCloudAPI getInstance() {
        return instance;
    }

    /**
     * Returns an optional instance to maybe check
     * if the instance is present or something similar
     */
    public static Optional<PoloCloudAPI> optionalInstance() {
        return Optional.ofNullable(instance);
    }

    public PoloType getType() {
        return type;
    }

    public IEventManager getEventManager() {
        return eventManager;
    }

    public ICommandManager getCommandManager() {
        return commandManager;
    }

    public IPlaceHolderManager getPlaceHolderManager() {
        return placeHolderManager;
    }

    public IWrapperManager getWrapperManager() {
        return wrapperManager;
    }
}
