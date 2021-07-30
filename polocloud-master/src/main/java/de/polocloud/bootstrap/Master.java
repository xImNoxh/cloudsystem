package de.polocloud.bootstrap;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.event.channel.ChannelActiveEvent;
import de.polocloud.api.event.channel.ChannelInactiveEvent;
import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.event.netty.NettyExceptionEvent;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.guice.PoloAPIGuiceModule;
import de.polocloud.api.network.IStartable;
import de.polocloud.api.network.ITerminatable;
import de.polocloud.api.network.server.SimpleNettyServer;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.client.IWrapperClientManager;
import de.polocloud.bootstrap.client.SimpleWrapperClientManager;
import de.polocloud.bootstrap.commands.*;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.bootstrap.config.database.DatabaseSupport;
import de.polocloud.bootstrap.creator.ServerCreatorRunner;
import de.polocloud.bootstrap.gameserver.SimpleGameServerManager;
import de.polocloud.bootstrap.guice.MasterGuiceModule;
import de.polocloud.bootstrap.listener.ChannelActiveListener;
import de.polocloud.bootstrap.listener.ChannelInactiveListener;
import de.polocloud.bootstrap.listener.NettyExceptionListener;
import de.polocloud.bootstrap.module.MasterModuleLoader;
import de.polocloud.bootstrap.network.handler.*;
import de.polocloud.bootstrap.player.SimpleCloudPlayerManager;
import de.polocloud.bootstrap.pubsub.PublishPacketHandler;
import de.polocloud.bootstrap.pubsub.SubscribePacketHandler;
import de.polocloud.bootstrap.template.SimpleTemplateService;
import de.polocloud.bootstrap.template.TemplateStorage;
import de.polocloud.database.DatabaseService;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class Master implements IStartable, ITerminatable {

    private final CloudAPI cloudAPI;

    private SimpleNettyServer nettyServer;

    public static Master instance;

    private final ITemplateService templateService;
    private final IWrapperClientManager wrapperClientManager;
    private final IGameServerManager gameServerManager;
    private final ICloudPlayerManager cloudPlayerManager;

    private DatabaseService databaseService;

    private final MasterModuleLoader moduleLoader;

    private boolean running = false;


    public Master() {
        instance = this;


        this.wrapperClientManager = new SimpleWrapperClientManager();
        this.gameServerManager = new SimpleGameServerManager();
        this.templateService = new SimpleTemplateService();
        this.cloudPlayerManager = new SimpleCloudPlayerManager();


        this.cloudAPI = new PoloCloudAPI(new PoloAPIGuiceModule(), new MasterGuiceModule(loadConfig(), this, wrapperClientManager, this.gameServerManager, templateService, this.cloudPlayerManager));

        this.moduleLoader = new MasterModuleLoader();


        ((SimpleTemplateService) this.templateService).load(this.cloudAPI, TemplateStorage.FILE);
        this.templateService.getTemplateLoader().loadTemplates();

        EventRegistry.registerListener(new NettyExceptionListener(), NettyExceptionEvent.class);


        PoloCloudAPI.getInstance().getCommandPool().registerCommand(new TemplateCloudCommand(this.templateService));
        PoloCloudAPI.getInstance().getCommandPool().registerCommand(new CloudStopCommand());
        PoloCloudAPI.getInstance().getCommandPool().registerCommand(new ShutdownGameServerCommand(gameServerManager));
        PoloCloudAPI.getInstance().getCommandPool().registerCommand(new CloudHelpCommand());
        PoloCloudAPI.getInstance().getCommandPool().registerCommand(new CloudEditCommand(templateService, gameServerManager));
        PoloCloudAPI.getInstance().getCommandPool().registerCommand(new CloudInfoCommand(this.templateService, this.gameServerManager));
        PoloCloudAPI.getInstance().getCommandPool().registerCommand(new ShutdownTemplateServerCommand(this.gameServerManager, this.templateService));
        PoloCloudAPI.getInstance().getCommandPool().registerCommand(new LogMeCommand());
        PoloCloudAPI.getInstance().getCommandPool().registerCommand(new PlayersCloudCommand(this.cloudPlayerManager, gameServerManager));

        PoloCloudAPI.getInstance().getCommandPool().registerCommand(new GameServerExecuteCommand(this.gameServerManager));
        PoloCloudAPI.getInstance().getCommandPool().registerCommand(CloudAPI.getInstance().getGuice().getInstance(GameServerCloudCommand.class));

        Thread runnerThread = new Thread(PoloCloudAPI.getInstance().getGuice().getInstance(ServerCreatorRunner.class));

        this.moduleLoader.loadModules();

        runnerThread.start();

    }

    private MasterConfig loadConfig() {

        File configFile = new File("config.json");
        IConfigLoader configLoader = new SimpleConfigLoader();

        MasterConfig masterConfig = configLoader.load(MasterConfig.class, configFile);

        IConfigSaver configSaver = new SimpleConfigSaver();
        configSaver.save(masterConfig, configFile);
        activeDatabaseSupport(masterConfig);

        return masterConfig;
    }

    private void activeDatabaseSupport(MasterConfig masterConfig) {
        DatabaseSupport databaseSupport = masterConfig.getDatabaseSupport();
        if (databaseSupport.isUse()) {
            databaseService = new DatabaseService(databaseSupport.getHostname(), databaseSupport.getUsername(), databaseSupport.getPassword(), databaseSupport.getDatabase(), databaseSupport.getPort());
        }
    }

    @Override
    public void start() {
        running = true;
        Logger.log(LoggerType.INFO, "Trying to start master...");

        this.nettyServer = this.cloudAPI.getGuice().getInstance(SimpleNettyServer.class);

        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(WrapperLoginPacketHandler.class));
        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(GameServerRegisterPacketHandler.class));
        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(GameServerPlayerRequestJoinHandler.class));
        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(StatisticMemoryHandler.class));
        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(GameServerPlayerUpdateListener.class));
        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(GameServerPlayerDisconnectListener.class));
        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(GameServerControlPlayerListener.class));
        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(GameServerCloudCommandExecuteListener.class));
        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(APIRequestGameServerHandler.class));
        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(APIRequestCloudPlayerListener.class));
        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(WrapperRegisterStaticServerListener.class));
        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(APIRequestTemplateHandler.class));
        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(PermissionCheckResponseHandler.class));

        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(RedirectPacketHandler.class));

        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(PublishPacketHandler.class));
        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(SubscribePacketHandler.class));

        //events
        EventRegistry.registerListener(PoloCloudAPI.getInstance().getGuice().getInstance(ChannelInactiveListener.class), ChannelInactiveEvent.class);
        EventRegistry.registerListener(PoloCloudAPI.getInstance().getGuice().getInstance(ChannelActiveListener.class), ChannelActiveEvent.class);

        new Thread(() -> nettyServer.start()).start();

        try {
            if (this.templateService.getLoadedTemplates().get().size() > 0) {
                StringBuilder builder = new StringBuilder();
                this.templateService.getLoadedTemplates().get().forEach(key -> builder.append(key.getName()).append("(" + key.getServerCreateThreshold() + "%),"));
                Logger.log(LoggerType.INFO, "Found templates: " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + builder.substring(0, builder.length() - 1));
            } else {
                Logger.log(LoggerType.INFO, "No templates founded.");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


        Logger.log(LoggerType.INFO, "The master is " + ConsoleColors.GREEN.getAnsiCode() + "successfully" + ConsoleColors.GRAY.getAnsiCode() + " started.");
    }


    @Override
    public boolean terminate() {
        this.running = false;
        if(databaseService != null) databaseService.connector().disconnect();
        return this.nettyServer.terminate();
    }

    public boolean isRunning() {
        return running;
    }

    public static Master getInstance() {
        return instance;
    }

    public IGameServerManager getGameServerManager() {
        return gameServerManager;
    }

    public CloudAPI getCloudAPI() {
        return cloudAPI;
    }

    public SimpleNettyServer getNettyServer() {
        return nettyServer;
    }

    public ITemplateService getTemplateService() {
        return templateService;
    }

    public IWrapperClientManager getWrapperClientManager() {
        return wrapperClientManager;
    }

    public ICloudPlayerManager getCloudPlayerManager() {
        return cloudPlayerManager;
    }

    public MasterModuleLoader getModuleLoader() {
        return moduleLoader;
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }


}
