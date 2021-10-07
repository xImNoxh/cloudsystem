package de.polocloud.wrapper;

import com.google.common.base.Throwables;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.executor.SimpleConsoleExecutor;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.FileConstants;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.helper.IStartable;
import de.polocloud.api.network.helper.ITerminatable;
import de.polocloud.api.network.client.SimpleNettyClient;
import de.polocloud.api.network.packets.api.CacheRequestPacket;
import de.polocloud.api.network.packets.master.MasterReportExceptionPacket;
import de.polocloud.api.network.packets.wrapper.WrapperLoginPacket;
import de.polocloud.api.network.packets.wrapper.WrapperUpdatePacket;
import de.polocloud.api.network.protocol.SimpleProtocol;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.pubsub.IPubSubManager;
import de.polocloud.api.pubsub.SimplePubSubManager;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.util.PoloHelper;
import de.polocloud.api.console.ConsoleRunner;
import de.polocloud.api.console.ConsoleColors;
import de.polocloud.api.wrapper.base.IWrapper;
import de.polocloud.wrapper.bootup.InternalWrapperBootstrap;
import de.polocloud.wrapper.impl.commands.HelpCommand;
import de.polocloud.wrapper.impl.commands.ScreenCommand;
import de.polocloud.wrapper.impl.commands.StopCommand;
import de.polocloud.wrapper.impl.config.WrapperConfig;
import de.polocloud.wrapper.impl.handler.*;
import de.polocloud.wrapper.manager.module.ModuleCopyService;
import de.polocloud.wrapper.manager.screen.IScreen;
import de.polocloud.wrapper.manager.screen.impl.SimpleCachedScreenManager;
import de.polocloud.wrapper.manager.screen.IScreenManager;
import de.polocloud.wrapper.manager.server.ServiceStarter;
import de.polocloud.wrapper.setup.WrapperSetup;
import io.netty.channel.ChannelHandlerContext;
import jline.console.ConsoleReader;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class Wrapper extends PoloCloudAPI implements IWrapper, IStartable, ITerminatable {

    /**
     * The connection
     */
    private final SimpleNettyClient nettyClient;

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
     * The copy service to handle packet-sent modules
     */
    private final ModuleCopyService moduleCopyService;

    /**
     * If this wrapper is authenticated
     */
    private boolean authenticated;

    /**
     * The currently starting servers
     */
    private int currentlyStartingServices;


    public Wrapper(boolean devMode, boolean ignoreUpdater) {
        super(PoloType.WRAPPER);

        PoloCloudAPI.getInstance().checkRequirements();

        //PoloCloudClient reference (Address and port)
        InternalWrapperBootstrap bootstrap = new InternalWrapperBootstrap();

        this.screenManager = new SimpleCachedScreenManager();
        this.config = bootstrap.loadWrapperConfig();

        this.loggerFactory.setGlobalPrefix(PoloHelper.CONSOLE_PREFIX);
        this.authenticated = false;
        this.currentlyStartingServices = 0;

        this.nettyClient = new SimpleNettyClient(config.getMasterAddress().split(":")[0], Integer.parseInt(config.getMasterAddress().split(":")[1]), new SimpleProtocol());
        this.pubSubManager = new SimplePubSubManager(nettyClient);

        this.moduleCopyService = new ModuleCopyService();

        //Loading wrapper boot
        bootstrap.registerUncaughtExceptionListener();
        bootstrap.checkAndDeleteTmpFolder();
        bootstrap.checkAndDeletePatchFolder();

        //Registering commands
        this.commandManager.registerCommand(new StopCommand());
        this.commandManager.registerCommand(new HelpCommand());
        this.commandManager.registerCommand(new ScreenCommand());
    }

    @Override
    public ConsoleReader getConsoleReader() {
        return ConsoleRunner.getInstance().getConsoleReader();
    }

    @Override
    public void updateCache() {
        sendPacket(new CacheRequestPacket());
    }

    boolean retrying;

    @Override
    public void start() {
        PoloLogger.print(LogLevel.INFO, (retrying ? "Retrying" : "Trying") + " to start the CloudWrapper...");

        if (config.getLoginKey().equalsIgnoreCase("default") || config.getLoginKey() == null) {
            PoloLogger.print(LogLevel.INFO, "§cIt seems like you §ehave not §cset up PoloCloud yet! Lets fix this real quick...");

            new WrapperSetup().sendSetup();
            return;
        }

        ConsoleRunner.getInstance().setActive(true);
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
            nettyClient.getProtocol().registerPacketHandler(new WrapperHandlerScreenRequest());
            nettyClient.getProtocol().registerPacketHandler(new WrapperHandlerMasterRequestCPU());
            nettyClient.getProtocol().registerPacketHandler(new WrapperHandlerMasterRequestMemory());
            nettyClient.getProtocol().registerPacketHandler(new WrapperHandlerMasterRequestUnusedMemory());

            PoloLogger.print(LogLevel.INFO, "The Wrapper was " + ConsoleColors.GREEN + "successfully " + ConsoleColors.GRAY + "started.");

            //Logging in
            PoloLogger.print(LogLevel.INFO, "§7Trying to log in as §7'§3" + config.getWrapperName() + "§7'!");
            nettyClient.sendPacket(new WrapperLoginPacket(this, config.getLoginKey()));
        }, throwable -> {
            if (throwable.getClass().getName().equalsIgnoreCase("io.netty.channel.AbstractChannel$AnnotatedConnectException")) {
                PoloLogger.print(LogLevel.ERROR, "§cCould not connect to §eMaster");
                retrying = true;
                Scheduler.runtimeScheduler().schedule(this::start, 20L);
            } else {
                throwable.printStackTrace();
            }
        })).start();

    }

    @Override
    public CommandExecutor getCommandExecutor() {
        return new SimpleConsoleExecutor();
    }

    @Override
    public void reportException(Throwable throwable) {
        sendPacket(new MasterReportExceptionPacket(throwable));
        String logException = Throwables.getStackTraceAsString(throwable);

        Objects.requireNonNull(PoloLogger.getInstance()).log(LogLevel.ERROR, "§c=======================");
        PoloLogger.getInstance().log(LogLevel.ERROR,"§cUnhandled §eException §coccurred while running a §eProcess§c!");
        PoloLogger.getInstance().log(LogLevel.ERROR,"§cThis was §enot §cintended to §ehappen.");
        PoloLogger.getInstance().log(LogLevel.INFO, "§cThe exception was sent to the master. The master will process the exception...");
        PoloLogger.getInstance().log(LogLevel.ERROR,"§cSTACKTRACE");
        PoloLogger.getInstance().log(LogLevel.ERROR, "§c=======================");
        PoloLogger.getInstance().noPrefix().log(LogLevel.ERROR,  "§e" + logException);
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
        return config.getWrapperName();
    }

    @Override
    public int getMaxSimultaneouslyStartingServices() {
        return config.getMaxSimultaneouslyStartingServices();
    }

    @Override
    public int getCurrentlyStartingServices() {
        return currentlyStartingServices;
    }

    @Override
    public void setCurrentlyStartingServices(int i) {
        this.currentlyStartingServices = i;
    }

    @Override
    public float getCpuUsage() {
        return (float) PoloCloudAPI.getInstance().getSystemManager().getResourceProvider().getSystemCpuLoad();
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    @Override
    public long getSnowflake() {
        return this.config.getSnowflake();
    }

    @Override
    public long getMaxMemory() {
        return config.getMemory();
    }

    @Override
    public boolean isStillConnected() {
        return true;
    }

    @Override
    public List<IGameServer> getServers() {
        return PoloCloudAPI.getInstance().getGameServerManager().getAllCached(gameServer -> gameServer.getWrapper().getName().equalsIgnoreCase(this.getName()));
    }

    @Override
    public ChannelHandlerContext ctx() {
        return getConnection().ctx();
    }

    @Override
    public void update() {
        WrapperUpdatePacket wrapperUpdatePacket = new WrapperUpdatePacket(this);
        sendPacket(wrapperUpdatePacket);
    }

    @Override
    public void startServer(IGameServer gameServer) {

        Scheduler.runtimeScheduler().schedule(() -> {
            IGameServer cached = PoloCloudAPI.getInstance().getGameServerManager().getCached(gameServer.getName());
            if (cached == null) {
                cached = gameServer;
            }
            if (cached.getPort() == -1) {
                cached.setPort(PoloCloudAPI.getInstance().getGameServerManager().getFreePort(cached.getTemplate()));
            }

            ServiceStarter serviceStarter = new ServiceStarter(cached);
            if (serviceStarter.checkWrapper()) {
                try {
                    serviceStarter.copyFiles();
                    serviceStarter.createProperties();
                    serviceStarter.createCloudFiles();
                    serviceStarter.start(server -> {
                        //If in screen not sending message!
                        if (Wrapper.getInstance().getScreenManager().getScreen() != null && Wrapper.getInstance().getScreenManager().isInScreen()) {
                            return;
                        }
                        PoloLogger.print(LogLevel.INFO, "§7Queued GameServer §b" + server.getName() + " §7[§7Port: §b" + server.getPort() + " §7| §7Mode: §b" + (server.getTemplate().isDynamic() ? "DYNAMIC" : "STATIC") + "_" + server.getTemplate().getTemplateType() + "§7@§3" + server.getTemplate().getVersion().getTitle() + "§7]");
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, 1L);
    }

    @Override
    public void stopServer(IGameServer gameServer) {
        long snowflake = gameServer.getSnowflake();
        String name = gameServer.getName();

        IScreenManager screenManager = Wrapper.getInstance().getScreenManager();

        IScreen screen = screenManager.getScreen(name);
        if (screen != null) {
            Process process = screen.getProcess();
            if (process != null) {
                process.destroy();
            }
        } else {
            PoloLogger.print(LogLevel.ERROR, "§cCouldn't stop §e" + name + " §cbecause no Screen was registered!");
        }
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
    public boolean terminate() {
        boolean terminate = this.nettyClient.terminate();

        for (IScreen screen : screenManager.getScreens()) {
            Process process = screen.getProcess();
            if (process != null) {
                process.destroy();
            }
        }

        this.loggerFactory.shutdown();
        Scheduler.runtimeScheduler().schedule(() -> {
            PoloHelper.deleteFolder(FileConstants.MASTER_MODULES);
            PoloHelper.deleteFolder(FileConstants.WRAPPER_DYNAMIC_SERVERS);
            PoloHelper.deleteFolder(FileConstants.WRAPPER_PATCHER_FOLDER);
        }, 10L);
        Scheduler.runtimeScheduler().schedule(() -> System.exit(0), 20L);
        return terminate;
    }

    public static Wrapper getInstance() {
        return (Wrapper) PoloCloudAPI.getInstance();
    }

}
