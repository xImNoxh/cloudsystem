package de.polocloud.wrapper;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.event.channel.ChannelActiveEvent;
import de.polocloud.api.event.EventHandler;
import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.guice.PoloAPIGuiceModule;
import de.polocloud.api.network.IStartable;
import de.polocloud.api.network.ITerminatable;
import de.polocloud.api.network.client.SimpleNettyClient;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperLoginPacket;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperRegisterStaticServerPacket;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;
import de.polocloud.updater.UpdateClient;
import de.polocloud.wrapper.commands.StopCommand;
import de.polocloud.wrapper.config.WrapperConfig;
import de.polocloud.wrapper.guice.WrapperGuiceModule;
import de.polocloud.wrapper.network.handler.MasterLoginResponsePacketHandler;
import de.polocloud.wrapper.network.handler.MasterRequestServerStartListener;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Wrapper implements IStartable, ITerminatable {

    private CloudAPI cloudAPI;

    private SimpleNettyClient nettyClient;

    private WrapperConfig config;

    public Wrapper() {

        config = loadWrapperConfig();

        requestStaticServersStart();

        checkAndDeleteTmpFolder();

        checkPoloCloudAPI();

        String[] masterAddress = config.getMasterAddress().split(":");
        this.cloudAPI = new PoloCloudAPI(new PoloAPIGuiceModule(), new WrapperGuiceModule(masterAddress[0], Integer.parseInt(masterAddress[1])));

        CloudAPI.getInstance().getCommandPool().registerCommand(new StopCommand());
    }

    private void requestStaticServersStart(){
        Executor executor = Executors.newCachedThreadPool();

        EventRegistry.registerListener((EventHandler<ChannelActiveEvent>) event -> executor.execute(() ->{
            for (String staticServer : config.getStaticServers()) {
                executor.execute(() ->{
                    String[] properties = staticServer.split(",");
                    String serverName = properties[0];
                    int port = Integer.parseInt(properties[1]);
                    int processMemory = Integer.parseInt(properties[2]);
                    Logger.log(LoggerType.INFO, "Starting static server » " + serverName + " on port » " + port + "...");
                    ProcessBuilder processBuilder = new ProcessBuilder(("java -jar -Xms" + processMemory + "M -Xmx" + processMemory + "M -Dcom.mojang.eula.agree=true spigot.jar nogui --online-mode false --max-players " + 100 + " --noconsole --port " + port).split(" "));
                    try{
                        processBuilder.directory(new File("static/" + serverName));

                        String name = serverName.split("#")[0];
                        long snowflakeID = Long.parseLong(serverName.split("#")[1]);

                        Logger.log(LoggerType.INFO, "Starting static server with " + name + "/" + snowflakeID + "(" + serverName + ")...");
                        event.getChx().writeAndFlush(new WrapperRegisterStaticServerPacket(name, snowflakeID));

                        Process process = processBuilder.start();
                        process.waitFor();
                    }catch (IOException | InterruptedException exception){
                        exception.printStackTrace();
                        Logger.log(LoggerType.ERROR, "Unexpected error while starting Server » " + serverName + " occured! Skipping...\n" +
                            "Please report this error.");
                    }
                });
            }
        }), ChannelActiveEvent.class);
    }

    private void checkAndDeleteTmpFolder(){
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

        String baseUrl = "http://37.114.60.129:8870";
        String apiDownloadURL = baseUrl + "/updater/download/api";
        String apiVersionURL = baseUrl + "/updater/version/api";

        UpdateClient updateClient = new UpdateClient(apiDownloadURL, apiJarFile, apiVersionURL, config.getApiVersion());
        boolean download = updateClient.download(true);
        if (download) {
            Logger.log(LoggerType.INFO, "Found new PoloCloud-API Version! (" + config.getApiVersion() + " -> " + updateClient.getFetchedVersion() + ") updating...");
            config.setApiVersion(updateClient.getFetchedVersion());
            IConfigSaver saver = new SimpleConfigSaver();
            saver.save(config, new File("config.json"));
            Logger.log(LoggerType.INFO, ConsoleColors.GREEN.getAnsiCode() + "Successfully " + ConsoleColors.GRAY.getAnsiCode() + "update PoloCloud-API! (" + config.getApiVersion() + ")");
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

        this.nettyClient = this.cloudAPI.getGuice().getInstance(SimpleNettyClient.class);
        new Thread(() -> {
            this.nettyClient.start();
        }).start();

        Logger.log(LoggerType.INFO, "The Wrapper was " + ConsoleColors.GREEN.getAnsiCode() + "successfully " + ConsoleColors.GRAY.getAnsiCode() + "started.");
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
        this.nettyClient.getProtocol().registerPacketHandler(new MasterRequestServerStartListener(config));
    }

    @Override
    public boolean terminate() {
        return this.nettyClient.terminate();
    }
}

