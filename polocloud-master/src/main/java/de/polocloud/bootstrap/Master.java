package de.polocloud.bootstrap;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.executor.ConsoleExecutor;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.event.impl.net.ChannelActiveEvent;
import de.polocloud.api.event.impl.net.ChannelInactiveEvent;
import de.polocloud.api.event.impl.net.NettyExceptionEvent;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.guice.PoloAPIGuiceModule;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.IStartable;
import de.polocloud.api.network.ITerminatable;
import de.polocloud.api.network.protocol.packet.api.other.GlobalCachePacket;
import de.polocloud.api.network.server.SimpleNettyServer;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.pubsub.IPubSubManager;
import de.polocloud.api.pubsub.SimplePubSubManager;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.api.util.PoloUtils;
import de.polocloud.bootstrap.commands.*;
import de.polocloud.bootstrap.commands.ingame.CloudIngameCommand;
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
import de.polocloud.api.fallback.base.SimpleFallback;
import de.polocloud.bootstrap.template.fallback.FallbackSearchService;
import de.polocloud.client.PoloCloudClient;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

public class Master extends PoloCloudAPI implements IStartable, ITerminatable {

    private final Injector injector;

    private final ITemplateService templateService;
    private final IGameServerManager gameServerManager;
    private final ICloudPlayerManager cloudPlayerManager;
    private final CommandExecutor commandExecutor;

    private final SimpleConfigLoader simpleConfigLoader = new SimpleConfigLoader();
    private final SimpleConfigSaver simpleConfigSaver = new SimpleConfigSaver();

    private final FallbackSearchService fallbackSearchService;
    private final ModuleCache moduleCache;
    private final MasterModuleLoader moduleLoader;
    private final PortService portService;
    private IPubSubManager pubSubManager;

    private SimpleNettyServer nettyServer;
    private boolean running = false;

    private final MasterConfig masterConfig;

    private PoloCloudClient client;

    private String currentVersion = "N/A";

    public Master() {
        super(PoloType.MASTER);

        this.commandExecutor = new ConsoleExecutor() {
            @Override
            public void runCommand(String command) {
                getCommandManager().runCommand(command, this);
            }

            @Override
            public void sendMessage(String text) {
                Logger.log(LoggerType.INFO, Logger.PREFIX + ConsoleColors.translateColorCodes('§', text));
            }

            @Override
            public ExecutorType getType() {
                return ExecutorType.CONSOLE;
            }

            @Override
            public boolean hasPermission(String permission) {
                return true;
            }

        };

        //TODO change IP on release
        this.client = new PoloCloudClient("127.0.0.1", 4542);
        registerUncaughtExceptionListener();

        this.gameServerManager = new SimpleGameServerManager();
        this.templateService = new SimpleTemplateService();
        this.cloudPlayerManager = new SimpleCloudPlayerManager();
        this.portService = new PortService(gameServerManager);

        masterConfig = loadConfig();

        injector = Guice.createInjector(new PoloAPIGuiceModule(), new MasterGuiceModule(masterConfig, this, this.gameServerManager, templateService, this.cloudPlayerManager));

        this.fallbackSearchService = new FallbackSearchService(masterConfig);
        this.moduleCache = new ModuleCache();
        this.moduleLoader = new MasterModuleLoader(moduleCache);

        ((SimpleTemplateService) this.templateService).load(this, TemplateStorage.FILE);
        this.templateService.getTemplateLoader().loadTemplates();

        getEventManager().registerHandler(NettyExceptionEvent.class, new NettyExceptionListener());

        getCommandManager().registerCommand(new StopCommand());
        getCommandManager().registerCommand(new TemplateCommand(templateService, gameServerManager));

        getCommandManager().registerCommand(new ReloadCommand());
        getCommandManager().registerCommand(new HelpCommand());
        getCommandManager().registerCommand(new PlayerCommand(cloudPlayerManager, gameServerManager));

        getCommandManager().registerCommand(getGuice().getInstance(GameServerCommand.class));
        getCommandManager().registerCommand(getGuice().getInstance(WrapperCommand.class));
        getCommandManager().registerCommand(getGuice().getInstance(CloudIngameCommand.class));
        getCommandManager().registerCommand(new ChangelogCommand());


        Thread runnerThread = new Thread(getGuice().getInstance(ServerCreatorRunner.class));
        this.moduleLoader.loadModules(false);
        runnerThread.start();
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
            Logger.log(LoggerType.WARNING, "No fallbacks are registered in config.json! The Cloud don't find any fallbacks, so you can't join!");
            Logger.log(LoggerType.INFO, "Adding a default Lobby fallback!");
            masterConfig.getProperties().getFallbackProperties().add(new SimpleFallback("Lobby", "", true, 1));
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
        getEventManager().registerHandler(ChannelInactiveEvent.class, getGuice().getInstance(ChannelInactiveListener.class));
        getEventManager().registerHandler(ChannelActiveEvent.class, getGuice().getInstance(ChannelActiveListener.class));

        this.pubSubManager = new SimplePubSubManager(nettyServer);
        new Thread(() -> nettyServer.start()).start();

        try {
            if (this.templateService.getLoadedTemplates().get().size() > 0) {
                StringBuilder builder = new StringBuilder();
                this.templateService.getLoadedTemplates().get().forEach(key -> builder.append(key.getName()).append("(").append(key.getServerCreateThreshold()).append("%), "));
                Logger.log(LoggerType.INFO, "Found templates: " + ConsoleColors.LIGHT_BLUE + builder.substring(0, builder.length() - 2));
            } else {
                Logger.log(LoggerType.INFO, "No templates found.");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Logger.log(LoggerType.INFO, "The master is " + ConsoleColors.GREEN + "successfully" + ConsoleColors.GRAY + " started.");
    }

    @Override
    public void updateCache() {
        this.getConnection().sendPacket(new GlobalCachePacket());
    }

    @Override
    public boolean terminate() {
        this.running = false;
        boolean terminate = this.nettyServer.terminate();

        for (IGameServer gameServer : PoloUtils.sneakyThrows(() -> gameServerManager.getGameServers().get())) {
            gameServer.terminate();
        }
        commandExecutor.sendMessage("§cShutting down in §e2 Seconds§c...");
        Scheduler.runtimeScheduler().schedule(() -> {
            commandExecutor.sendMessage("§7All §bGameServers §7were §cstopped§7!");
            commandExecutor.sendMessage("Shutting down Master...");
            System.exit(0);
        }, 40L);
        return terminate;
    }

    private void registerUncaughtExceptionListener(){
        String currentVersion = "-1";
        try {
            FileReader reader = new FileReader("launcher.json");
            this.currentVersion = new GsonBuilder().setPrettyPrinting().create().fromJson(reader, JsonObject.class).get("version").getAsString();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String finalCurrentVersion = this.currentVersion;
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            throwable.printStackTrace();
            client.getExceptionReportService().reportException(throwable, "master", finalCurrentVersion);
        });
    }

    @Override
    public Injector getGuice() {
        return injector;
    }

    @Override
    public IPubSubManager getPubSubManager() {
        return pubSubManager;
    }

    @Override
    public ITemplateService getTemplateService() {
        return templateService;
    }

    public MasterConfig getMasterConfig() {
        return masterConfig;
    }

    @Override
    public CommandExecutor getCommandExecutor() {
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

    public PoloCloudClient getClient() {
        return client;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }
}
