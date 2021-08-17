package de.polocloud.api;

import com.google.inject.Injector;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.commands.ICommandPool;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.event.IEventHandler;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.pubsub.IPubSubManager;
import de.polocloud.api.template.ITemplateService;
import org.jetbrains.annotations.NotNull;

public abstract class PoloCloudAPI {

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
     * Sets the {@link PoloCloudAPI} instance
     *
     * @param instance the instance
     */
    protected static void setInstance(@NotNull PoloCloudAPI instance) {
        PoloCloudAPI.instance = instance;
    }

    /**
     * The {@link PoloType} of this cloud instance
     * To identify this process as Master/Wrapper or Spigot/Proxy
     */
    public abstract PoloType getType();

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
     * The current {@link ICommandExecutor} (e.g. console)
     */
    public abstract ICommandExecutor getCommandExecutor();

    /**
     * The current {@link ICommandPool} instance
     * to register, unregister and handle {@link de.polocloud.api.commands.CloudCommand}s
     */
    public abstract ICommandPool getCommandPool();

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
     * The current {@link IEventHandler} instance
     */
    public abstract IEventHandler getEventHandler();

    /**
     * The current {@link Injector} instance
     * (Google {@link com.google.inject.Guice})
     */
    public abstract Injector getGuice();

}
