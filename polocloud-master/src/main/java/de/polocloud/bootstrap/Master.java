package de.polocloud.bootstrap;

import com.google.common.base.Throwables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.polocloud.api.APIVersion;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.executor.SimpleConsoleExecutor;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.FileConstants;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.event.impl.net.ChannelActiveEvent;
import de.polocloud.api.event.impl.net.ChannelInactiveEvent;
import de.polocloud.api.event.impl.net.NettyExceptionEvent;
import de.polocloud.api.fallback.base.SimpleFallback;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.guice.google.PoloAPIGuiceModule;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.module.loader.ModuleService;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.helper.IStartable;
import de.polocloud.api.network.packets.api.GlobalCachePacket;
import de.polocloud.api.network.packets.api.PropertyCachePacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.server.SimpleNettyServer;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.pubsub.IPubSubManager;
import de.polocloud.api.pubsub.SimplePubSubManager;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.template.SimpleTemplate;
import de.polocloud.api.template.TemplateStorage;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.helper.GameServerVersion;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.api.util.gson.PoloHelper;
import de.polocloud.bootstrap.commands.*;
import de.polocloud.api.config.master.MasterConfig;
import de.polocloud.bootstrap.creator.ServerCreatorRunner;
import de.polocloud.bootstrap.fallback.FallbackSearchService;
import de.polocloud.bootstrap.guice.MasterGuiceModule;
import de.polocloud.bootstrap.listener.ChannelActiveListener;
import de.polocloud.bootstrap.listener.ChannelInactiveListener;
import de.polocloud.bootstrap.listener.NettyExceptionListener;
import de.polocloud.bootstrap.network.SimplePacketService;
import de.polocloud.bootstrap.network.ports.PortService;
import de.polocloud.bootstrap.pubsub.PublishPacketHandler;
import de.polocloud.bootstrap.pubsub.SubscribePacketHandler;
import de.polocloud.bootstrap.setup.MasterSetup;
import de.polocloud.client.PoloCloudClient;
import de.polocloud.api.console.ConsoleRunner;
import de.polocloud.api.console.ConsoleColors;
import jline.console.ConsoleReader;

import java.io.File;
import java.util.*;

public class Master extends PoloCloudAPI implements IStartable {

    private Injector injector;

    private final CommandExecutor commandExecutor;

    private final SimpleConfigLoader simpleConfigLoader = new SimpleConfigLoader();
    private final SimpleConfigSaver simpleConfigSaver = new SimpleConfigSaver();

    private FallbackSearchService fallbackSearchService;
    private ModuleService moduleService;
    private final PortService portService;
    private MasterConfig masterConfig;
    private final PoloCloudClient client;
    private IPubSubManager pubSubManager;
    private SimpleNettyServer nettyServer;
    private boolean running = false;

    public Master() {
        super(PoloType.MASTER);

        this.commandExecutor = new SimpleConsoleExecutor();

        this.client = new PoloCloudClient("37.114.60.98", 4542);
        this.registerUncaughtExceptionListener();

        this.portService = new PortService(gameServerManager);
        this.loggerFactory.setGlobalPrefix(PoloHelper.CONSOLE_PREFIX);

        this.masterConfig = this.loadConfig();

        if (this.propertyManager.loadProperties()) {

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
            this.commandManager.registerCommand(new MeCommand());
            this.commandManager.registerCommand(new ScreenCommand());
            this.commandManager.registerCommand(injector.getInstance(GameServerCommand.class));
            this.commandManager.registerCommand(injector.getInstance(WrapperCommand.class));


            //Server-Runner thread starting
            new Thread(new ServerCreatorRunner()).start();

        }
    }

    public static Master getInstance() {
        return (Master) PoloCloudAPI.getInstance();
    }

    @Override
    public INetworkConnection getConnection() {
        return this.nettyServer;
    }

    private MasterConfig loadConfig() {

        File configFile = FileConstants.MASTER_CONFIG_FILE;
        MasterConfig masterConfig = simpleConfigLoader.load(MasterConfig.class, configFile);

        //Registering default fallback
        if (masterConfig.getProperties().getFallbacks().isEmpty()) {
            masterConfig.getProperties().getFallbacks().add(new SimpleFallback("Lobby", "", true, 1));
        }
        //Sorting the Fallbacks after the FallbackPriority, to make it faster
        masterConfig.getProperties().getFallbacks().sort(Comparator.comparingInt(SimpleFallback::getPriority));
        for (SimpleFallback fallbackProperty : masterConfig.getProperties().getFallbacks()) {
            PoloCloudAPI.getInstance().getFallbackManager().registerFallback(fallbackProperty);
        }
        simpleConfigSaver.save(masterConfig, configFile);
        return masterConfig;
    }

    @Override
    public void start() {
        running = true;
        PoloHelper.printHeader();
        PoloLogger.print(LogLevel.INFO, "Trying to start the CloudMaster...");

        if (masterConfig.getProperties().getWrapperKey() == null) {
            PoloLogger.print(LogLevel.INFO, "§cIt seems like you §ehave not §cset up PoloCloud yet! Lets fix this real quick...");

            new MasterSetup().sendSetup();
            String wrapperKey = UUID.randomUUID() + "@CloudMaster@" + UUID.randomUUID();
            masterConfig.getProperties().setWrapperKey(wrapperKey);
            masterConfig.update();

            PoloLogger.print(LogLevel.INFO, "§cThe Master will now §eshutdown§c! You have to restart to apply all changes and confirm that no bugs appear!");
            System.exit(0);

        }

        ConsoleRunner.getInstance().setActive(true);

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
            PoloLogger.print(LogLevel.INFO, "Loaded templates: " + ConsoleColors.LIGHT_BLUE + builder.substring(0, builder.length() - 2));

            this.moduleService.load();
            if (!this.moduleService.getModules().isEmpty()) {
                PoloLogger.print(LogLevel.INFO, "§8"); //Empty line for better design after module load
            }

        } else {
            PoloLogger.print(LogLevel.ERROR, "§cNo Templates found to be loaded! Creating default Lobby-Template and Proxy-Template...");

            ITemplate proxy = new SimpleTemplate("Proxy", false, 5, 1, TemplateType.PROXY, GameServerVersion.PROXY, 50, 512, false, "A PoloCloud Proxy", 100, new String[]{"Wrapper-1"});
            ITemplate lobby = new SimpleTemplate("Lobby", false, 5, 1, TemplateType.MINECRAFT, GameServerVersion.SPIGOT_1_8_8, 25, 512, false, "A PoloCloud Lobby", 100, new String[]{"Wrapper-1"});

            templateManager.addTemplate(proxy);
            templateManager.addTemplate(lobby);
            templateManager.reloadTemplates();
            PoloLogger.print(LogLevel.ERROR, "§7Created §b" + proxy.getName() + " §7as §eProxy-Template §7and §3" + lobby.getName() + " §7as §6Lobby-Template§7!");
            PoloLogger.print(LogLevel.INFO, "§7Registering new created §bLobby-Template §7as §3Fallback§7!");
            masterConfig.update();
        }

        PoloLogger.print(LogLevel.INFO, "The master is §asuccessfully §7started.");
    }

    @Override
    public void reload() {
        this.masterConfig = loadConfig();
        this.moduleService.reload();

        this.updateCache();
    }

    @Override
    public void updateCache() {
        if (this.getConnection() == null) {
            return;
        }
        this.getConnection().sendPacket(new GlobalCachePacket());
        this.getConnection().sendPacket(new PropertyCachePacket());
    }

    @Override
    public boolean terminate() {
        this.running = false;
        boolean terminate = this.nettyServer != null && this.nettyServer.terminate();

        //Kicking all players
        commandExecutor.sendMessage("§7Kicking §3" + (cloudPlayerManager == null ? "0" : cloudPlayerManager.getAllCached().size()) + " §7CloudPlayers...");
        for (ICloudPlayer cloudPlayer : (cloudPlayerManager == null ? new ArrayList<ICloudPlayer>() : cloudPlayerManager)) {
            cloudPlayer.kick(PoloCloudAPI.getInstance().getMasterConfig().getMessages().getNetworkShutdown());
        }

        //Stopping all servers
        commandExecutor.sendMessage("§7Stopping §3" + gameServerManager.getAllCached().size() + " §7GameServers...");
        for (IGameServer gameServer : new LinkedList<>(gameServerManager.getAllCached())) {
            gameServer.terminate();
        }

        //Shutting down all modules
        commandExecutor.sendMessage("§7Disabling §3" + moduleService.getModules().size() + " §7Modules...");
        moduleService.shutdown();

        //Stopping process
        Scheduler.runtimeScheduler().schedule(() -> System.exit(0), 20L);

        //Shutting down loggers
        commandExecutor.sendMessage("§7Saving §3" + loggerFactory.getLoggers().size() + " §7Loggers...");
        loggerFactory.shutdown();
        return terminate;
    }


    @Override
    public ConsoleReader getConsoleReader() {
        return ConsoleRunner.getInstance().getConsoleReader();
    }
    @Override
    public void reportException(Throwable throwable) {
        String logException = Throwables.getStackTraceAsString(throwable);

        PoloLogger.getInstance().log(LogLevel.ERROR, "§c=======================");
        PoloLogger.getInstance().log(LogLevel.ERROR,"§cUnhandled §eException §coccurred while running §eProcess§c!");
        PoloLogger.getInstance().log(LogLevel.ERROR,"§cThis was §enot §cintended to §ehappen.");
        PoloLogger.getInstance().log(LogLevel.ERROR,"§cPlease §ereport §cthis at §e" + PoloCloudAPI.class.getAnnotation(APIVersion.class).discord());
        PoloLogger.getInstance().log(LogLevel.ERROR, "");
        PoloLogger.getInstance().log(LogLevel.ERROR,"§cSTACKTRACE");
        PoloLogger.getInstance().log(LogLevel.ERROR, "§c=======================");
        PoloLogger.getInstance().noPrefix().log(LogLevel.ERROR,  "§e" + logException);

        client.getExceptionReportService().reportException(throwable, "master", getVersion().version());
    }

    private void registerUncaughtExceptionListener() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> reportException(throwable));
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
        return getVersion().version();
    }
}
