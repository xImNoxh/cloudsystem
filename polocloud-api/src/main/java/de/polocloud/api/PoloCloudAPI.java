package de.polocloud.api;

import de.polocloud.api.bridge.PoloPluginBridge;
import de.polocloud.api.command.ICommandManager;
import de.polocloud.api.command.SimpleCommandManager;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.inject.Injector;
import de.polocloud.api.network.protocol.packet.handler.ConsumingPacketHandler;
import de.polocloud.api.util.session.ISession;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.util.session.SimpleSession;
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

import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.base.SimpleGameServer;
import de.polocloud.api.inject.base.SimpleInjector;
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
import de.polocloud.api.network.packets.wrapper.WrapperUpdatePacket;
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
import de.polocloud.api.template.SimpleTemplate;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.common.AutoRegistry;
import de.polocloud.api.util.other.Snowflake;
import de.polocloud.api.util.system.SystemManager;
import de.polocloud.api.util.system.resources.IResourceConverter;
import de.polocloud.api.util.system.resources.IResourceProvider;
import de.polocloud.api.uuid.IUUIDFetcher;
import de.polocloud.api.uuid.SimpleUUIDFetcher;
import de.polocloud.api.wrapper.IWrapperManager;
import de.polocloud.api.wrapper.SimpleCachedWrapperManager;
import de.polocloud.api.wrapper.base.IWrapper;
import io.netty.channel.ChannelHandlerContext;
import jline.console.ConsoleReader;
import lombok.Data;
import lombok.Getter;
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
@Data
public abstract class PoloCloudAPI implements IPacketReceiver, ITerminatable {

    //The instance for this api
    @Getter private static PoloCloudAPI instance;

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
    protected Injector injector;
    protected final SystemManager systemManager;

    //The config managers
    protected final IConfigLoader configLoader;
    protected final IConfigSaver configSaver;

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
        this.injector = new SimpleInjector();

        this.configLoader = new SimpleConfigLoader();
        this.configSaver = new SimpleConfigSaver();

        this.masterConfig = new MasterConfig();
        
        injector.bind(Scheduler.class).toInstance(new SimpleScheduler());
        injector.bind(Snowflake.class).toInstance(new Snowflake());

        injector.bind(IConfigLoader.class).toInstance(new SimpleConfigLoader());
        injector.bind(IConfigSaver.class).toInstance(new SimpleConfigSaver());
        
        injector.bind(ITemplate.class).toInstance(new SimpleTemplate());
        injector.bind(IGameServer.class).toInstance(new SimpleGameServer());

        injector.bind(ISession.class).toInstance(new SimpleSession());

        this.registerPacketHandler(new IPacketHandler<WrapperUpdatePacket>() {

            @Override
            public void handlePacket(ChannelHandlerContext ctx, WrapperUpdatePacket packet) {
                IWrapper wrapper = packet.getWrapper();
                wrapperManager.updateWrapper(wrapper);
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return WrapperUpdatePacket.class;
            }
        });
    }

    public void checkRequirements(){
        //Checks the requirements from PoloCloud
        IResourceConverter resourceConverter = getSystemManager().getResourceConverter();
        IResourceProvider resourceProvider = getSystemManager().getResourceProvider();
        if(resourceProvider.getSystemPhysicalMemory() < 2048){
            PoloLogger.print(LogLevel.WARNING, "[ResourceWatcher] This system seems to have less than the minimum requirements of memory (Minimal: 2GB, you have " + resourceConverter.convertLongToSize(resourceProvider.getSystemPhysicalMemory()) + ")");
        }
        if(resourceProvider.getProcessVirtualMemory() < 2048){
            PoloLogger.print(LogLevel.WARNING, "[ResourceWatcher] This process seems to have less than the minimum requirements of memory (Minimal: 2GB, you have " + resourceConverter.convertLongToSize(resourceProvider.getSystemPhysicalMemory()) + ")");
        }
        if(resourceProvider.getSystemProcessors() < 2){
            PoloLogger.print(LogLevel.WARNING, "[ResourceWatcher] This system seems to have less than the minimum requirements of CPU-cores (Minimal: 2, you have " + resourceConverter.convertLongToSize(resourceProvider.getSystemProcessors()) + ")");
        }

        //Checks the user
        String userName = getSystemManager().getSystemUserName();
        if(userName.equalsIgnoreCase("admin") || userName.equalsIgnoreCase("administrator") || userName.equalsIgnoreCase("root")){
            PoloLogger.print(LogLevel.WARNING, "[SystemWatcher] It seems that you running this Instance on a Account with root (administrative) permissions. This isn't very secure.");
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
     * Sends a message to the CloudMaster
     * If this instance is plugin it will send a packet to the master
     * otherwise it will just print the message
     *
     * @param message the message to send
     */
    public abstract void messageCloud(String message);

    /**
     * Sets the cache for this api instance
     * and sets all values of the cache for the given managers
     *
     * @param cache the cache object
     */
    public void updateCache(MasterCache cache) {
        this.cloudPlayerManager.setCached(cache.getCloudPlayers());
        this.gameServerManager.setCached(cache.getGameServers());
        this.templateManager.setCachedObjects(cache.getTemplates());
        this.wrapperManager.setCachedObjects(cache.getWrappers());
        this.fallbackManager.setAvailableFallbacks(cache.getFallbacks());
        this.masterConfig = cache.getMasterConfig();
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
        INetworkConnection connection = this.getConnection();
        if (connection == null) {
            Scheduler.runtimeScheduler().schedule(() -> sendPacket(packet), () ->getConnection() != null);
            return;
        }
        connection.sendPacket(packet);
    }

    /**
     * Registers an {@link IPacketHandler} for a given Packet
     * in this {@link IProtocol}-Instance
     *
     * @param packetHandler the handler to register
     */
    public void registerPacketHandler(IPacketHandler<? extends Packet> packetHandler) {
        INetworkConnection connection = this.getConnection();
        if (connection == null) {
            Scheduler.runtimeScheduler().schedule(() -> registerPacketHandler(packetHandler), () -> getConnection() != null);
            return;
        }
        connection.getProtocol().registerPacketHandler(packetHandler);
    }

    /**
     * Registers a simple forwarding {@link IPacketHandler}
     * for not having the need to create an extra class to handle a single {@link Packet}
     *
     * @param packetClass the class of the packet to listen for
     * @param handler the handler to handle the packet
     * @param <T> the generic (has to be an instance of a {@link Packet})
     */
    public <T extends Packet> void registerSimplePacketHandler(Class<T> packetClass, Consumer<T> handler) {
        this.registerPacketHandler(new ConsumingPacketHandler<>(packetClass, handler));
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
            PoloLogger.print(LogLevel.ERROR, "An exception was caught while registering the packets. The cloud may not react to a packet.");
            PoloCloudAPI.getInstance().reportException(e);
        }
    }

}
