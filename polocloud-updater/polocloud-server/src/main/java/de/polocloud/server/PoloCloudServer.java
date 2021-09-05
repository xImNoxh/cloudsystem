package de.polocloud.server;

import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.server.config.PoloCloudServerConfig;
import de.polocloud.server.config.PoloCloudServerStatisticsConfig;
import de.polocloud.server.requests.RequestHandler;
import de.polocloud.server.shutdown.ShutdownHook;
import de.polocloud.server.threaded.ThreadProvider;
import io.javalin.Javalin;

import java.io.File;
import java.io.IOException;

public class PoloCloudServer {

    private static PoloCloudServer instance;

    private PoloCloudServerConfig config;

    private ThreadProvider threadProvider;

    private PoloCloudServerStatisticsConfig statisticsConfig;

    private Javalin javalin;

    private RequestHandler requestHandler;

    public PoloCloudServer() {
        System.out.println("Booting...");
        instance = this;

        System.out.println("Loading configs...");
        loadConfig();
        loadStatisticsConfig();
        File devs = new File("dev/");
        if (!devs.exists()) {
            devs.mkdirs();
        }

        File releases = new File("release/");
        if (!releases.exists()) {
            releases.mkdirs();
        }

        System.out.println("Registering instances...");
        new ShutdownHook().registerHook();

        threadProvider = new ThreadProvider();

        System.out.println("Setting up Javalin and the requesthandlers...");
        this.javalin = Javalin.create();
        this.requestHandler = new RequestHandler();
        requestHandler.getUpdateRequestHandler().registerDownloads();

        javalin.start(4542);
    }

    public static PoloCloudServer getInstance() {
        return instance;
    }

    public void loadConfig() {
        IConfigLoader configLoader = new SimpleConfigLoader();
        IConfigSaver configSaver = new SimpleConfigSaver();

        File file = new File("config.json");

        this.config = configLoader.load(PoloCloudServerConfig.class, file);

        try {
            File bootstrapChangelog = new File("changelog/bootstrap/" + config.getBoostrapVersion() + ".txt");
            File apiChangelog = new File("changelog/api/" + config.getApiVersion() + ".txt");
            apiChangelog.getParentFile().mkdirs();
            bootstrapChangelog.getParentFile().mkdirs();
            if (!bootstrapChangelog.exists()) {
                bootstrapChangelog.createNewFile();
            }
            if (!apiChangelog.exists()) {
                apiChangelog.createNewFile();
            }
        } catch (IOException ignored) {
        }
        configSaver.save(config, file);
    }

    public void loadStatisticsConfig() {
        IConfigLoader configLoader = new SimpleConfigLoader();
        IConfigSaver configSaver = new SimpleConfigSaver();

        File file = new File("statistics.json");

        this.statisticsConfig = configLoader.load(PoloCloudServerStatisticsConfig.class, file);
        configSaver.save(this.statisticsConfig, file);
    }

    public void shutdown() {
        System.out.println("Stopping...");
        this.threadProvider.getRefreshThread().destroyRefreshThread();
        this.javalin.stop();
    }

    public PoloCloudServerConfig getConfig() {
        return config;
    }

    public ThreadProvider getThreadProvider() {
        return threadProvider;
    }

    public PoloCloudServerStatisticsConfig getStatisticsConfig() {
        return statisticsConfig;
    }

    public Javalin getJavalin() {
        return javalin;
    }

    public RequestHandler getRequestHandler() {
        return requestHandler;
    }
}
