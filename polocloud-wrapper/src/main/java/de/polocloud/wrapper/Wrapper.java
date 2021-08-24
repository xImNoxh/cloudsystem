package de.polocloud.wrapper;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.executor.ConsoleExecutor;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.event.impl.net.ChannelActiveEvent;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.guice.PoloAPIGuiceModule;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.IStartable;
import de.polocloud.api.network.ITerminatable;
import de.polocloud.api.network.client.SimpleNettyClient;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperLoginPacket;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperRegisterStaticServerPacket;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.pubsub.IPubSubManager;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.api.wrapper.IWrapperManager;
import de.polocloud.client.PoloCloudClient;
import de.polocloud.client.PoloCloudUpdater;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;
import de.polocloud.updater.UpdateClient;
import de.polocloud.wrapper.commands.HelpCommand;
import de.polocloud.wrapper.commands.StopCommand;
import de.polocloud.wrapper.config.WrapperConfig;
import de.polocloud.wrapper.guice.WrapperGuiceModule;
import de.polocloud.wrapper.network.handler.*;
import de.polocloud.wrapper.process.ProcessManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
     * The wrapper config object
     */
    private final WrapperConfig config;

    private ProcessManager processManager;

    /**
     * Declares if the wrapper is in the devMode
     */
    private boolean devMode;

    /**
     * The PoloCloudClient for the PoloCloudUpdater and
     * the ExceptionReporterService
     */
    private PoloCloudClient poloCloudClient;

    public Wrapper(boolean devMode) {
        super(PoloType.WRAPPER);

        this.devMode = devMode;

        //TODO change IP to PoloCloud Servers on release
        this.poloCloudClient = new PoloCloudClient("127.0.0.1", 4542);

        registerUncaughtExceptionListener();

        this.config = loadWrapperConfig();

        this.checkNecessaryFolders();
        this.requestStaticServersStart();
        this.checkAndDeleteTmpFolder();
        this.checkPoloCloudAPI();

        String[] masterAddress = config.getMasterAddress().split(":");
        this.injector = Guice.createInjector(new PoloAPIGuiceModule(), new WrapperGuiceModule(masterAddress[0], Integer.parseInt(masterAddress[1])));

        this.commandManager.registerCommand(new StopCommand());
        this.commandManager.registerCommand(new HelpCommand());
    }

    public static Wrapper getInstance() {
        return (Wrapper) PoloCloudAPI.getInstance();
    }

    @Override
    public INetworkConnection getConnection() {
        return this.nettyClient;
    }

    private void requestStaticServersStart() {
        Executor executor = Executors.newCachedThreadPool();

        PoloCloudAPI.getInstance().getEventManager().registerHandler(ChannelActiveEvent.class, event -> executor.execute(() -> {
            for (String staticServer : config.getStaticServers()) {
                executor.execute(() -> {
                    String[] properties = staticServer.split(",");
                    String serverName = properties[0];
                    int port = Integer.parseInt(properties[1]);
                    int processMemory = Integer.parseInt(properties[2]);
                    Logger.log(LoggerType.INFO, "Starting static server » " + ConsoleColors.LIGHT_BLUE + serverName + ConsoleColors.GRAY + " on port » " + ConsoleColors.LIGHT_BLUE + port + ConsoleColors.GRAY + "...");
                    ProcessBuilder processBuilder = new ProcessBuilder(("java -jar -Xms" + processMemory + "M -Xmx" + processMemory + "M -Dcom.mojang.eula.agree=true spigot.jar nogui --online-mode false --max-players " + 100 + " --noconsole --port " + port).split(" "));
                    try {
                        processBuilder.directory(new File("static/" + serverName));

                        String name = serverName.split("#")[0];
                        long snowflakeID = Long.parseLong(serverName.split("#")[1]);

                        Logger.log(LoggerType.INFO, "Starting static server with " + ConsoleColors.LIGHT_BLUE + name + ConsoleColors.GRAY + "/" + ConsoleColors.LIGHT_BLUE + ConsoleColors.GRAY + snowflakeID + "(" + serverName + ")...");
                        event.getChx().writeAndFlush(new WrapperRegisterStaticServerPacket(name, snowflakeID));

                        Process process = processBuilder.start();
                        process.waitFor();
                    } catch (IOException | InterruptedException exception) {
                        exception.printStackTrace();
                        Logger.log(LoggerType.ERROR, "Unexpected error while starting Server » " + serverName + " occurred! Skipping...\n" +
                            "Please report this error.");
                    }
                });
            }
        }));
    }

    @Override
    public void updateCache() {

    }

    private void registerUncaughtExceptionListener(){
        String currentVersion = "-1";
        try {
            FileReader reader = new FileReader("launcher.json");
            currentVersion = new GsonBuilder().setPrettyPrinting().create().fromJson(reader, JsonObject.class).get("version").getAsString();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String finalCurrentVersion = currentVersion;
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> poloCloudClient.getExceptionReportService().reportException(throwable, "wrapper", finalCurrentVersion));
    }

    private void checkAndDeleteTmpFolder() {
        File tmpFile = new File("tmp");
        if (tmpFile.exists()) {
            try {
                FileUtils.forceDelete(tmpFile);
            } catch (IOException exception) {
                exception.printStackTrace();
                Logger.log(LoggerType.ERROR, "Unexpected error while deleting tmp Folder! Cloud may react abnormal!\n" +
                    "Please report this error.");
            }
        }
    }


    private void checkPoloCloudAPI(){
        File apiJarFile = new File("templates/PoloCloud-API.jar");

        if (!apiJarFile.getParentFile().exists()) {
            apiJarFile.getParentFile().mkdirs();
        }


        String currentVersion;
        boolean forceUpdate;
        if (apiJarFile.exists()) {
            forceUpdate = false;
            currentVersion = config.getApiVersion();
        } else {
            forceUpdate = true;
            currentVersion = "First download";
        }
        PoloCloudUpdater updater = new PoloCloudUpdater(devMode, currentVersion, "api", apiJarFile);

        if(forceUpdate){
            if(devMode){
                Logger.log(LoggerType.INFO, Logger.PREFIX + "[Updater] Downloading latest development build...");
                if (updater.download()) {
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "[Updater] Successfully downloaded latest development build!");
                } else {
                    Logger.log(LoggerType.ERROR, Logger.PREFIX + "[Updater] Couldn't download latest development build!");
                }
            }else{
                Logger.log(LoggerType.INFO, "[Updater] Force update was due to no version of the PoloCloud-API found activated. Downloading latest build...");
                if (updater.download()) {
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "[Updater] Successfully downloaded latest build!");
                } else {
                    Logger.log(LoggerType.ERROR, Logger.PREFIX + "[Updater] Couldn't download latest build!");
                }
            }
        }else if(devMode){
            Logger.log(LoggerType.INFO, Logger.PREFIX + "[Updater] Downloading latest development build...");
            if (updater.download()) {
                Logger.log(LoggerType.INFO, Logger.PREFIX + "[Updater] Successfully downloaded latest development build!");
            } else {
                Logger.log(LoggerType.ERROR, Logger.PREFIX + "[Updater] Couldn't download latest development build!");
            }
        }else{
            Logger.log(LoggerType.INFO, Logger.PREFIX + "[Updater] Searching for regular PoloCloud-API updates...");
            if (updater.check()) {
                Logger.log(LoggerType.INFO, Logger.PREFIX + "[Updater] Found a update! (" + currentVersion + " -> " + updater.getFetchedVersion() + " (Upload date: " + updater.getLastUpdate() + "))");
                Logger.log(LoggerType.INFO, Logger.PREFIX + "[Updater] downloading...");
                if (updater.download()) {
                    config.setApiVersion(updater.getFetchedVersion());
                    new SimpleConfigSaver().save(config, new File("config.json"));
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "[Updater] Successfully downloaded latest version! (" + updater.getFetchedVersion() + ")");
                } else {
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "[Updater] Couldn't download latest version!");
                }
            } else {
                Logger.log(LoggerType.INFO, Logger.PREFIX + "[Updater] You are running the latest version of the PoloCloud-API! (" + currentVersion + ")");
            }
        }

    }

    /**
     * Currently unused old method
     * Is used as backup if the new Updater is not started or not functional
     */
    private void checkPoloCloudAPINative() {
        Logger.log(LoggerType.INFO, "Checking PoloCloud-API Version...");
        File apiJarFile = new File("templates/PoloCloud-API.jar");

        if (!apiJarFile.getParentFile().exists()) {
            apiJarFile.getParentFile().mkdirs();
        }

        String baseUrl = "http://37.114.60.129:8870";
        String apiDownloadURL = baseUrl + "/updater/download/api";
        String apiVersionURL = baseUrl + "/updater/version/api";

        UpdateClient updateClient = new UpdateClient(apiDownloadURL, apiJarFile, apiVersionURL, config.getApiVersion());

        boolean download;
        String currentversion;
        if (apiJarFile.exists()) {
            download = updateClient.download(false);
            currentversion = config.getApiVersion();
        } else {
            download = updateClient.download(true);
            currentversion = "First download";
        }

        if (download) {
            Logger.log(LoggerType.INFO, "Found new PoloCloud-API Version! (" + currentversion + " -> " + updateClient.getFetchedVersion() + ") updating...");
            config.setApiVersion(updateClient.getFetchedVersion());
            IConfigSaver saver = new SimpleConfigSaver();
            saver.save(config, new File("config.json"));
            Logger.log(LoggerType.INFO, ConsoleColors.GREEN + "Successfully " + ConsoleColors.GRAY + "update PoloCloud-API! (" + config.getApiVersion() + ")");
        } else {
            Logger.log(LoggerType.INFO, "No update for PoloCloud-API found!");
        }

        while (!apiJarFile.exists()) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
                Logger.log(LoggerType.ERROR, "Unexpected error while checking PoloCloud-API Jar!\n" +
                    "Please report this error.");
            }
        }
    }

    public void checkNecessaryFolders(){
        File EVERY_PROXY = new File("templates/EVERY_PROXY");
        File EVERY_GAMESERVER = new File("templates/EVERY_GAMESERVER");
        if(!EVERY_GAMESERVER.exists()){
            EVERY_GAMESERVER.mkdirs();
        }
        if(!EVERY_PROXY.exists()){
            EVERY_PROXY.mkdirs();
        }
    }

    private WrapperConfig loadWrapperConfig() {

        File configFile = new File("config.json");
        IConfigLoader configLoader = new SimpleConfigLoader();

        WrapperConfig wrapperConfig = configLoader.load(WrapperConfig.class, configFile);

        IConfigSaver configSaver = new SimpleConfigSaver();
        configSaver.save(wrapperConfig, configFile);

        return wrapperConfig;
    }

    @Override
    public void start() {
        Logger.log(LoggerType.INFO, "Trying to start the wrapper...");

        this.nettyClient = getGuice().getInstance(SimpleNettyClient.class);
        new Thread(() -> this.nettyClient.start(nettyClient -> {
            processManager = new ProcessManager();

            Logger.log(LoggerType.INFO, "The Wrapper was " + ConsoleColors.GREEN + "successfully " + ConsoleColors.GRAY + "started.");
            Logger.log(LoggerType.INFO, "§7Trying to log in as §7'§3" + config.getWrapperName() + "§7'!");
            nettyClient.sendPacket(new WrapperLoginPacket(config.getWrapperName(), config.getLoginKey()));
            //this.nettyClient.registerListener(new SimpleWrapperNetworkListener(this.nettyClient.getProtocol()));
            nettyClient.getProtocol().registerPacketHandler(new MasterLoginResponsePacketHandler());
            nettyClient.getProtocol().registerPacketHandler(new MasterRequestServerStartListener(config, processManager));
            nettyClient.getProtocol().registerPacketHandler(new APIRequestGameServerCopyHandler());
            nettyClient.getProtocol().registerPacketHandler(new WrapperRequestShutdownHandler());
            nettyClient.getProtocol().registerPacketHandler(new MasterRequestsServerTerminatePacketHandler(processManager));
        })).start();

    }

    @Override
    public ITemplateService getTemplateService() {
        return null;
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
    }

    @Override
    public IGameServerManager getGameServerManager() {
        return null;
    }

    @Override
    public ICloudPlayerManager getCloudPlayerManager() {
        return null;
    }

    @Override
    public IPubSubManager getPubSubManager() {
        return null;
    }

    @Override
    public Injector getGuice() {
        return injector;
    }

    @Override
    public boolean terminate() {
        boolean terminate = this.nettyClient.terminate();
        for (Long snowflake : processManager.getProcessMap().keySet()) {
            processManager.terminateProcess(snowflake);
        }
        Scheduler.runtimeScheduler().schedule(() -> System.exit(0), 20L);
        return terminate;
    }

    @Override
    public IWrapperManager getWrapperManager() {
        return null;
    }

    public SimpleNettyClient getNettyClient() {
        return nettyClient;
    }

    public boolean isDevMode() {
        return devMode;
    }
}
