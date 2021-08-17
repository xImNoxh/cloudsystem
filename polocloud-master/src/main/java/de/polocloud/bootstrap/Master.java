package de.polocloud.bootstrap;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.commands.SimpleCommandPool;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.commands.ICommandPool;
import de.polocloud.api.commands.types.ConsoleExecutor;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.event.IEventHandler;
import de.polocloud.api.event.channel.ChannelActiveEvent;
import de.polocloud.api.event.channel.ChannelInactiveEvent;
import de.polocloud.api.event.netty.NettyExceptionEvent;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.guice.PoloAPIGuiceModule;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.IStartable;
import de.polocloud.api.network.ITerminatable;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.server.SimpleNettyServer;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.pubsub.IPubSubManager;
import de.polocloud.api.pubsub.SimplePubSubManager;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.client.IWrapperClientManager;
import de.polocloud.bootstrap.client.SimpleWrapperClientManager;
import de.polocloud.bootstrap.commands.*;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.bootstrap.creator.ServerCreatorRunner;
import de.polocloud.bootstrap.gameserver.SimpleGameServerManager;
import de.polocloud.bootstrap.guice.MasterGuiceModule;
import de.polocloud.bootstrap.listener.ChannelActiveListener;
import de.polocloud.bootstrap.listener.ChannelInactiveListener;
import de.polocloud.bootstrap.listener.NettyExceptionListener;
import de.polocloud.bootstrap.module.MasterModuleLoader;
import de.polocloud.bootstrap.module.ModuleCache;
import de.polocloud.bootstrap.network.SimplePacketService;
import de.polocloud.bootstrap.network.ports.PortService;
import de.polocloud.bootstrap.player.SimpleCloudPlayerManager;
import de.polocloud.bootstrap.pubsub.PublishPacketHandler;
import de.polocloud.bootstrap.pubsub.SubscribePacketHandler;
import de.polocloud.bootstrap.template.SimpleTemplateService;
import de.polocloud.bootstrap.template.TemplateStorage;
import de.polocloud.bootstrap.template.fallback.FallbackProperty;
import de.polocloud.bootstrap.template.fallback.FallbackSearchService;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.io.File;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

public class Master extends PoloCloudAPI implements IStartable, ITerminatable {

    public static Master instance;
    private final Injector inector;

    private final ICommandPool commandPool;
    private final ITemplateService templateService;
    private final IWrapperClientManager wrapperClientManager;
    private final IGameServerManager gameServerManager;
    private final ICloudPlayerManager cloudPlayerManager;
    private final ICommandExecutor commandExecutor;

    private final SimpleConfigLoader simpleConfigLoader = new SimpleConfigLoader();
    private final SimpleConfigSaver simpleConfigSaver = new SimpleConfigSaver();

    private final FallbackSearchService fallbackSearchService;
    private final ModuleCache moduleCache;
    private final MasterModuleLoader moduleLoader;
    private final PortService portService;
    private IPubSubManager pubSubManager;

    private SimpleNettyServer nettyServer;
    private boolean running = false;

    private MasterConfig masterConfig;

    public Master() {

        instance = this;
        PoloCloudAPI.setInstance(this);

        this.commandPool = new SimpleCommandPool();
        this.commandExecutor = new ConsoleExecutor() {
            @Override
            public void sendMessage(String message) {
                Logger.log(LoggerType.INFO, message);
            }
        };
        this.wrapperClientManager = new SimpleWrapperClientManager();
        this.gameServerManager = new SimpleGameServerManager();
        this.templateService = new SimpleTemplateService();
        this.cloudPlayerManager = new SimpleCloudPlayerManager();
        this.portService = new PortService(gameServerManager);

        masterConfig = loadConfig();

        inector = Guice.createInjector(new PoloAPIGuiceModule(), new MasterGuiceModule(masterConfig, this, wrapperClientManager, this.gameServerManager, templateService, this.cloudPlayerManager));

        this.fallbackSearchService = new FallbackSearchService(masterConfig);
        this.moduleCache = new ModuleCache();
        this.moduleLoader = new MasterModuleLoader(moduleCache);


        ((SimpleTemplateService) this.templateService).load(this, TemplateStorage.FILE);
        this.templateService.getTemplateLoader().loadTemplates();

        EventRegistry.registerListener(new NettyExceptionListener(), NettyExceptionEvent.class);

        getCommandPool().registerCommand(new StopCommand());
        getCommandPool().registerCommand(new TemplateCommand(templateService, gameServerManager));

        getCommandPool().registerCommand(new ReloadCommand());
        getCommandPool().registerCommand(new HelpCommand());
        getCommandPool().registerCommand(new PlayerCommand(cloudPlayerManager, gameServerManager));

        getCommandPool().registerCommand(getGuice().getInstance(GameServerCommand.class));
        getCommandPool().registerCommand(getGuice().getInstance(WrapperCommand.class));

        Thread runnerThread = new Thread(getGuice().getInstance(ServerCreatorRunner.class));
        this.moduleLoader.loadModules(false);
        runnerThread.start();
    }

    @Override
    public INetworkConnection getConnection() {
        return this.nettyServer;
    }

    @Override
    public PoloType getType() {
        return PoloType.MASTER;
    }

    public static Master getInstance() {
        return instance;
    }

    private MasterConfig loadConfig() {

        File configFile = new File("config.json");
        MasterConfig masterConfig = simpleConfigLoader.load(MasterConfig.class, configFile);

        //Sorting the Fallbacks after the FallbackPriority, to make it faster
        if (!masterConfig.getProperties().getFallbackProperties().isEmpty()) {
            masterConfig.getProperties().getFallbackProperties().sort(Comparator.comparingInt(FallbackProperty::getPriority));
        } else {
            Logger.log(LoggerType.WARNING, "No fallbacks are registered in config.json! The Cloud don't find any fallbacks, so you can't join!");
            Logger.log(LoggerType.INFO, "Adding a default Lobby fallback!");
            masterConfig.getProperties().getFallbackProperties().add(new FallbackProperty("Lobby", "", true, 1));
        }
        simpleConfigSaver.save(masterConfig, configFile);
        return masterConfig;
    }

    @Override
    public void start() {
        running = true;
        Logger.log(LoggerType.INFO, "Trying to start master...");

        this.nettyServer = getGuice().getInstance(SimpleNettyServer.class);
        getGuice().getInstance(SimplePacketService.class);

        this.nettyServer.getProtocol().registerPacketHandler(getGuice().getInstance(PublishPacketHandler.class));
        this.nettyServer.getProtocol().registerPacketHandler(getGuice().getInstance(SubscribePacketHandler.class));

        //events
        EventRegistry.registerListener(getGuice().getInstance(ChannelInactiveListener.class), ChannelInactiveEvent.class);
        EventRegistry.registerListener(getGuice().getInstance(ChannelActiveListener.class), ChannelActiveEvent.class);

        this.pubSubManager = new SimplePubSubManager(nettyServer);
        new Thread(() -> nettyServer.start()).start();

        try {
            if (this.templateService.getLoadedTemplates().get().size() > 0) {
                StringBuilder builder = new StringBuilder();
                this.templateService.getLoadedTemplates().get().forEach(key -> builder.append(key.getName()).append("(" + key.getServerCreateThreshold() + "%),"));
                Logger.log(LoggerType.INFO, "Found templates: " + ConsoleColors.LIGHT_BLUE + builder.substring(0, builder.length() - 1));
            } else {
                Logger.log(LoggerType.INFO, "No templates founded.");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Logger.log(LoggerType.INFO, "The master is " + ConsoleColors.GREEN + "successfully" + ConsoleColors.GRAY + " started.");
    }

    @Override
    public boolean terminate() {
        this.running = false;
        boolean terminate = this.nettyServer.terminate();

        try {
            for (IGameServer gameServer : getGameServerManager().getGameServers().get()) {
                gameServer.terminate();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        Scheduler.runtimeScheduler().schedule(() -> {
            Logger.log(LoggerType.INFO, "All " + ConsoleColors.CYAN + "GameServers " + ConsoleColors.RESET + "are now " + ConsoleColors.RED + "stopped" + ConsoleColors.RESET + "!");
            Logger.log(LoggerType.INFO, "Shutting down Master...");
            System.exit(0);
        }, () -> {
            try {
                return PoloCloudAPI.getInstance().getGameServerManager().getGameServers().get().isEmpty();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return false;
        });
        return terminate;
    }

    @Override
    public ICommandPool getCommandPool() {
        return commandPool;
    }

    @Override
    public Injector getGuice() {
        return inector;
    }

    @Override
    public IConfigLoader getConfigLoader() {
        return simpleConfigLoader;
    }

    @Override
    public IConfigSaver getConfigSaver() {
        return simpleConfigSaver;
    }

    @Override
    public IPubSubManager getPubSubManager() {
        return pubSubManager;
    }

    @Override
    public IProtocol getCloudProtocol() {
        return null;
    }

    @Override
    public IEventHandler getEventHandler() {
        return null;
    }

    @Override
    public ITemplateService getTemplateService() {
        return templateService;
    }

    public MasterConfig getMasterConfig() {
        return masterConfig;
    }

    @Override
    public ICommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    @Override
    public ICloudPlayerManager getCloudPlayerManager() {
        return cloudPlayerManager;
    }

    @Override
    public IGameServerManager getGameServerManager() {
        return gameServerManager;
    }

    public SimpleNettyServer getNettyServer() {
        return nettyServer;
    }

    public IWrapperClientManager getWrapperClientManager() {
        return wrapperClientManager;
    }

    public MasterModuleLoader getModuleLoader() {
        return moduleLoader;
    }

    public boolean isRunning() {
        return running;
    }

    public ModuleCache getModuleCache() {
        return moduleCache;
    }

    public FallbackSearchService getFallbackSearchService() {
        return fallbackSearchService;
    }

    public PortService getPortService() {
        return portService;
    }
}
