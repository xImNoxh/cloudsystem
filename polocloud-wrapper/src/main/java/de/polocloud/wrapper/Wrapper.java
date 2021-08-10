package de.polocloud.wrapper;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.commands.CommandPool;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.commands.ICommandPool;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.event.EventHandler;
import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.event.IEventHandler;
import de.polocloud.api.event.channel.ChannelActiveEvent;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.guice.PoloAPIGuiceModule;
import de.polocloud.api.network.IStartable;
import de.polocloud.api.network.ITerminatable;
import de.polocloud.api.network.client.SimpleNettyClient;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperLoginPacket;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperRegisterStaticServerPacket;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.pubsub.IPubSubManager;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;
import de.polocloud.updater.UpdateClient;
import de.polocloud.wrapper.commands.StopCommand;
import de.polocloud.wrapper.config.WrapperConfig;
import de.polocloud.wrapper.guice.WrapperGuiceModule;
import de.polocloud.wrapper.network.handler.*;
import de.polocloud.wrapper.process.ProcessManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Wrapper extends PoloCloudAPI implements IStartable, ITerminatable {

    private static Wrapper instance;

    private ICommandPool commandPool;

    private SimpleNettyClient nettyClient;
    private Injector injector;
    private WrapperConfig config;

    private ProcessManager processManager;

    public Wrapper() {
        instance = this;
        PoloCloudAPI.setInstance(this);

        commandPool = new CommandPool();
        config = loadWrapperConfig();

        requestStaticServersStart();

        checkAndDeleteTmpFolder();

        checkPoloCloudAPI();

        String[] masterAddress = config.getMasterAddress().split(":");
        injector = Guice.createInjector(new PoloAPIGuiceModule(), new WrapperGuiceModule(masterAddress[0], Integer.parseInt(masterAddress[1])));

        getCommandPool().registerCommand(new StopCommand());
    }

    public static Wrapper getInstance() {
        return instance;
    }

    private void requestStaticServersStart() {
        Executor executor = Executors.newCachedThreadPool();

        EventRegistry.registerListener((EventHandler<ChannelActiveEvent>) event -> executor.execute(() -> {
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
        }), ChannelActiveEvent.class);
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

    private void checkPoloCloudAPI() {
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
        new Thread(() -> {
            this.nettyClient.start();
        }).start();

        this.processManager = new ProcessManager();

        Logger.log(LoggerType.INFO, "The Wrapper was " + ConsoleColors.GREEN + "successfully " + ConsoleColors.GRAY + "started.");
        try {
            Thread.sleep(500);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
            Logger.log(LoggerType.ERROR, "Unexpected error while waiting for the NettyClient!\n" +
                "Please report this error.");
        }
        this.nettyClient.sendPacket(new WrapperLoginPacket(config.getWrapperName(), config.getLoginKey()));
        //this.nettyClient.registerListener(new SimpleWrapperNetworkListener(this.nettyClient.getProtocol()));

        this.nettyClient.getProtocol().registerPacketHandler(new MasterLoginResponsePacketHandler());
        this.nettyClient.getProtocol().registerPacketHandler(new MasterRequestServerStartListener(config, processManager));
        this.nettyClient.getProtocol().registerPacketHandler(new APIRequestGameServerCopyHandler());
        this.nettyClient.getProtocol().registerPacketHandler(new WrapperRequestShutdownHandler());
        this.nettyClient.getProtocol().registerPacketHandler(new MasterRequestsServerTerminatePacketHandler(processManager));
    }

    @Override
    public ITemplateService getTemplateService() {
        return null;
    }

    @Override
    public ICommandExecutor getCommandExecutor() {
        return null;
    }

    @Override
    public ICommandPool getCommandPool() {
        return commandPool;
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
    public IConfigLoader getConfigLoader() {
        return null;
    }

    @Override
    public IConfigSaver getConfigSaver() {
        return null;
    }

    @Override
    public IPubSubManager getPubSubManager() {
        return null;
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
    public Injector getGuice() {
        return injector;
    }

    @Override
    public boolean terminate() {
        return this.nettyClient.terminate();
    }

    public SimpleNettyClient getNettyClient() {
        return nettyClient;
    }
}

