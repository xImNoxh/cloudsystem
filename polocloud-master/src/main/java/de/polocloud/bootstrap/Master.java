package de.polocloud.bootstrap;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.executor.SimpleConsoleExecutor;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.FileConstants;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.event.impl.net.ChannelActiveEvent;
import de.polocloud.api.event.impl.net.ChannelInactiveEvent;
import de.polocloud.api.event.impl.net.NettyExceptionEvent;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.guice.google.PoloAPIGuiceModule;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.module.loader.ModuleService;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.helper.IStartable;
import de.polocloud.api.network.packets.api.other.GlobalCachePacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.server.SimpleNettyServer;
import de.polocloud.api.pubsub.IPubSubManager;
import de.polocloud.api.pubsub.SimplePubSubManager;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.template.TemplateStorage;
import de.polocloud.api.util.PoloHelper;
import de.polocloud.bootstrap.commands.*;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.bootstrap.creator.ServerCreatorRunner;
import de.polocloud.bootstrap.guice.MasterGuiceModule;
import de.polocloud.bootstrap.listener.ChannelActiveListener;
import de.polocloud.bootstrap.listener.ChannelInactiveListener;
import de.polocloud.bootstrap.listener.NettyExceptionListener;
import de.polocloud.bootstrap.network.SimplePacketService;
import de.polocloud.bootstrap.network.ports.PortService;
import de.polocloud.bootstrap.pubsub.PublishPacketHandler;
import de.polocloud.bootstrap.pubsub.SubscribePacketHandler;
import de.polocloud.api.fallback.base.SimpleFallback;
import de.polocloud.bootstrap.fallback.FallbackSearchService;
import de.polocloud.client.PoloCloudClient;
import de.polocloud.logger.log.types.ConsoleColors;

import java.io.File;
import java.util.Comparator;
import java.util.LinkedList;

public class Master extends PoloCloudAPI implements IStartable {

    private final Injector injector;

    private final CommandExecutor commandExecutor;

    private final SimpleConfigLoader simpleConfigLoader = new SimpleConfigLoader();
    private final SimpleConfigSaver simpleConfigSaver = new SimpleConfigSaver();

    private final FallbackSearchService fallbackSearchService;
    private final ModuleService moduleService;
    private final PortService portService;
    private IPubSubManager pubSubManager;

    private SimpleNettyServer nettyServer;
    private boolean running = false;

    private final MasterConfig masterConfig;

    private final PoloCloudClient client;

    private String currentVersion = "N/A";

    public Master() {
        super(PoloType.MASTER);

        this.commandExecutor = new SimpleConsoleExecutor();

        this.client = new PoloCloudClient("37.114.60.98", 4542);
        this.registerUncaughtExceptionListener();

        this.portService = new PortService(gameServerManager);
        this.loggerFactory.setGlobalPrefix(PoloHelper.CONSOLE_PREFIX);

        this.masterConfig = this.loadConfig();

        this.portManager.setProxyPort(masterConfig.getProperties().getDefaultProxyStartPort());
        this.portManager.setServerPort(masterConfig.getProperties().getDefaultServerStartPort());

        this.injector = Guice.createInjector(new PoloAPIGuiceModule(), new MasterGuiceModule(masterConfig, this, this.gameServerManager, templateManager, this.cloudPlayerManager));

        this.templateManager.loadTemplates(TemplateStorage.FILE);

        this.fallbackSearchService = new FallbackSearchService(masterConfig);
        this.moduleService = new ModuleService(FileConstants.MASTER_MODULES);

        this.templateManager.getTemplateLoader().loadTemplates();

        //Event handler registering
        this.eventManager.registerHandler(NettyExceptionEvent.class, new NettyExceptionListener());

        //Registering all commands
        this.commandManager.registerCommand(new StopCommand());
        this.commandManager.registerCommand(new TemplateCommand(templateManager, gameServerManager));
        this.commandManager.registerCommand(new ReloadCommand());
        this.commandManager.registerCommand(new HelpCommand());
        this.commandManager.registerCommand(new PlayerCommand(cloudPlayerManager, gameServerManager));
        this.commandManager.registerCommand(new ChangelogCommand());
        this.commandManager.registerCommand(injector.getInstance(GameServerCommand.class));
        this.commandManager.registerCommand(injector.getInstance(WrapperCommand.class));


        //Server-Runner thread starting
        new Thread(getGuice().getInstance(ServerCreatorRunner.class)).start();
    }

    @Override
    public INetworkConnection getConnection() {
        return this.nettyServer;
    }

    public static Master getInstance() {
        return (Master) PoloCloudAPI.getInstance();
    }

    private MasterConfig loadConfig() {

        File configFile = new File("config.json");
        MasterConfig masterConfig = simpleConfigLoader.load(MasterConfig.class, configFile);

        //Sorting the Fallbacks after the FallbackPriority, to make it faster
        if (!masterConfig.getProperties().getFallbackProperties().isEmpty()) {
            masterConfig.getProperties().getFallbackProperties().sort(Comparator.comparingInt(SimpleFallback::getPriority));
        } else {
            PoloLogger.print(LogLevel.WARNING, "No fallbacks are registered in config.json! The Cloud don't find any fallbacks, so you can't join!");
            PoloLogger.print(LogLevel.INFO, "Adding a default Lobby fallback!");
            masterConfig.getProperties().getFallbackProperties().add(new SimpleFallback("Lobby", "", true, 1));
        }
        for (SimpleFallback fallbackProperty : masterConfig.getProperties().getFallbackProperties()) {
            PoloCloudAPI.getInstance().getFallbackManager().registerFallback(fallbackProperty);
        }
        simpleConfigSaver.save(masterConfig, configFile);
        return masterConfig;
    }

    @Override
    public void start() {
        running = true;
        PoloLogger.print(LogLevel.INFO, "Trying to start the CloudMaster...");

        this.nettyServer = getGuice().getInstance(SimpleNettyServer.class);
        getGuice().getInstance(SimplePacketService.class);

        this.nettyServer.getProtocol().registerPacketHandler(getGuice().getInstance(PublishPacketHandler.class));
        this.nettyServer.getProtocol().registerPacketHandler(getGuice().getInstance(SubscribePacketHandler.class));

        //events
        getEventManager().registerHandler(ChannelInactiveEvent.class, getGuice().getInstance(ChannelInactiveListener.class));
        getEventManager().registerHandler(ChannelActiveEvent.class, getGuice().getInstance(ChannelActiveListener.class));

        this.pubSubManager = new SimplePubSubManager(nettyServer);
        new Thread(() -> nettyServer.start()).start();

        if (this.templateManager.getTemplates().size() > 0) {
            StringBuilder builder = new StringBuilder();
            this.templateManager.getTemplates().forEach(key -> builder.append(key.getName()).append("(").append(key.getServerCreateThreshold()).append("%), "));
            PoloLogger.print(LogLevel.INFO, "Found templates: " + ConsoleColors.LIGHT_BLUE + builder.substring(0, builder.length() - 2));

            this.moduleService.load();

        } else {
            PoloLogger.print(LogLevel.INFO, "No Templates found to be loaded!");
        }

        PoloLogger.print(LogLevel.INFO, "The master is §asuccessfully §7started.");
    }

    @Override
    public void reload() {
        this.updateCache();
        this.moduleService.reload();
    }

    @Override
    public void updateCache() {
        this.getConnection().sendPacket(new GlobalCachePacket());
    }

    @Override
    public boolean terminate() {
        this.running = false;
        boolean terminate = this.nettyServer.terminate();

        this.moduleService.shutdown(() -> {
            for (IGameServer gameServer : new LinkedList<>(gameServerManager.getAllCached())) {
                gameServer.terminate();
            }

            commandExecutor.sendMessage("§cShutting down in §e2 Seconds§c...");
            loggerFactory.shutdown(() -> {
                Scheduler.runtimeScheduler().schedule(() -> {
                    commandExecutor.sendMessage("§7All §bGameServers §7were §cstopped§7!");
                    commandExecutor.sendMessage("Shutting down Master...");
                    System.exit(0);
                }, 40L);
            });
        });
        return terminate;
    }

    private void registerUncaughtExceptionListener(){
        JsonData jsonData = new JsonData(new File("launcher.json"));
        this.currentVersion = jsonData.fallback("N/A").getString("version");
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            throwable.printStackTrace();
            client.getExceptionReportService().reportException(throwable, "master", this.currentVersion);
        });
    }

    @Override
    public void receivePacket(Packet packet) {
        nettyServer.getProtocol().firePacketHandlers(nettyServer.ctx(), packet);
    }

    @Override
    public String getName() {
        return getType().name();
    }
    @Override
    public Injector getGuice() {
        return injector;
    }

    @Override
    public IPubSubManager getPubSubManager() {
        return pubSubManager;
    }

    public MasterConfig getMasterConfig() {
        return masterConfig;
    }

    public ModuleService getModuleService() {
        return moduleService;
    }

    @Override
    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    public SimpleNettyServer getNettyServer() {
        return nettyServer;
    }

    public boolean isRunning() {
        return running;
    }

    public FallbackSearchService getFallbackSearchService() {
        return fallbackSearchService;
    }

    public PortService getPortService() {
        return portService;
    }

    public PoloCloudClient getClient() {
        return client;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }
}
