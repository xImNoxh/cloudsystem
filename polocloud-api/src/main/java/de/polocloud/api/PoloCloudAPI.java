package de.polocloud.api;

import com.google.inject.Injector;
import de.polocloud.api.bridge.PoloPluginBridge;
import de.polocloud.api.command.ICommandManager;
import de.polocloud.api.command.SimpleCommandManager;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.FileConstants;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.master.MasterConfig;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.event.IEventManager;
import de.polocloud.api.event.SimpleCachedEventManager;
import de.polocloud.api.fallback.IFallbackManager;
import de.polocloud.api.fallback.SimpleCachedFallbackManager;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.gameserver.SimpleCachedGameServerManager;

import de.polocloud.api.guice.own.Guice;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.PoloLoggerFactory;
import de.polocloud.api.logger.def.SimplePoloLoggerFactory;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.messaging.IMessageManager;
import de.polocloud.api.messaging.def.SimpleCachedMessageManager;
import de.polocloud.api.module.IModuleHolder;
import de.polocloud.api.module.loader.ModuleService;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.helper.ITerminatable;
import de.polocloud.api.network.packets.api.MasterCache;
import de.polocloud.api.network.packets.other.TextPacket;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.packet.IPacketReceiver;
import de.polocloud.api.network.protocol.packet.PacketFactory;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.api.placeholder.IPlaceHolderManager;
import de.polocloud.api.placeholder.SimpleCachedPlaceHolderManager;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.player.def.SimpleCachedCloudPlayerManager;
import de.polocloud.api.property.IPropertyManager;
import de.polocloud.api.property.def.SimpleCachedPropertyManager;
import de.polocloud.api.pubsub.IPubSubManager;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.scheduler.base.SimpleScheduler;
import de.polocloud.api.template.ITemplateManager;
import de.polocloud.api.template.SimpleCachedTemplateManager;
import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.util.Snowflake;
import de.polocloud.api.util.system.SystemManager;
import de.polocloud.api.uuid.IUUIDFetcher;
import de.polocloud.api.uuid.SimpleUUIDFetcher;
import de.polocloud.api.wrapper.IWrapperManager;
import de.polocloud.api.wrapper.SimpleCachedWrapperManager;
import io.netty.channel.ChannelHandlerContext;
import jline.console.ConsoleReader;
import org.reflections.Reflections;

import java.util.Optional;
import java.util.function.Consumer;

@APIVersion(
    version = "alpha-1.0",
    developers = {
        "iPommes",
        "Max_DE",
        "HttpMarco",
        "Lystx"
    },
    identifier = "@Alpha",
    discord = "https://discord.gg/HyRnsdkUBA"
)
public abstract class PoloCloudAPI implements IPacketReceiver, ITerminatable {

    //The instance for this api
    private static PoloCloudAPI instance;

    //All manager instances
    protected final ICommandManager commandManager;
    protected final IPlaceHolderManager placeHolderManager;
    protected final IWrapperManager wrapperManager;
    protected final IEventManager eventManager;
    protected final IFallbackManager fallbackManager;
    protected final IGameServerManager gameServerManager;
    protected final ITemplateManager templateManager;
    protected final ICloudPlayerManager cloudPlayerManager;
    protected final PoloLoggerFactory loggerFactory;
    protected final IMessageManager messageManager;
    protected final IModuleHolder moduleHolder;
    protected final IPropertyManager propertyManager;
    protected final IUUIDFetcher uuidFetcher;
    protected final SystemManager systemManager;

    //The bridge instance
    protected PoloPluginBridge poloBridge;

    private final PoloType type;
    protected MasterConfig masterConfig;

    protected PoloCloudAPI(PoloType type) {
        instance = this;

        this.type = type;

        this.registerPackets();

        this.loggerFactory = new SimplePoloLoggerFactory(FileConstants.POLO_LOGS_FOLDER);
        this.eventManager = new SimpleCachedEventManager();
        this.commandManager = new SimpleCommandManager();
        this.placeHolderManager = new SimpleCachedPlaceHolderManager();
        this.wrapperManager = new SimpleCachedWrapperManager();
        this.fallbackManager = new SimpleCachedFallbackManager();
        this.gameServerManager = new SimpleCachedGameServerManager();
        this.cloudPlayerManager = new SimpleCachedCloudPlayerManager();
        this.templateManager = new SimpleCachedTemplateManager();
        this.messageManager = new SimpleCachedMessageManager();
        this.moduleHolder = new ModuleService(FileConstants.MASTER_MODULES);
        this.propertyManager = new SimpleCachedPropertyManager();
        this.systemManager = new SystemManager();
        this.uuidFetcher = new SimpleUUIDFetcher(1);
        this.masterConfig = new MasterConfig();

        Guice.bind(Scheduler.class).toInstance(new SimpleScheduler());
        Guice.bind(Snowflake.class).toInstance(new Snowflake());

        Guice.bind(IConfigLoader.class).toInstance(new SimpleConfigLoader());
        Guice.bind(IConfigSaver.class).toInstance(new SimpleConfigSaver());
    }

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

    /**
     * Sends a message to the CloudMaster
     * If this instance is plugin it will send a packet to the master
     * otherwise it will just print the message
     *
     * @param message the message to send
     */
    public void messageCloud(String message) {
        if (type != PoloType.MASTER) {
            sendPacket(new TextPacket(message));
        } else {
            PoloLogger.print(LogLevel.INFO, message);
        }
    }

    /**
     * The {@link ConsoleReader} instance for console managing
     */
    public abstract ConsoleReader getConsoleReader();

    /**
     * Reports an Exception to the server and automatically
     * prints it, so you can see it locally and the admins
     * on the server
     *
     * @param throwable the error
     */
    public abstract void reportException(Throwable throwable);


    /**
     * Sets the cache for this api instance
     * and sets all values of the cache for the given managers
     *
     * @param cache the cache object
     */
    public void setCache(MasterCache cache) {
        this.cloudPlayerManager.setCached(cache.getCloudPlayers());
        this.gameServerManager.setCached(cache.getGameServers());
        this.templateManager.setCachedObjects(cache.getTemplates());
        this.wrapperManager.setCachedObjects(cache.getWrappers());
        this.fallbackManager.setAvailableFallbacks(cache.getFallbacks());
        this.masterConfig = cache.getMasterConfig();
    }

    /**
     * Gets the {@link MasterConfig} to get Messages
     * or properties of it in general
     *
     * @return config instance
     */
    public MasterConfig getMasterConfig() {
        return this.masterConfig;
    }

    /**
     * Terminates this {@link PoloCloudAPI} instance
     * and shuts down all needed manager instances
     *
     * @return true if it was successfully terminated
     */
    @Override
    public abstract boolean terminate();

    /**
     * Updates the cache of the current instance
     * <p>
     * If....
     * <p>
     * 'MASTER' -> Sends cache to all servers
     * 'PLUGIN' -> Requests cache from master
     */
    public abstract void updateCache();

    /**
     * Reloads this instance
     */
    public abstract void reload();

    /**
     * The current {@link INetworkConnection} to manage
     * all the networking stuff like sending packets
     * and registering packet handlers or
     * creating and sending requests and responses
     */
    public abstract INetworkConnection getConnection();

    //========================================

    // Class provided field getters and setters

    //========================================

    /**
     * The current {@link CommandExecutor} (e.g. console)
     */
    public abstract CommandExecutor getCommandExecutor();

    /**
     * The current {@link IPubSubManager} to manage all
     * data-flows and register channel-handlers or send data yourself
     */
    public abstract IPubSubManager getPubSubManager();

    /**
     * The current {@link Injector} instance
     * (Google {@link com.google.inject.Guice})
     */
    public abstract Injector getGuice();

    /**
     * Sends a {@link Packet} to the other connection side
     * <p>
     * If this instance is...
     * <p>
     * 'CLOUD/SERVER' -> Will send to all connected clients
     * 'BRIDGE/SPIGOT/PROXY' -> WIll send to the Master
     *
     * @param packet the packet to send
     */
    public void sendPacket(Packet packet) {
        this.getConnection().sendPacket(packet);
    }

    /**
     * Registers an {@link IPacketHandler} for a given Packet
     * in this {@link IProtocol}-Instance
     *
     * @param packetHandler the handler to register
     */
    public void registerPacketHandler(IPacketHandler<? extends Packet> packetHandler) {
        getConnection().getProtocol().registerPacketHandler(packetHandler);
    }

    public <T extends Packet> void registerSimplePacketHandler(Class<T> packetClass, Consumer<T> handler) {
        this.registerPacketHandler(new IPacketHandler<T>() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, T packet) {
                handler.accept(packet);
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return packetClass;
            }
        });
    }

    public PoloType getType() {
        return type;
    }

    public PoloLoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    public IEventManager getEventManager() {
        return eventManager;
    }

    public ICloudPlayerManager getCloudPlayerManager() {
        return cloudPlayerManager;
    }

    public IUUIDFetcher getUuidFetcher() {
        return uuidFetcher;
    }

    public IPropertyManager getPropertyManager() {
        return propertyManager;
    }

    public ICommandManager getCommandManager() {
        return commandManager;
    }

    public IPlaceHolderManager getPlaceHolderManager() {
        return placeHolderManager;
    }

    public ITemplateManager getTemplateManager() {
        return templateManager;
    }

    public IGameServerManager getGameServerManager() {
        return gameServerManager;
    }

    public IFallbackManager getFallbackManager() {
        return fallbackManager;
    }

    public IWrapperManager getWrapperManager() {
        return wrapperManager;
    }

    public IMessageManager getMessageManager() {
        return messageManager;
    }

    public IModuleHolder getModuleHolder() {
        return moduleHolder;
    }

    public SystemManager getSystemManager() {
        return systemManager;
    }

    public PoloPluginBridge getPoloBridge() {
        return poloBridge;
    }

    public void setPoloBridge(PoloPluginBridge minecraftBridge) {
        this.poloBridge = minecraftBridge;
    }

    public void setMasterConfig(MasterConfig masterConfig) {
        this.masterConfig = masterConfig;
    }

    /**
     * Gets an {@link APIVersion} instance of this api
     */
    public APIVersion getVersion() {
        return PoloCloudAPI.class.isAnnotationPresent(APIVersion.class) ? PoloCloudAPI.class.getAnnotation(APIVersion.class) : null;
    }

    /**
     * Auto registers all packets
     */
    private void registerPackets() {
        try {
            int autoId = 0;
            Reflections reflections = new Reflections(INetworkConnection.class.getPackage().getName());

            for (Class<? extends Packet> cls : reflections.getSubTypesOf(Packet.class)) {
                AutoRegistry annotation = cls.getAnnotation(AutoRegistry.class);
                if (annotation != null) {
                    PacketFactory.registerPacket(autoId, cls);
                    autoId++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
