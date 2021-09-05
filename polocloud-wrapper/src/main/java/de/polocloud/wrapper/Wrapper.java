package de.polocloud.wrapper;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.executor.ConsoleExecutor;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.master.MasterConfig;
import de.polocloud.api.guice.google.PoloAPIGuiceModule;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.helper.IStartable;
import de.polocloud.api.network.helper.ITerminatable;
import de.polocloud.api.network.client.SimpleNettyClient;
import de.polocloud.api.network.packets.api.CacheRequestPacket;
import de.polocloud.api.network.packets.master.MasterReportExceptionPacket;
import de.polocloud.api.network.packets.wrapper.WrapperLoginPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.pubsub.IPubSubManager;
import de.polocloud.api.pubsub.SimplePubSubManager;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.util.PoloHelper;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.wrapper.bootup.InternalWrapperBootstrap;
import de.polocloud.wrapper.impl.commands.HelpCommand;
import de.polocloud.wrapper.impl.commands.ScreenCommand;
import de.polocloud.wrapper.impl.commands.StopCommand;
import de.polocloud.wrapper.impl.config.WrapperConfig;
import de.polocloud.wrapper.impl.guice.WrapperGuiceModule;
import de.polocloud.wrapper.impl.handler.*;
import de.polocloud.wrapper.manager.module.ModuleCopyService;
import de.polocloud.wrapper.manager.screen.IScreen;
import de.polocloud.wrapper.manager.screen.impl.SimpleCachedScreenManager;
import de.polocloud.wrapper.manager.screen.IScreenManager;

import java.net.InetSocketAddress;


public class Wrapper extends PoloCloudAPI implements IStartable, ITerminatable {

    /**
     * The connection
     */
    private SimpleNettyClient nettyClient;

    /**
     * The Google guice injector
     */
    private final Injector injector;

    /**
     * The wrapper config object (default)
     */
    private final WrapperConfig config;

    /**
     * The {@link IScreenManager} to manage all server outputs
     */
    private final IScreenManager screenManager;

    /**
     * The {@link IPubSubManager} instance
     */
    private final IPubSubManager pubSubManager;

    /**
     * The master config
     */
    private MasterConfig masterConfig;

    private ModuleCopyService moduleCopyService;

    public Wrapper(boolean devMode) {
        super(PoloType.WRAPPER);
                                                                                //PoloCloudClient reference (Address and port)
        InternalWrapperBootstrap bootstrap = new InternalWrapperBootstrap(this, devMode, new InetSocketAddress("37.114.60.98", 4542));

        this.screenManager = new SimpleCachedScreenManager();
        this.config = bootstrap.loadWrapperConfig();
        this.injector = Guice.createInjector(new PoloAPIGuiceModule(), new WrapperGuiceModule(config.getMasterAddress().split(":")[0], Integer.parseInt(config.getMasterAddress().split(":")[1])));

        this.loggerFactory.setGlobalPrefix(PoloHelper.CONSOLE_PREFIX);

        this.nettyClient = this.injector.getInstance(SimpleNettyClient.class);
        this.pubSubManager = new SimplePubSubManager(nettyClient);

        this.moduleCopyService = new ModuleCopyService();

        //Loading wrapper boot
        bootstrap.registerUncaughtExceptionListener();
        bootstrap.checkAndDeleteTmpFolder();
        bootstrap.checkPoloCloudAPI();

        //Registering commands
        this.commandManager.registerCommand(new StopCommand());
        this.commandManager.registerCommand(new HelpCommand());
        this.commandManager.registerCommand(new ScreenCommand());
    }

    @Override
    public void updateCache() {
        sendPacket(new CacheRequestPacket());
    }

    @Override
    public void start() {
        if (config.getLoginKey().equalsIgnoreCase("default")) {

            PoloLogger.print(LogLevel.ERROR, "§cPlease put the §eWrapperKey §cof the Master into the §econfig.json§c!");
            terminate();
        }
        PoloLogger.print(LogLevel.INFO, "Trying to start the wrapper...");

        new Thread(() -> this.nettyClient.start(nettyClient -> {

            //Registering handlers
            nettyClient.getProtocol().registerPacketHandler(new WrapperHandlerLoginResponse());
            nettyClient.getProtocol().registerPacketHandler(new WrapperHandlerMasterRequestStart());
            nettyClient.getProtocol().registerPacketHandler(new WrapperHandlerCopyServer());
            nettyClient.getProtocol().registerPacketHandler(new WrapperHandlerShutdownRequest());
            nettyClient.getProtocol().registerPacketHandler(new WrapperHandlerCacheUpdate());
            nettyClient.getProtocol().registerPacketHandler(new WrapperHandlerMasterRequestTerminate());
            nettyClient.getProtocol().registerPacketHandler(new WrapperHandlerFileTransfer());
            nettyClient.getProtocol().registerPacketHandler(new WrapperHandlerTransferModules());

            PoloLogger.print(LogLevel.INFO, "The Wrapper was " + ConsoleColors.GREEN + "successfully " + ConsoleColors.GRAY + "started.");

            //Logging in
            PoloLogger.print(LogLevel.INFO, "§7Trying to log in as §7'§3" + config.getWrapperName() + "§7'!");
            nettyClient.sendPacket(new WrapperLoginPacket(config.getWrapperName(), config.getLoginKey()));
        })).start();

    }

    @Override
    public CommandExecutor getCommandExecutor() {
        return new ConsoleExecutor() {
            @Override
            public void runCommand(String command) {
                getCommandManager().runCommand(command, this);
            }

            @Override
            public void sendMessage(String text) {
                PoloLogger.print(LogLevel.INFO, ConsoleColors.translateColorCodes('§', text));
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
    }

    @Override
    public void reportException(Throwable throwable) {
        sendPacket(new MasterReportExceptionPacket(throwable));
    }

    @Override
    public void reload() {
        updateCache();
    }

    @Override
    public void receivePacket(Packet packet) {
        nettyClient.getProtocol().firePacketHandlers(nettyClient.ctx(), packet);
    }

    @Override
    public String getName() {
        return getType().name();
    }
    @Override
    public INetworkConnection getConnection() {
        return this.nettyClient;
    }

    @Override
    public IPubSubManager getPubSubManager() {
        return pubSubManager;
    }

    @Override
    public Injector getGuice() {
        return injector;
    }


    @Override
    public boolean terminate() {
        boolean terminate = this.nettyClient.terminate();

        for (IScreen screen : screenManager.getScreens()) {
            Process process = screen.getProcess();
            if (process != null) {
                process.destroy();
            }
        }

        this.loggerFactory.shutdown(() -> Scheduler.runtimeScheduler().schedule(() -> System.exit(0), 20L));
        return terminate;
    }

    public WrapperConfig getConfig() {
        return config;
    }

    public IScreenManager getScreenManager() {
        return screenManager;
    }

    public static Wrapper getInstance() {
        return (Wrapper) PoloCloudAPI.getInstance();
    }

    public ModuleCopyService getModuleCopyService() {
        return moduleCopyService;
    }
}
