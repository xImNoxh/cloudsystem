package de.polocloud.wrapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.guice.PoloAPIGuiceModule;
import de.polocloud.api.network.IStartable;
import de.polocloud.api.network.ITerminatable;
import de.polocloud.api.network.client.SimpleNettyClient;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperLoginPacket;
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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class Wrapper implements IStartable, ITerminatable {

    private CloudAPI cloudAPI;

    private SimpleNettyClient nettyClient;

    private WrapperConfig config;

    public Wrapper() {

        config = loadConfig();


        File tmpFile = new File("tmp");
        File apiFile = new File("templates/PoloCloud-API.jar");
        if (tmpFile.exists()) {
            try {
                FileUtils.forceDelete(tmpFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!apiFile.getParentFile().exists()) {
            apiFile.getParentFile().mkdirs();
        }
        String baseUrl = "http://37.114.60.129:8870";
        String downloadUrl = baseUrl + "/updater/download/api";
        String versionUrl = baseUrl + "/updater/version/api";


        UpdateClient updateClient = new UpdateClient(downloadUrl, apiFile, versionUrl, config.getApiVersion());
        boolean download = updateClient.download(true);
        if (download) {
            config.setApiVersion(updateClient.getFetchedVersion());
            IConfigSaver saver = new SimpleConfigSaver();
            saver.save(config, new File("config.json"));
            Logger.log(LoggerType.INFO, "updated API to version " + config.getApiVersion());
        }

        while (!apiFile.exists()) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        String[] masterAddress = config.getMasterAddress().split(":");
        this.cloudAPI = new PoloCloudAPI(new PoloAPIGuiceModule(), new WrapperGuiceModule(masterAddress[0], Integer.parseInt(masterAddress[1])));


        CloudAPI.getInstance().getCommandPool().registerCommand(new StopCommand());
    }


    private WrapperConfig loadConfig() {

        File configFile = new File("config.json");
        IConfigLoader configLoader = new SimpleConfigLoader();

        WrapperConfig wrapperConfig = configLoader.load(WrapperConfig.class, configFile);

        IConfigSaver configSaver = new SimpleConfigSaver();
        configSaver.save(wrapperConfig, configFile);

        return wrapperConfig;
    }

    @Override
    public void start() {

        Logger.log(LoggerType.INFO, "Trying to start wrapper...");

        this.nettyClient = this.cloudAPI.getGuice().getInstance(SimpleNettyClient.class);
        new Thread(() -> {
            this.nettyClient.start();

        }).start();

        Logger.log(LoggerType.INFO, "The Wrapper " + ConsoleColors.GREEN.getAnsiCode() + "successfully " + ConsoleColors.GRAY.getAnsiCode() + "started.");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
